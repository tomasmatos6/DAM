package dam_48286.finalproject

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Settings : AppCompatActivity() {

    private lateinit var themeButton: Button
    private lateinit var helpButton: Button
    private lateinit var aboutButton: Button
    private lateinit var premiumButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        themeButton = findViewById(R.id.themeButton)
        helpButton = findViewById(R.id.helpButton)
        aboutButton = findViewById(R.id.aboutButton)
        premiumButton = findViewById(R.id.premiumButton)

        // Set click listeners for the buttons
        themeButton.setOnClickListener {
            //startActivity(Intent(this, Theme::class.java))
            showThemePopup()
        }

        helpButton.setOnClickListener {
            startActivity(Intent(this, Help::class.java))
        }

        aboutButton.setOnClickListener {
            startActivity(Intent(this, About::class.java))
        }

        premiumButton.setOnClickListener {
            showPremiumPopup()
        }

        // Check if the user is premium
        checkUserPremiumStatus()
    }

    private fun checkUserPremiumStatus() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val db = FirebaseFirestore.getInstance()
            val userRef = db.collection("Users").document(userId)

            userRef.get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val userType = document.getString("type")
                        if (userType == "premium") {
                            premiumButton.visibility = View.GONE
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to check user status: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showPremiumPopup() {
        val dialogBuilder = androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Upgrade to Premium")
            .setMessage("Would you like to upgrade to premium?")
            .setPositiveButton("Yes") { dialog, _ ->
                // Handle the upgrade process
                upgradeToPremium()
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
        dialogBuilder.create().show()
    }

    private fun upgradeToPremium() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val db = FirebaseFirestore.getInstance()
            val userRef = db.collection("Users").document(userId)

            userRef.update("type", "premium")
                .addOnSuccessListener {
                    Toast.makeText(this, "You have been upgraded to Premium!", Toast.LENGTH_SHORT).show()
                    premiumButton.visibility = View.GONE
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to upgrade to Premium: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showThemePopup() {
        // Inflating the dialog layout
        val dialogView = LayoutInflater.from(this).inflate(R.layout.theme_dialog, null)

        // Creating AlertDialog Builder
        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Choose Theme")

        // Creating AlertDialog object
        val alertDialog = dialogBuilder.create()

        // Setting click listeners for buttons in the dialog
        dialogView.findViewById<Button>(R.id.lightModeButton).setOnClickListener {
            // Apply Light Mode theme
            setThemeAndRestart(AppCompatDelegate.MODE_NIGHT_NO)
            alertDialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.darkModeButton).setOnClickListener {
            // Apply Dark Mode theme
            setThemeAndRestart(AppCompatDelegate.MODE_NIGHT_YES)
            alertDialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.systemSettingsButton).setOnClickListener {
            // Apply System default theme
            setThemeAndRestart(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            alertDialog.dismiss()
        }

        // Showing the dialog
        alertDialog.show()
    }

    private fun setThemeAndRestart(mode: Int) {
        AppCompatDelegate.setDefaultNightMode(mode)
        recreate()
        val sharedPref = getSharedPreferences("ThemePreferences", MODE_PRIVATE)
        with(sharedPref.edit()) {
            putInt("theme_mode", mode)
            apply()
        }
        Log.d("Shared Pref", sharedPref.getInt("theme_mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM).toString())
    }
}
