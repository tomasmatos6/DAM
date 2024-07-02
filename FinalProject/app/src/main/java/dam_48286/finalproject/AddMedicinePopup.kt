package dam_48286.finalproject

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.util.*

class AddMedicinePopup : DialogFragment() {

    private lateinit var nameEditText: EditText
    private lateinit var labelEditText: EditText
    private lateinit var repeatSpinner: Spinner
    private lateinit var timeTextView: TextView
    private lateinit var dateTextView: TextView
    private lateinit var addButton: Button
    private lateinit var hourlySelector: LinearLayout
    private lateinit var hourPicker: NumberPicker
    private lateinit var customSelector: LinearLayout

    private lateinit var mondayImageView: ImageView
    private lateinit var tuesdayImageView: ImageView
    private lateinit var wednesdayImageView: ImageView
    private lateinit var thursdayImageView: ImageView
    private lateinit var fridayImageView: ImageView
    private lateinit var saturdayImageView: ImageView
    private lateinit var sundayImageView: ImageView

    private var selectedDays = mutableSetOf<DayOfWeek>()

    private var db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.add_medicine_popup, container, false)

        nameEditText = view.findViewById(R.id.nameEditText)
        labelEditText = view.findViewById(R.id.labelEditText)
        repeatSpinner = view.findViewById(R.id.repeatSpinner)
        timeTextView = view.findViewById(R.id.timeTextView)
        dateTextView = view.findViewById(R.id.dateTextView)
        addButton = view.findViewById(R.id.addButton)
        hourlySelector = view.findViewById(R.id.hourlySelector)
        hourPicker = view.findViewById(R.id.hourPicker)
        customSelector = view.findViewById(R.id.customSelector)

        mondayImageView = view.findViewById(R.id.mondayImageView)
        tuesdayImageView = view.findViewById(R.id.tuesdayImageView)
        wednesdayImageView = view.findViewById(R.id.wednesdayImageView)
        thursdayImageView = view.findViewById(R.id.thursdayImageView)
        fridayImageView = view.findViewById(R.id.fridayImageView)
        saturdayImageView = view.findViewById(R.id.saturdayImageView)
        sundayImageView = view.findViewById(R.id.sundayImageView)

        setupRepeatSpinner()
        setupDayImageViews()

        timeTextView.setOnClickListener { pickTime() }
        dateTextView.setOnClickListener { pickDate() }

        addButton.setOnClickListener {
            addMedicine()
        }

        hourPicker.maxValue = 23
        hourPicker.minValue = 1

        return view
    }

    private fun setupRepeatSpinner() {
        val repeatOptions = Repeat.values().map { it.name }.toTypedArray()
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, repeatOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        repeatSpinner.adapter = adapter

        repeatSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                when (Repeat.valueOf(repeatOptions[position])) {
                    Repeat.HOURLY -> {
                        hourlySelector.visibility = View.VISIBLE
                        timeTextView.visibility = View.GONE
                        dateTextView.visibility = View.GONE
                        customSelector.visibility = View.GONE
                    }
                    Repeat.CUSTOM -> {
                        hourlySelector.visibility = View.GONE
                        timeTextView.visibility = View.VISIBLE
                        dateTextView.visibility = View.VISIBLE
                        customSelector.visibility = View.VISIBLE
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun setupDayImageViews() {
        val dayViews = mapOf(
            DayOfWeek.MONDAY to mondayImageView,
            DayOfWeek.TUESDAY to tuesdayImageView,
            DayOfWeek.WEDNESDAY to wednesdayImageView,
            DayOfWeek.THURSDAY to thursdayImageView,
            DayOfWeek.FRIDAY to fridayImageView,
            DayOfWeek.SATURDAY to saturdayImageView,
            DayOfWeek.SUNDAY to sundayImageView
        )

        dayViews.forEach { (day, imageView) ->
            imageView.setOnClickListener {
                toggleDaySelection(day, imageView)
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

    private fun pickDate() {
        val now = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
                val currentDate = LocalDate.now()
                val selectedTime = LocalTime.parse(timeTextView.text.toString())

                // Disable today if selected time is before now
                if (selectedDate.isEqual(currentDate) && selectedTime.isBefore(LocalTime.now())) {
                    Toast.makeText(requireContext(), "Cannot select today with a past time", Toast.LENGTH_SHORT).show()
                } else if (selectedDate.isBefore(currentDate)) {
                    Toast.makeText(requireContext(), "Cannot select a date before today", Toast.LENGTH_SHORT).show()
                } else {
                    dateTextView.text = selectedDate.toString()
                }
            },
            now.get(Calendar.YEAR),
            now.get(Calendar.MONTH),
            now.get(Calendar.DAY_OF_MONTH)
        )

        // Disable past dates
        datePickerDialog.datePicker.minDate = now.timeInMillis
        datePickerDialog.show()
    }

    private fun pickTime() {
        val now = Calendar.getInstance()
        val timePickerDialog = TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                val time = LocalTime.of(hourOfDay, minute)
                timeTextView.text = time.toString()
            },
            now.get(Calendar.HOUR_OF_DAY),
            now.get(Calendar.MINUTE),
            true
        )
        timePickerDialog.show()
    }

    private fun addMedicine() {
        val name = nameEditText.text.toString()
        val time = timeTextView.text.toString()

        val label = labelEditText.text.toString()
        val repeat = Repeat.valueOf(repeatSpinner.selectedItem.toString().toUpperCase(Locale.ROOT))

        if (name.isEmpty()) {
            Toast.makeText(context, "Please enter the medicine name", Toast.LENGTH_SHORT).show()
            return
        }

        if (time.isEmpty() && repeat != Repeat.HOURLY) {
            Toast.makeText(context, "Please select the time", Toast.LENGTH_SHORT).show()
            return
        }

        val timeValue: LocalTime? = if (repeat != Repeat.HOURLY) {
            LocalTime.parse(timeTextView.text.toString())
        } else {
            null
        }

        val date: LocalDate? = if (repeat != Repeat.HOURLY && selectedDays.isEmpty()) {
            if(dateTextView.text.isNotBlank()) {
                LocalDate.parse(dateTextView.text?.toString())
            } else {
                if(timeValue?.isBefore(LocalTime.now()) == true) {
                    LocalDate.now().plusDays(1)
                } else {
                    LocalDate.now()
                }

            }
        } else {
            null
        }

        val reminder = Reminder(
            repeat = repeat,
            hour = if (repeat == Repeat.HOURLY) Reminder.fromLocalTime(LocalTime.of(hourPicker.value, 0)) else Reminder.fromLocalTime(timeValue!!),
            date = date?.let { Reminder.fromLocalDate(it) },
            daysOfWeek = if (repeat == Repeat.CUSTOM) selectedDays.toList() else emptyList(),
            enabled = true
        )

        val medicine = hashMapOf(
            "name" to name,
            "label" to label,
            "image" to "",
            "reminder" to reminder
        )

        val planId = arguments?.getString("planId")
        if (planId != null) {
            db.collection("Medicines")
                .add(medicine)
                .addOnSuccessListener { documentReference ->
                        updatePlanWithMedicine(planId, documentReference.id)
                }
                .addOnFailureListener { e ->
                    // Handle failure
                }
        }
    }

    private fun updatePlanWithMedicine(planId: String, medicineId: String) {
        val planRef = db.collection("Plans").document(planId)
        planRef.update("medicines", FieldValue.arrayUnion(medicineId))
            .addOnSuccessListener {
                dismiss()
            }
            .addOnFailureListener { e ->
                // Handle failure
            }
    }
}
