package dam_48286.finalproject

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class AddPlanPopup : DialogFragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.add_plan_popup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set click listeners for the buttons
        view.findViewById<Button>(R.id.createNewPlanButton).setOnClickListener {
            showCreateNewPlanDialog()
        }

        view.findViewById<Button>(R.id.addExistingPlanButton).setOnClickListener {
            checkUserTypeAndShowDialog()
        }
    }

    private fun showCreateNewPlanDialog() {
        val dialogView = layoutInflater.inflate(R.layout.create_new_plan, null)
        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setView(dialogView)
        val dialog = dialogBuilder.create()
        dialog.show()

        dialogView.findViewById<Button>(R.id.createPlanButton).setOnClickListener {
            // Handle create button click for new plan
            val planName = dialogView.findViewById<EditText>(R.id.planNameEditText).text.toString()
            if(planName.isEmpty()) {
                Toast.makeText(requireContext(), "Plan name cannot be empty", Toast.LENGTH_SHORT).show()
            } else {
                createNewPlan(planName)
                dialog.dismiss() // Dismiss the dialog after handling the click
            }
        }
    }

    private fun createNewPlan(planName: String) {
        val user = auth.currentUser


        if (user != null) {
            val plan = hashMapOf(
                "name" to planName,
                "creationDate" to Date(),
                "creatorId" to user.uid
            )

            db.collection("Plans")
                .add(plan)
                .addOnSuccessListener { documentReference ->
                    addPlanToUser(user.uid, documentReference.id)
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Error occurred: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun checkUserTypeAndShowDialog() {
        val user = auth.currentUser
        if (user != null) {
            db.collection("Users").document(user.uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val userType = document.getString("type")
                        if (userType == "premium") {
                            showAddExistingPlanDialog()
                        } else {
                            showUpgradeToPremiumDialog()
                        }
                    } else {
                        Toast.makeText(requireContext(), "User data not found", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Error occurred: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun showAddExistingPlanDialog() {
        val dialogView = layoutInflater.inflate(R.layout.add_existing_plan, null)
        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setView(dialogView)
        val dialog = dialogBuilder.create()
        dialog.show()

        dialogView.findViewById<Button>(R.id.addPlanButton).setOnClickListener {
            // Handle add button click for existing plan
            val planId = dialogView.findViewById<EditText>(R.id.planIdEditText).text.toString()
            if(planId.isEmpty()) {
                Toast.makeText(requireContext(), "Please provide a plan id", Toast.LENGTH_SHORT)
                    .show()
            } else {
                addExistingPlan(planId)
                dialog.dismiss() // Dismiss the dialog after handling the click
            }
        }
    }

    private fun showUpgradeToPremiumDialog() {
        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setTitle("Upgrade to Premium")
            .setMessage("To add an existing plan, you need to subscribe to Premium. Would you like to upgrade?")
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
                    Toast.makeText(requireContext(), "You have been upgraded to Premium!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Failed to upgrade to Premium: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(requireContext(), "User not authenticated", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addExistingPlan(planId: String) {
        val user = auth.currentUser
        if (user != null) {
            db.collection("Plans").document(planId).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        addPlanToUser(user.uid, planId)
                    } else {
                        Toast.makeText(requireContext(), "Invalid Plan ID", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Error occurred: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun addPlanToUser(userId: String, planId: String) {
        val userRef = db.collection("Users").document(userId)
        userRef.update("plans", FieldValue.arrayUnion(planId))
            .addOnSuccessListener {
                // Successfully added plan ID to user document
                Toast.makeText(context, "Plan added", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                // Handle failure
                Log.e("AddPlanPopup", "Error adding plan to user", e)
                Toast.makeText(context, "Error adding plan", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent) // Set background color to transparent
        return dialog
    }
}
