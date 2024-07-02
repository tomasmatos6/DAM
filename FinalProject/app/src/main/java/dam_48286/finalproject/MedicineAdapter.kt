package dam_48286.finalproject

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.firebase.firestore.FirebaseFirestore
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class MedicineAdapter(
    private val medicines: MutableList<Medicine>,
    private val listener: OnMedicineInteractionListener
) : RecyclerView.Adapter<MedicineAdapter.MedicineViewHolder>() {

    inner class MedicineViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.nameTextView)
        val daysTextView: TextView = view.findViewById(R.id.daysTextView)
        val timeTextView: TextView = view.findViewById(R.id.timeTextView)
        val switchButton: SwitchMaterial = view.findViewById(R.id.switchButton)
        val dividerTextView: TextView = itemView.findViewById(R.id.dividerTextView)
    }

    interface OnMedicineInteractionListener {
        fun onMedicineDeleted(medicine: Medicine)
        fun onReminderStateChanged(medicine: Medicine, isEnabled: Boolean)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicineViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_medicine, parent, false)
        return MedicineViewHolder(view)
    }

    override fun onBindViewHolder(holder: MedicineViewHolder, position: Int) {
        val medicine = medicines[position]

        holder.nameTextView.text = medicine.name

        holder.daysTextView.text = when (medicine.reminder?.repeat) {
            Repeat.CUSTOM -> medicine.reminder?.daysOfWeek?.joinToString(" ") { it.name.take(3) } ?: ""
            else -> ""
        }

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, MedicineDetails::class.java).apply {
                putExtra("medicine", medicine)
            }
            context.startActivity(intent)
        }

        holder.itemView.setOnLongClickListener {
            AlertDialog.Builder(holder.itemView.context)
                .setTitle("Delete Medicine")
                .setMessage("Are you sure you want to delete this medicine?")
                .setPositiveButton("Yes") { dialog, _ ->
                    listener.onMedicineDeleted(medicine)
                    dialog.dismiss()
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
            true
        }

        holder.switchButton.isChecked = medicine.reminder?.enabled == true
        holder.timeTextView.text = if (medicine.reminder?.enabled == true) calculateNextReminder(medicine, LocalDateTime.now(), holder.itemView.context) else ""

        holder.switchButton.setOnCheckedChangeListener { _, isChecked ->
            listener.onReminderStateChanged(medicine, isChecked)
            updateReminderState(holder.itemView.context, medicine, isChecked)
            if (isChecked) {
                holder.timeTextView.text = calculateNextReminder(medicine, LocalDateTime.now(), holder.itemView.context)
                holder.dividerTextView.visibility = View.VISIBLE
            } else {
                cancelReminder(holder.itemView.context, medicine)
                holder.timeTextView.visibility = View.GONE
                holder.dividerTextView.visibility = View.GONE
            }
        }

        if (medicine.reminder?.daysOfWeek.isNullOrEmpty() || holder.timeTextView.text == "") {
            holder.dividerTextView.visibility = View.GONE
        }
    }

    private fun updateReminderState(context: Context, medicine: Medicine, isEnabled: Boolean) {
        val db = FirebaseFirestore.getInstance()
        val medicineRef = db.collection("Medicines").document(medicine.medicineId)

        medicineRef.get().addOnSuccessListener { document ->
            if (document != null) {
                val reminder = document.get("reminder") as? Map<String, Any>
                if (reminder != null) {
                    val updatedReminder = reminder.toMutableMap()
                    updatedReminder["enabled"] = isEnabled
                    medicineRef.update("reminder", updatedReminder)
                }
            }
        }.addOnFailureListener { e ->
            Log.e("ERROR:", "Error changing reminder state: " + e.message)
        }
    }

    private fun calculateNextReminder(medicine: Medicine, now: LocalDateTime, context: Context): String {
        val nextReminder = calculateNextReminderTime(medicine, now, context)
        val duration = java.time.Duration.between(now, nextReminder)

        val hours = duration.toHours()
        val minutes = duration.minusHours(hours).toMinutes()
        val reminderText = "Reminder in ${hours}h ${minutes}m"

        return reminderText
    }

    fun calculateNextReminderTime(medicine: Medicine, now: LocalDateTime, context: Context): LocalDateTime {
        val reminderTimeStr = medicine.reminder?.hour
        val reminderDateStr = medicine.reminder?.date

        val reminderTime = reminderTimeStr?.let { LocalTime.parse(it, DateTimeFormatter.ISO_LOCAL_TIME) }
        val reminderDate = reminderDateStr?.let { LocalDate.parse(it, DateTimeFormatter.ISO_LOCAL_DATE) }

        val nextReminder = when (medicine.reminder?.repeat) {
            Repeat.HOURLY -> {
                val nextReminderTime = now.withMinute(reminderTime?.minute ?: 0).withSecond(0).withNano(0)
                if (now.isAfter(nextReminderTime)) {
                    nextReminderTime.plusHours(1)
                } else {
                    nextReminderTime
                }
            }
            Repeat.CUSTOM -> {
                if (reminderTime != null) {
                    if (!medicine.reminder?.daysOfWeek.isNullOrEmpty()) {
                        val sortedDaysOfWeek = medicine.reminder!!.daysOfWeek
                            .map { DayOfWeek.valueOf(it.toString()) }
                            .sorted()

                        val today = now.dayOfWeek
                        val currentTime = now.toLocalTime()

                        val nextDay = sortedDaysOfWeek
                            .find { it > today || (it == today && reminderTime.isAfter(currentTime)) }
                            ?: sortedDaysOfWeek.first()

                        val daysToAdd = if (nextDay > today || (nextDay == today && reminderTime.isAfter(currentTime))) {
                            nextDay.value - today.value
                        } else {
                            7 - today.value + nextDay.value
                        }

                        now.plusDays(daysToAdd.toLong()).withHour(reminderTime.hour).withMinute(reminderTime.minute).withSecond(0).withNano(0)
                    } else if (reminderDate != null) {
                        val nextReminderDateTime = LocalDateTime.of(reminderDate, reminderTime)
                        if (now.isBefore(nextReminderDateTime)) {
                            nextReminderDateTime
                        } else {
                            nextReminderDateTime.plusDays(1)
                        }
                    } else {
                        now
                    }
                } else {
                    now
                }
            }
            else -> now
        }

        scheduleReminder(context, medicine, nextReminder)
        return nextReminder
    }

    @SuppressLint("MissingPermission")
    private fun scheduleReminder(context: Context, medicine: Medicine, reminderTime: LocalDateTime) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("requestCode", medicine.hashCode())
            putExtra("medicineId", medicine.medicineId)
            putExtra("medicine", medicine)
        }
        val requestCode = medicine.hashCode()

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val triggerTime = reminderTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerTime,
            pendingIntent
        )
    }

    fun cancelReminder(context: Context, medicine: Medicine) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(
            PendingIntent.getBroadcast(
                context,
                medicine.hashCode(),
                Intent(context, ReminderReceiver::class.java),
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        )
    }

    override fun getItemCount(): Int {
        return medicines.size
    }

    fun updateMedicine(medicine: Medicine) {
        val position = medicines.indexOfFirst { it.medicineId == medicine.medicineId }
        if (position != -1) {
            medicines[position] = medicine
            notifyItemChanged(position)
        }
    }
}
