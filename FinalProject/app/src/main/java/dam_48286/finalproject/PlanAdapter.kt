package dam_48286.finalproject

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class PlanAdapter(
    private val plans: List<Plan>,
    private val listener: OnPlanInteractionListener) : RecyclerView.Adapter<PlanAdapter.PlanViewHolder>() {

    private val firestore = FirebaseFirestore.getInstance()

    class PlanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val planName: TextView = itemView.findViewById(R.id.nameTextView)
        val creatorName: TextView = itemView.findViewById(R.id.creatorTextView)
    }

    interface OnPlanInteractionListener {
        fun onPlanDeleted(plan: Plan)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlanViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_plan, parent, false)
        return PlanViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlanViewHolder, position: Int) {
        val plan = plans[position]
        holder.planName.text = plan.planName
        loadCreatorName(plan.creatorId, holder.creatorName)

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, MedicineList::class.java)
            intent.putExtra("planId", plan.planId)
            intent.putExtra("planName", plan.planName)
            context.startActivity(intent)
        }

        holder.itemView.setOnLongClickListener {
            AlertDialog.Builder(holder.itemView.context)
                .setTitle("Delete Plan")
                .setMessage("Are you sure you want to delete this plan?")
                .setPositiveButton("Yes") { dialog, _ ->
                    listener.onPlanDeleted(plan)
                    dialog.dismiss()
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
            true
        }
    }

    override fun getItemCount(): Int = plans.size

    private fun loadCreatorName(creatorId: String, textView: TextView) {
        firestore.collection("Users").document(creatorId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val creatorName = document.getString("name")
                    "Created by: $creatorName".also { textView.text = it }
                } else {
                    "Creator not found".also { textView.text = it }
                }
            }
            .addOnFailureListener { e ->
                "Error loading creator".also { textView.text = it }
                Log.e("PlanAdapter", "Error loading creator", e)
            }
    }
}

