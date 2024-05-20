package dam_488286.finalproject

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import dam_488286.finalproject.R

class AddPlanPopup : DialogFragment() {

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
            showAddExistingPlanDialog()
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
            // Add your logic here
            dialog.dismiss() // Dismiss the dialog after handling the click
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
            // Add your logic here
            dialog.dismiss() // Dismiss the dialog after handling the click
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent) // Set background color to transparent
        return dialog
    }
}
