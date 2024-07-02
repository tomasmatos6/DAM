package dam_48286.finalproject

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.util.Calendar

class MedicineDetails : AppCompatActivity() {

    private lateinit var medicineImage: ImageView
    private lateinit var medicineNameTextView: TextView
    private lateinit var medicineNameEditText: EditText
    private lateinit var timeTextView: TextView
    private lateinit var dateTextView: TextView
    private lateinit var labelTextView: TextView
    private lateinit var labelTextInput: EditText
    private lateinit var saveButton: Button

    private lateinit var mondayImageView: ImageView
    private lateinit var tuesdayImageView: ImageView
    private lateinit var wednesdayImageView: ImageView
    private lateinit var thursdayImageView: ImageView
    private lateinit var fridayImageView: ImageView
    private lateinit var saturdayImageView: ImageView
    private lateinit var sundayImageView: ImageView

    private val selectedDays = mutableSetOf<DayOfWeek>()
    private val REQUEST_CODE_SELECT_IMAGE = 1
    private var selectedImageUri: Uri? = null

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_medicine_details)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.medicine_details)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize views
        medicineImage = findViewById(R.id.medicineImage)
        medicineNameTextView = findViewById(R.id.medicineNameTextView)
        medicineNameEditText = findViewById(R.id.medicineNameEditText)
        timeTextView = findViewById(R.id.timeTextView)
        dateTextView = findViewById(R.id.dateTextView)
        labelTextView = findViewById(R.id.labelTextView)
        labelTextInput = findViewById(R.id.labelTextInput)
        saveButton = findViewById(R.id.saveButton)

        mondayImageView = findViewById(R.id.mondayImageView)
        tuesdayImageView = findViewById(R.id.tuesdayImageView)
        wednesdayImageView = findViewById(R.id.wednesdayImageView)
        thursdayImageView = findViewById(R.id.thursdayImageView)
        fridayImageView = findViewById(R.id.fridayImageView)
        saturdayImageView = findViewById(R.id.saturdayImageView)
        sundayImageView = findViewById(R.id.sundayImageView)

        // Set click listeners for day selection
        mondayImageView.setOnClickListener { toggleDaySelection(DayOfWeek.MONDAY, mondayImageView) }
        tuesdayImageView.setOnClickListener { toggleDaySelection(DayOfWeek.TUESDAY, tuesdayImageView) }
        wednesdayImageView.setOnClickListener { toggleDaySelection(DayOfWeek.WEDNESDAY, wednesdayImageView) }
        thursdayImageView.setOnClickListener { toggleDaySelection(DayOfWeek.THURSDAY, thursdayImageView) }
        fridayImageView.setOnClickListener { toggleDaySelection(DayOfWeek.FRIDAY, fridayImageView) }
        saturdayImageView.setOnClickListener { toggleDaySelection(DayOfWeek.SATURDAY, saturdayImageView) }
        sundayImageView.setOnClickListener { toggleDaySelection(DayOfWeek.SUNDAY, sundayImageView) }

        medicineImage.setOnClickListener { selectImage() }
        medicineNameTextView.setOnClickListener { toggleEditTextView(medicineNameTextView, medicineNameEditText) }
        timeTextView.setOnClickListener { pickTime() }
        dateTextView.setOnClickListener { pickDate() }
        labelTextView.setOnClickListener { toggleEditTextView(labelTextView, labelTextInput) }


        // Retrieve and display data
        val medicine = intent.getParcelableExtra<Medicine>("medicine")
        if (medicine != null) {
            displayMedicineDetails(medicine)
            saveButton.setOnClickListener { saveMedicineDetails(medicine) }
        }
    }

    private fun displayMedicineDetails(medicine: Medicine) {
        // Set medicine name
        medicineNameTextView.text = medicine.name
        medicineNameEditText.setText(medicine.name)
        if(auth.currentUser != null) {
            // Load medicine image using Glide
            val imageUrl = medicine.image
            if (imageUrl.isNotEmpty()) {
                Glide.with(this)
                    .load(imageUrl)
                    .into(medicineImage)
            } else {
                medicineImage.setImageResource(R.drawable.edit_photo)
            }
        }


        // Set time and date
        val reminder = medicine.reminder
        if (reminder != null) {
            timeTextView.text = reminder.hour
            dateTextView.text = reminder.date ?: ""
            reminder.daysOfWeek.forEach { day ->
                toggleDaySelection(day, getImageViewForDay(day))
            }
        }
        // Set label text
        labelTextView.text = medicine.label
        labelTextInput.setText(medicine.label)
    }

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE)
    }

    private fun pickDate() {
        val now = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val date = LocalDate.of(year, month + 1, dayOfMonth)
                dateTextView.text = date.toString()
            },
            now.get(Calendar.YEAR),
            now.get(Calendar.MONTH),
            now.get(Calendar.DAY_OF_MONTH)
        )
        saveButton.visibility = View.VISIBLE
        datePickerDialog.setOnDismissListener { saveButton.visibility = View.GONE }
        datePickerDialog.setOnCancelListener { saveButton.visibility = View.GONE }
        datePickerDialog.show()
    }

    private fun pickTime() {
        val now = Calendar.getInstance()
        val timePickerDialog = TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                val time = LocalTime.of(hourOfDay, minute)
                timeTextView.text = time.toString()
            },
            now.get(Calendar.HOUR_OF_DAY),
            now.get(Calendar.MINUTE),
            true
        )
        saveButton.visibility = View.VISIBLE
        timePickerDialog.setOnDismissListener { saveButton.visibility = View.GONE }
        timePickerDialog.setOnCancelListener { saveButton.visibility = View.GONE }
        timePickerDialog.show()
    }

    private fun toggleEditTextView(textView: TextView, editText: EditText) {
        textView.visibility = View.GONE
        editText.visibility = View.VISIBLE
        saveButton.visibility = View.VISIBLE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                selectedImageUri = uri
                Glide.with(this).load(uri).into(medicineImage)
            }
        }
    }

    private fun toggleDaySelection(day: DayOfWeek, imageView: ImageView) {
        if (selectedDays.contains(day)) {
            selectedDays.remove(day)
            imageView.setImageResource(getUnselectedImageRes(day))
        } else {
            selectedDays.add(day)
            imageView.setImageResource(getSelectedImageRes(day))
        }
        dateTextView.visibility = if (selectedDays.isEmpty()) View.VISIBLE else View.GONE
        saveButton.visibility = View.VISIBLE
    }

    private fun getSelectedImageRes(day: DayOfWeek): Int {
        return when (day) {
            DayOfWeek.MONDAY -> R.drawable.m_selected
            DayOfWeek.TUESDAY -> R.drawable.t_selected
            DayOfWeek.WEDNESDAY -> R.drawable.w_selected
            DayOfWeek.THURSDAY -> R.drawable.t_selected
            DayOfWeek.FRIDAY -> R.drawable.f_selected
            DayOfWeek.SATURDAY -> R.drawable.s_selected
            DayOfWeek.SUNDAY -> R.drawable.s_selected
        }
    }

    private fun getUnselectedImageRes(day: DayOfWeek): Int {
        return when (day) {
            DayOfWeek.MONDAY -> R.drawable.m_unselected
            DayOfWeek.TUESDAY -> R.drawable.t_unselected
            DayOfWeek.WEDNESDAY -> R.drawable.w_unselected
            DayOfWeek.THURSDAY -> R.drawable.t_unselected
            DayOfWeek.FRIDAY -> R.drawable.f_unselected
            DayOfWeek.SATURDAY -> R.drawable.s_unselected
            DayOfWeek.SUNDAY -> R.drawable.s_unselected
        }
    }

    private fun getImageViewForDay(day: DayOfWeek): ImageView {
        return when (day) {
            DayOfWeek.MONDAY -> mondayImageView
            DayOfWeek.TUESDAY -> tuesdayImageView
            DayOfWeek.WEDNESDAY -> wednesdayImageView
            DayOfWeek.THURSDAY -> thursdayImageView
            DayOfWeek.FRIDAY -> fridayImageView
            DayOfWeek.SATURDAY -> saturdayImageView
            DayOfWeek.SUNDAY -> sundayImageView
        }
    }

    private fun saveMedicineDetails(medicine: Medicine) {
        // Retrieve updated values
        val updatedName = medicineNameEditText.text.toString()
        val updatedTime = timeTextView.text.toString()
        val updatedDate = dateTextView.text.toString()
        val updatedLabel = labelTextInput.text.toString()

        // Update the medicine object
        medicine.name = updatedName
        medicine.reminder?.daysOfWeek = selectedDays.toList()
        medicine.reminder?.hour = updatedTime
        medicine.reminder?.date = if (selectedDays.isEmpty()) updatedDate else null
        medicine.label = updatedLabel

        val db = FirebaseFirestore.getInstance()
        val medicineRef = db.collection("Medicines").document(medicine.medicineId)

        if (selectedImageUri != null) {
            // Save image to Firebase Storage
            val imageRef = storage.reference.child("medicine_images/${medicine.medicineId}.jpg")
            imageRef.putFile(selectedImageUri!!).addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    val photoUrl = uri.toString()
                    medicine.image = photoUrl

                    // Save the updated medicine to the database
                    medicineRef.set(medicine)
                        .addOnSuccessListener {
                            // Show success message
                            Toast.makeText(this, "Medicine details updated", Toast.LENGTH_SHORT).show()
                            // Toggle views back to TextView
                            medicineNameEditText.visibility = View.GONE
                            medicineNameTextView.visibility = View.VISIBLE
                            labelTextInput.visibility = View.GONE
                            labelTextView.visibility = View.VISIBLE
                            // Update the TextViews with new values
                            medicineNameTextView.text = updatedName
                            labelTextView.text = updatedLabel
                        }
                        .addOnFailureListener { e ->
                            // Show error message
                            Toast.makeText(this, "Error updating medicine: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    saveButton.visibility = View.GONE
                }.addOnFailureListener {
                    Toast.makeText(this, "Failed to get image URL", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
            }
        } else {
            // No image selected, save the updated medicine to the database
            medicineRef.set(medicine)
                .addOnSuccessListener {
                    // Show success message
                    Toast.makeText(this, "Medicine details updated", Toast.LENGTH_SHORT).show()
                    // Toggle views back to TextView
                    medicineNameEditText.visibility = View.GONE
                    medicineNameTextView.visibility = View.VISIBLE
                    labelTextInput.visibility = View.GONE
                    labelTextView.visibility = View.VISIBLE
                    // Update the TextViews with new values
                    medicineNameTextView.text = updatedName
                    labelTextView.text = updatedLabel
                }
                .addOnFailureListener { e ->
                    // Show error message
                    Toast.makeText(this, "Error updating medicine: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            saveButton.visibility = View.GONE
        }
        this.startActivity(Intent(this, PlanList::class.java))
        finish()
    }

}
