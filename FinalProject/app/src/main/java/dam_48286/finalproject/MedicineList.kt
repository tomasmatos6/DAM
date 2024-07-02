package dam_48286.finalproject

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import java.time.DayOfWeek
import java.time.LocalDateTime

class MedicineList : AppCompatActivity(), MedicineAdapter.OnMedicineInteractionListener {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val medicines = mutableListOf<Medicine>()

    private lateinit var recyclerView: RecyclerView
    private lateinit var medicineAdapter: MedicineAdapter
    private lateinit var noMedicinesTextView: TextView
    private lateinit var addMedicineButton: View
    private lateinit var planNameTextView: TextView
    private lateinit var planId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medicine_list)

        recyclerView = findViewById(R.id.recyclerView)
        noMedicinesTextView = findViewById(R.id.noMedicinesTextView)
        addMedicineButton = findViewById(R.id.addMedicineButton)
        planNameTextView = findViewById(R.id.planNameTextView)

        planId = intent.getStringExtra("planId") ?: ""
        val planName = intent.getStringExtra("planName")
        planNameTextView.text = planName

        recyclerView.layoutManager = LinearLayoutManager(this)
        medicineAdapter = MedicineAdapter(medicines, this)
        recyclerView.adapter = medicineAdapter

        addMedicineButton.setOnClickListener {
            showAddMedicinePopup()
        }

        planNameTextView.setOnLongClickListener {
            Toast.makeText(this, "Copy ID", Toast.LENGTH_SHORT).show()
            true
        }

        planNameTextView.setOnClickListener {
            copyPlanIdToClipboard()
        }

        listenToPlanMedicines()
    }

    private fun copyPlanIdToClipboard() {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Plan ID", planId)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, "Plan ID copied to clipboard", Toast.LENGTH_SHORT).show()
    }

    private fun showAddMedicinePopup() {
        val popupWindowFragment = AddMedicinePopup()
        val args = Bundle()
        args.putString("planId", planId)
        popupWindowFragment.arguments = args
        popupWindowFragment.show(supportFragmentManager, "addMedicinePopup")
    }

    private fun listenToPlanMedicines() {
        planId.let {
            db.collection("Plans").document(planId)
                .addSnapshotListener { documentSnapshot, e ->
                    if (e != null) {
                        Log.e("MedicineList", "Listen failed.", e)
                        return@addSnapshotListener
                    }

                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        updateMedicinesList(documentSnapshot)
                    } else {
                        Log.d("MedicineList", "No such document")
                    }
                }
        }
    }

    private fun updateMedicinesList(planDocument: DocumentSnapshot) {
        medicines.clear()
        val medicineIds = planDocument.get("medicines") as? List<String> ?: emptyList()

        val tasks = medicineIds.map { medicineId ->
            db.collection("Medicines").document(medicineId).get()
        }

        Tasks.whenAllSuccess<DocumentSnapshot>(tasks)
            .addOnSuccessListener { medicineSnapshots ->
                for (snapshot in medicineSnapshots) {
                    if (snapshot.exists()) {
                        val id = snapshot.id
                        val name = snapshot.getString("name") ?: ""
                        val label = snapshot.getString("label") ?: ""
                        val image = snapshot.getString("image") ?: ""
                        val reminderMap = snapshot.get("reminder") as? Map<String, Any>
                        val reminder = reminderMap?.let { it ->
                            Reminder(
                                date = it["date"] as String?,
                                hour = it["hour"] as String,
                                repeat = Repeat.valueOf(it["repeat"] as String),
                                daysOfWeek = (it["daysOfWeek"] as? List<String>)?.map { day -> DayOfWeek.valueOf(day) } ?: emptyList(),
                                enabled = it["enabled"] as Boolean
                            )
                        }
                        val medicine = Medicine(id, name, image, label, reminder)
                        medicines.add(medicine)
                    }
                }
                medicineAdapter.notifyDataSetChanged()

                noMedicinesTextView.visibility = if (medicines.isEmpty()) View.VISIBLE else View.GONE
            }
            .addOnFailureListener { e ->
                Log.e("MedicineList", "Error fetching medicines", e)
            }
    }


    override fun onMedicineDeleted(medicine: Medicine) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Log.e("MedicineList", "User not logged in.")
            return
        }

        val planRef = db.collection("Plans").document(planId)
        val medicineRef = db.collection("Medicines").document(medicine.medicineId)

        db.runTransaction { transaction ->
            val planSnapshot = transaction.get(planRef)
            val medicinesArray = planSnapshot.get("medicines") as? MutableList<String> ?: mutableListOf()
            val updatedMedicinesArray = medicinesArray.filter { it != medicine.medicineId }
            transaction.update(planRef, "medicines", updatedMedicinesArray)
            transaction.delete(medicineRef)
        }.addOnSuccessListener {
            Log.d("MedicineList", "Medicine successfully deleted.")
            medicineAdapter.cancelReminder(this, medicine)
        }.addOnFailureListener { e ->
            Log.e("MedicineList", "Error deleting medicine", e)
        }
    }

    override fun onReminderStateChanged(medicine: Medicine, isEnabled: Boolean) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Log.e("MedicineList", "User not logged in.")
            return
        }

        val planRef = db.collection("Plans").document(planId)

        if (isEnabled) {
            // Schedule the reminder
            medicineAdapter.calculateNextReminderTime(medicine, LocalDateTime.now(), this)
        } else {
            // Cancel the reminder
            medicineAdapter.cancelReminder(this, medicine)
        }
    }

}
