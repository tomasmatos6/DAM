package dam_48286.finalproject

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class EditProfile : AppCompatActivity() {

    private lateinit var editProfilePhotoImageView: ImageView
    private lateinit var newUsernameEditText: EditText
    private lateinit var cancelButton: Button
    private lateinit var saveButton: Button


    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_profile)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize views
        editProfilePhotoImageView = findViewById(R.id.editProfilePhotoImageView)
        newUsernameEditText = findViewById(R.id.newUsernameEditText)
        cancelButton = findViewById(R.id.cancelButton)
        saveButton = findViewById(R.id.saveButton)

        // Set click listener for the profile photo
        editProfilePhotoImageView.setOnClickListener {
            openImagePicker()
        }

        // Set click listener for the cancel button
        cancelButton.setOnClickListener {
            finish() // Go back to the ProfilePage activity
        }

        // Set click listener for the save button
        saveButton.setOnClickListener {
            saveProfileChanges()
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                selectedImageUri = uri
                Glide.with(this).load(uri).into(editProfilePhotoImageView)
            }
        }
    }

    private fun saveProfileChanges() {
        val newUsername = newUsernameEditText.text.toString().trim()
        val currentUser = auth.currentUser

        if (currentUser != null) {
            val userId = currentUser.uid
            val userDocRef = db.collection("Users").document(userId)

            if (selectedImageUri != null) {
                // Save image to Firebase Storage
                val imageRef = storage.reference.child("profile_images/$userId.jpg")
                imageRef.putFile(selectedImageUri!!).addOnSuccessListener {
                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        val photoUrl = uri.toString()
                        updateUserProfile(userDocRef, newUsername, photoUrl)
                    }
                }.addOnFailureListener {
                    Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
                }
            } else {
                updateUserProfile(userDocRef, newUsername, null)
            }
        }
    }

    private fun updateUserProfile(userDocRef: DocumentReference, newUsername: String, photoUrl: String?) {
        val updates = mutableMapOf<String, Any>()
        if (newUsername.isNotEmpty()) {
            updates["name"] = newUsername
        }
        if (photoUrl != null) {
            updates["image"] = photoUrl
        }

        userDocRef.update(updates).addOnSuccessListener {
            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, ProfilePage::class.java)
            startActivity(intent)
            finish()
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show()
        }
    }
    companion object {
        private const val REQUEST_CODE_PICK_IMAGE = 1001
    }
}
