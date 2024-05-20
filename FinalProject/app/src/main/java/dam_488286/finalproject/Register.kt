package dam_488286.finalproject

import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView

/**
 * A simple [Fragment] subclass.
 * Use the [Register.newInstance] factory method to
 * create an instance of this fragment.
 */
class Register : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_register, container, false)

        // Make "Sign in" text clickable and change fragment
        val signInText = "Already have an account? Sign in."
        val spannableString = SpannableString(signInText)
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                // Handle click event here
                // For example, navigate to another fragment
                // Replace FragmentTwo() with the fragment you want to navigate to
                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                transaction.replace(R.id.loginFragmentContainer, Login())
                transaction.addToBackStack(null)
                transaction.commit()
            }
        }
        spannableString.setSpan(clickableSpan, signInText.indexOf("Sign in"), signInText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        val textViewSignIn = view.findViewById<TextView>(R.id.textViewSignIn)
        textViewSignIn.text = spannableString
        textViewSignIn.movementMethod = LinkMovementMethod.getInstance()

        buttonListener(view)

        return view
    }

    private fun buttonListener(view: View) {
        // Find the sign-up button
        val buttonSignUp = view.findViewById<Button>(R.id.buttonRegister)

        // Set click listener for the sign-up button
        buttonSignUp.setOnClickListener {
            // Navigate to PlanList activity
            val intent = Intent(requireContext(), PlanList::class.java)
            startActivity(intent)
        }
    }
}