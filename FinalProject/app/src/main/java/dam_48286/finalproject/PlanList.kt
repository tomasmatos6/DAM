package dam_48286.finalproject

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class PlanList : AppCompatActivity(), PlanAdapter.OnPlanInteractionListener {
    private var backPressedOnce = false
    private val auth = FirebaseAuth.getInstance()
    private var db = FirebaseFirestore.getInstance()
    private val plans = mutableListOf<Plan>()

    private lateinit var recyclerView: RecyclerView
    private lateinit var planAdapter: PlanAdapter
    private lateinit var noPlansTextView: TextView
    private lateinit var addPlanButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_plan_list)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.plan_list)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recyclerView = findViewById(R.id.recyclerView)
        noPlansTextView = findViewById(R.id.noPlansTextView)
        addPlanButton = findViewById(R.id.addPlanButton)
        recyclerView.layoutManager = LinearLayoutManager(this)
        planAdapter = PlanAdapter(plans, this)
        recyclerView.adapter = planAdapter

        listenToUserPlans()
    }

    private fun listenToUserPlans() {
        val user = auth.currentUser
        user?.let {
            db.collection("Users").document(user.uid)
                .addSnapshotListener { documentSnapshot, e ->
                    if (e != null) {
                        Log.e("PlanList", "Listen failed.", e)
                        return@addSnapshotListener
                    }

                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        val planIds = documentSnapshot.get("plans") as? List<String>
                        if (!planIds.isNullOrEmpty()) {
                            fetchPlans(planIds)
                        } else {
                            plans.clear()
                            planAdapter.notifyDataSetChanged()
                            showPlansOrMessage()
                        }
                    } else {
                        plans.clear()
                        planAdapter.notifyDataSetChanged()
                        showPlansOrMessage()
                    }
                }
        }
    }

    private fun fetchPlans(planIds: List<String>) {
        val tasks = planIds.map { planId ->
            db.collection("Plans").document(planId).get()
        }

        Tasks.whenAllSuccess<DocumentSnapshot>(tasks)
            .addOnSuccessListener { planSnapshots ->
                plans.clear()
                for (snapshot in planSnapshots) {
                    if (snapshot.exists()) {
                        val plan = snapshot.toObject(Plan::class.java)
                        if (plan != null) {
                            plan.planId = snapshot.id // Set the planId (document ID)
                            plan.planName = snapshot.getString("name") ?: ""
                            Log.d("Plan:", plan.toString())
                            plans.add(plan)
                        }
                    }
                }
                planAdapter.notifyDataSetChanged()
                showPlansOrMessage()
            }
            .addOnFailureListener { e ->
                Log.e("PlanList", "Error fetching plans", e)
            }
    }

    override fun onPlanDeleted(plan: Plan) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Log.e("PlanList", "User not logged in.")
            return
        }

        val planRef = db.collection("Plans").document(plan.planId)
        val userRef = db.collection("Users").document(currentUser.uid)

        // Step 1: Fetch the plan document
        planRef.get().addOnSuccessListener { planSnapshot ->
            if (planSnapshot.exists()) {
                val medicines = planSnapshot.get("medicines") as? List<String> ?: listOf()

                // Step 2: Run the transaction
                db.runTransaction { transaction ->
                    // Get the current user's document snapshot
                    val userSnapshot = transaction.get(userRef)
                    val userPlans = userSnapshot.get("plans") as? MutableList<String> ?: mutableListOf()

                    // Remove the plan ID from the user's plans list
                    userPlans.remove(plan.planId)
                    transaction.update(userRef, "plans", userPlans)

                    // Delete each medicine document
                    for (medicineId in medicines) {
                        val medicineRef = db.collection("Medicines").document(medicineId)
                        transaction.delete(medicineRef)
                    }

                    // Delete the plan document from the Plans collection
                    transaction.delete(planRef)
                }.addOnSuccessListener {
                    Log.d("PlanList", "Plan and associated medicines successfully deleted.")
                    plans.remove(plan)
                    planAdapter.notifyDataSetChanged()
                    showPlansOrMessage()
                }.addOnFailureListener { e ->
                    Log.e("PlanList", "Error deleting plan and medicines", e)
                }
            } else {
                Log.e("PlanList", "Plan document does not exist.")
            }
        }.addOnFailureListener { e ->
            Log.e("PlanList", "Error fetching plan document", e)
        }
    }



    private fun showPlansOrMessage() {
        if (plans.isEmpty()) {
            noPlansTextView.visibility = View.VISIBLE
        } else {
            noPlansTextView.visibility = View.GONE
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
