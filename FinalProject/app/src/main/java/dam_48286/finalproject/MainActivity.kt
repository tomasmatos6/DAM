package dam_48286.finalproject

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private var backPressedOnce = false
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createNotificationChannel()
        AppCompatDelegate.setDefaultNightMode(getSavedThemeMode())

        // Check if user is logged in
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            // User is logged in, navigate to PlanList activity
            val intent = Intent(this, PlanList::class.java)
            startActivity(intent)
            finish()
        } else {
            // User is not logged in, show the login fragment
            val fragmentManager: FragmentManager = supportFragmentManager
            val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.loginFragmentContainer, Login())
            fragmentTransaction.commit()
        }

        if(ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED
            ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                POST_NOTIFICATION_REQUEST_CODE
            )
        }
    }

    private fun getSavedThemeMode(): Int {
        val sharedPref = getSharedPreferences("ThemePreferences", MODE_PRIVATE)
        return sharedPref.getInt("theme_mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }

    override fun onBackPressed() {
        if (backPressedOnce) {
            super.onBackPressed()
            finishAffinity()
            return
        }

        this.backPressedOnce = true
        Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show()

        // Reset flag after 2 seconds
        Handler().postDelayed({ backPressedOnce = false }, 2000)
    }

    private fun createNotificationChannel() {
        val name = getString(R.string.channel_name)
        val descriptionText = getString(R.string.channel_description)
        val importance = NotificationManager.IMPORTANCE_HIGH // Set importance to HIGH
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    companion object {
        const val CHANNEL_ID = "medicine_reminder_channel"
        private const val POST_NOTIFICATION_REQUEST_CODE = 1001
    }
}