package dam_48286.finalproject

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Toolbar : Fragment() {
    private lateinit var backButton: ImageView
    private lateinit var profileButton: ImageView
    private lateinit var auth: FirebaseAuth
    private lateinit var settingsButton: ImageView
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_toolbar, container, false)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        backButton = view.findViewById(R.id.backButton)
        profileButton = view.findViewById(R.id.profileButton)
        settingsButton = view.findViewById(R.id.settingsButton)

        val activityName = activity?.javaClass?.simpleName

        when (activityName) {
            "PlanList" -> {
                backButton.visibility = View.GONE
            }
            "ProfilePage" -> {
                backButton.visibility = View.VISIBLE
                backButton.setOnClickListener {
                    activity?.finish()
                }
                profileButton.visibility = View.GONE
                settingsButton.visibility = View.VISIBLE
            }
            "Help", "About" -> {
                backButton.visibility = View.VISIBLE
                backButton.setOnClickListener {
                    activity?.finish()
                }
                profileButton.visibility = View.GONE
                settingsButton.visibility = View.GONE
            }
            "Settings" -> {
                backButton.visibility = View.VISIBLE
                backButton.setOnClickListener {
                    activity?.finish()
                }
                profileButton.visibility = View.GONE
                settingsButton.visibility = View.GONE
            }
            else -> {
                backButton.visibility = View.VISIBLE
                backButton.setOnClickListener {
                    activity?.finish()
                }
                profileButton.visibility = View.VISIBLE
                settingsButton.visibility = View.GONE
            }
        }

        profileButton.setOnClickListener {
            val intent = Intent(activity, ProfilePage::class.java)
            startActivity(intent)
        }

        settingsButton.setOnClickListener {
            val intent = Intent(activity, Settings::class.java)
            startActivity(intent)
        }

        return view
    }

    private fun loadUserProfileImage() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val userRef = firestore.collection("Users").document(userId)
            userRef.get().addOnSuccessListener { document ->
                if (document != null && document.contains("image")) {
                    val profileImageUrl = document.getString("image")
                    if (!profileImageUrl.isNullOrEmpty()) {
                        Glide.with(this)
                            .load(profileImageUrl)
                            .placeholder(R.drawable.profile_icon) // Placeholder image
                            .transform(CircleCrop())
                            .into(profileButton)
                    }
                }
            }.addOnFailureListener { e ->
                // Handle error
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadUserProfileImage()
    }
}
