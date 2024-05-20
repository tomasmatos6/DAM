package dam_488286.finalproject

import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class PlanList : AppCompatActivity() {
    private var backPressedOnce = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_plan_list)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.plan_list)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun showPopupWindow(view: View) {
        val popupWindowFragment = AddPlanPopup()
        popupWindowFragment.show(supportFragmentManager, "addPlanPopup")
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
}