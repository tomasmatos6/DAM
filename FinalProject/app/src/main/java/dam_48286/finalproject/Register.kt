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

class Register : Fragment() {
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
        val view = inflater.inflate(R.layout.fragment_register, container, false)

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
                        Log.e("RegisterFragment", "Couldn't start One Tap UI: ${e.localizedMessage}")
                    }
                }
                .addOnFailureListener(requireActivity()) { e ->
                    Log.d("RegisterFragment", e.localizedMessage)
                }
        }

        val emailEditText: EditText = view.findViewById(R.id.editTextEmail)
        val passwordEditText: EditText = view.findViewById(R.id.editTextPassword)
        val nameEditText: EditText = view.findViewById(R.id.editTextUsername)
        val registerButton: Button = view.findViewById(R.id.buttonRegister)

        // Find the "Sign In" text view
        val signInTextView: TextView = view.findViewById(R.id.textViewSignIn)

        // Set click listener for "Sign In" text view
        signInTextView.setOnClickListener {
            // Replace the register fragment with the login fragment
            val fragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.loginFragmentContainer, Login())
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        registerButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val name = nameEditText.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty() && name.isNotEmpty()) {
                registerUser(email, password, name)
            } else {
                Toast.makeText(context, "Please fill out all fields", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun registerUser(email: String, password: String, name: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.let { currentUser ->
                        Log.d("UID", currentUser.uid)
                        val newUser = User(name, email)
                        db.collection("Users").document(currentUser.uid).set(newUser)
                            .addOnSuccessListener {
                                val intent = Intent(activity, PlanList::class.java)
                                intent.putExtra("currentUser", newUser)
                                startActivity(intent)
                                activity?.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                                activity?.finish()
                            }
                            .addOnFailureListener { exception ->
                                Log.w("RegisterFragment", "Error adding user", exception)
                                Toast.makeText(context, "Error creating user", Toast.LENGTH_SHORT).show()
                            }
                    }
                } else {
                    Log.w("RegisterFragment", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(context, "Please provide a valid email", Toast.LENGTH_SHORT).show()
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
                                    Log.w("RegisterFragment", "signInWithCredential:failure", task.exception)
                                    Toast.makeText(context, "Authentication failed", Toast.LENGTH_SHORT).show()
                                }
                            }
                    }
                    else -> {
                        Log.d("RegisterFragment", "No ID token!")
                    }
                }
            } catch (e: ApiException) {
                Log.e("RegisterFragment", "signInResult:failed code=${e.statusCode}")
            }
        }
    }

    private fun checkIfUserExistsAndProceed(uid: String, email: String?, name: String?) {
        db.collection("Users").document(uid)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val user = document.toObject(User::class.java)
                    user?.let {
                        val intent = Intent(activity, PlanList::class.java)
                        intent.putExtra("currentUser", it)
                        startActivity(intent)
                        activity?.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                        activity?.finish()
                    }
                } else {
                    val newUser = User(name ?: "", email ?: "")
                    db.collection("Users").document(uid).set(newUser)
                        .addOnSuccessListener {
                            val intent = Intent(activity, PlanList::class.java)
                            intent.putExtra("currentUser", newUser)
                            startActivity(intent)
                            activity?.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                            activity?.finish()
                        }
                        .addOnFailureListener { exception ->
                            Log.w("RegisterFragment", "Error adding user", exception)
                            Toast.makeText(context, "Error creating user", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener { exception ->
                Log.d("RegisterFragment", "get failed with ", exception)
                Toast.makeText(context, "Failed to get user data", Toast.LENGTH_SHORT).show()
            }
    }
}
