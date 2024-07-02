package dam_48286.finalproject

import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

/**
 * A simple [Fragment] subclass.
 * Use the [Login.newInstance] factory method to
 * create an instance of this fragment.
 */
class Login : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest
    private val RC_SIGN_IN = 9001
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()


        oneTapClient = Identity.getSignInClient(requireActivity())
        signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(getString(R.string.client_id))
                    .setFilterByAuthorizedAccounts(false) // Show all accounts
                    .build()
            )
            .build()

        val googleSignInButton: SignInButton = view.findViewById(R.id.google)
        googleSignInButton.setOnClickListener {
            oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(requireActivity()) { result ->
                    try {
                        startIntentSenderForResult(
                            result.pendingIntent.intentSender, RC_SIGN_IN,
                            null, 0, 0, 0, null
                        )
                    } catch (e: IntentSender.SendIntentException) {
                        Log.e("LoginFragment", "Couldn't start One Tap UI: ${e.localizedMessage}")
                    }
                }
                .addOnFailureListener(requireActivity()) { e ->
                    Log.d("LoginFragment", e.localizedMessage)
                }
        }

        // Find views
        val emailEditText: EditText = view.findViewById(R.id.editTextEmail)
        val passwordEditText: EditText = view.findViewById(R.id.editTextPassword)
        val loginButton: Button = view.findViewById(R.id.buttonLogin)

        // Find the "Sign Up" text view
        val signUpTextView: TextView = view.findViewById(R.id.textViewSignUp)

        // Set click listener for "Sign Up" text view
        signUpTextView.setOnClickListener {
            // Replace the login fragment with the signup fragment
            val fragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.loginFragmentContainer, Register())
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        // Set click listener for login button
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                login(email, password)
            } else {
                Toast.makeText(context, "Please fill out all fields", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, navigate to main activity
                    val user = auth.currentUser
                    user?.let { currentUser ->
                        Log.d("UID", currentUser.uid)
                        getUserData(currentUser.uid)
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("LoginFragment", "signInWithEmail:failure", task.exception)
                    Toast.makeText(context, "Authentication failed", Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == RC_SIGN_IN) {
            try {
                val credential = oneTapClient.getSignInCredentialFromIntent(data)
                val idToken = credential.googleIdToken
                when {
                    idToken != null -> {
                        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                        auth.signInWithCredential(firebaseCredential)
                            .addOnCompleteListener(requireActivity()) { task ->
                                if (task.isSuccessful) {
                                    val user = auth.currentUser
                                    user?.let {
                                        checkIfUserExistsAndProceed(it.uid, it.email, it.displayName)
                                    }
                                } else {
                                    Log.w("LoginFragment", "signInWithCredential:failure", task.exception)
                                    Toast.makeText(context, "Authentication failed", Toast.LENGTH_SHORT).show()
                                }
                            }
                    }
                    else -> {
                        Log.d("LoginFragment", "No ID token!")
                    }
                }
            } catch (e: ApiException) {
                Log.e("LoginFragment", "signInResult:failed code=${e.statusCode}")
            }
        }
    }

    private fun checkIfUserExistsAndProceed(uid: String, email: String?, name: String?) {
        db.collection("Users").document(uid)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    // User exists, proceed to main activity
                    val user = document.toObject(User::class.java)
                    user?.let {
                        val intent = Intent(activity, PlanList::class.java)
                        intent.putExtra("currentUser", it)
                        startActivity(intent)
                        activity?.finish()
                    }
                } else {
                    // User does not exist, create new user
                    val newUser = User(name ?: "", email ?: "")
                    db.collection("Users").document(uid).set(newUser)
                        .addOnSuccessListener {
                            // Proceed to main activity with new user
                            val intent = Intent(activity, PlanList::class.java)
                            intent.putExtra("currentUser", newUser)
                            startActivity(intent)
                            activity?.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                            activity?.finish()
                        }
                        .addOnFailureListener { exception ->
                            Log.w("LoginFragment", "Error adding user", exception)
                            Toast.makeText(context, "Error creating user", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener { exception ->
                Log.d("LoginFragment", "get failed with ", exception)
                Toast.makeText(context, "Failed to get user data", Toast.LENGTH_SHORT).show()
            }
    }

    private fun getUserData(uid: String) {
        db.collection("Users").document(uid)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val user = document.toObject(User::class.java)
                    user?.let {
                        // Navigate to main activity with the user instance
                        val intent = Intent(activity, PlanList::class.java)
                        intent.putExtra("currentUser", it)
                        startActivity(intent)
                        activity?.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                        activity?.finish()
                    }
                } else {
                    Log.d("LoginFragment", "No such user")
                    Toast.makeText(context, "User not found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Log.d("LoginFragment", "get failed with ", exception)
                Toast.makeText(context, "Failed to get user data", Toast.LENGTH_SHORT).show()
            }
    }
}