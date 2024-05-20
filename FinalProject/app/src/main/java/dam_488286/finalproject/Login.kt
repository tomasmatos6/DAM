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
 * Use the [Login.newInstance] factory method to
 * create an instance of this fragment.
 */
class Login : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        signUpText(view)

        buttonListener(view)

        return view
    }

    private fun signUpText(view: View) {
        // Make "Sign in." text clickable and change fragment
        val signUpText = "Don't have an account? Sign up."
        val spannableString = SpannableString(signUpText)
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                //findNavController().navigate(R.id.registerFragment)

                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                transaction.replace(R.id.loginFragmentContainer, Register())
                transaction.addToBackStack(null)
                transaction.commit()
            }
        }
        spannableString.setSpan(clickableSpan, signUpText.indexOf("Sign up."), signUpText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        val textViewSignUp = view.findViewById<TextView>(R.id.textViewSignUp)
        textViewSignUp.text = spannableString
        textViewSignUp.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun buttonListener(view: View) {
        // Find the sign-up button
        val buttonSignUp = view.findViewById<Button>(R.id.buttonLogin)

        // Set click listener for the sign-up button
        buttonSignUp.setOnClickListener {
            // Navigate to PlanList activity
            val intent = Intent(requireContext(), PlanList::class.java)
            startActivity(intent)
        }
    }
}