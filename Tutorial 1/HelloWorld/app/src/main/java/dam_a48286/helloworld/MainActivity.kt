package dam_a48286.helloworld

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.CalendarView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        println(this@MainActivity.localClassName + getString(R.string.activity_oncreate_msg))
        getString(R.string.activity_oncreate_msg, this@MainActivity.localClassName)

        val calendarView = findViewById<CalendarView>(R.id.calendarView)
        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            changeBackground(year, month, dayOfMonth)
        }
    }

    private fun changeBackground(year: Int, month: Int, dayOfMonth: Int) {
        // Example: Check if the day is even or odd, and change background accordingly
        val rootView = findViewById<ConstraintLayout>(R.id.rootLayout)
        val selectedDate = "$year/$month/$dayOfMonth"
        val title = findViewById<TextView>(R.id.textView)
        title.text = selectedDate
        val isDayEven = dayOfMonth % 2 == 0
        if (isDayEven) {
            rootView.setBackgroundColor(Color.GRAY)
        } else {
            rootView.setBackgroundColor(Color.WHITE)
        }
    }
}