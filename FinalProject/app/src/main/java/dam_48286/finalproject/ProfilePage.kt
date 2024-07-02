package dam_48286.finalproject

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ProfilePage : AppCompatActivity() {

    private lateinit var profilePhotoImageView: ImageView
    private lateinit var usernameTextView: TextView
    private lateinit var editProfileButton: Button
    private lateinit var logoutButton: Button

    private val auth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_page)

        // Initialize views
        profilePhotoImageView = findViewById(R.id.profilePhotoImageView)
        usernameTextView = findViewById(R.id.usernameTextView)
        editProfileButton = findViewById(R.id.editProfileButton)
        logoutButton = findViewById(R.id.logoutButton)

        val currentUser = auth.currentUser
        currentUser?.let { user ->
            val userId = user.uid

            db.collection("Users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val userName = document.getString("name")
                        val photoUrl = document.getString("image")

                        // Set user name
                        usernameTextView.text = userName

                        // Load profile photo
                        if (!photoUrl.isNullOrEmpty()) {
                            Glide.with(this@ProfilePage)
                                .load(photoUrl)
                                .placeholder(R.drawable.profile_icon) // Default profile icon
                                .error(R.drawable.profile_icon) // Error placeholder if loading fails
                                .into(profilePhotoImageView)
                        } else {
                            // Use default profile icon if no photoUrl is available
                            profilePhotoImageView.setImageResource(R.drawable.profile_icon)
                        }
                    } else {
                        // Document doesn't exist
                    }
                }
                .addOnFailureListener { e ->
                    // Handle failure
                }
        }
        // Button click listener to edit profile
        editProfileButton.setOnClickListener {
            startActivity(Intent(this@ProfilePage, EditProfile::class.java))
            finish()
        }

        // Set click listener for the logout button
        logoutButton.setOnClickListener {
            logoutUser()
        }
    }

    private fun logoutUser() {
        auth.signOut()
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }
}
