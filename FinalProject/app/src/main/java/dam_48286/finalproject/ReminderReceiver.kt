package dam_48286.finalproject

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.firestore.FirebaseFirestore
import dam_48286.finalproject.MainActivity.Companion.CHANNEL_ID
import java.time.LocalDateTime
import java.time.ZoneId

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationIntent = Intent(context, MedicineList::class.java)
        val requestCode = intent.getIntExtra("requestCode", 0)
        val medicineId = intent.getStringExtra("medicineId")
        val medicine = intent.getParcelableExtra<Medicine>("medicine")
        Log.d("Alarm request code:", requestCode.toString())

        val pendingIntent = PendingIntent.getActivity(
            context,
            requestCode,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle("Medicine Reminder")
            .setContentText("It's time to take your ${medicine?.name}.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1, notification)

        // Turn off the reminder
        if (medicineId != null) {
            turnOffReminder(medicineId)
        }
    }

    private fun turnOffReminder(medicineId: String) {
        val db = FirebaseFirestore.getInstance()
        val medicineRef = db.collection("Medicines").document(medicineId)

        medicineRef.get().addOnSuccessListener { document ->
            if (document != null) {
                val reminder = document.get("reminder") as? Map<String, Any>
                if (reminder != null) {
                    val updatedReminder = reminder.toMutableMap()
                    if(updatedReminder["date"] != null) {
                        updatedReminder["enabled"] = false
                        medicineRef.update("reminder", updatedReminder)
                    }
                }
            }
        }.addOnFailureListener { exception ->
            Log.w("ReminderReceiver", "Error updating document", exception)
        }
    }
}
