package dam_48286.finalproject

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

@Parcelize
data class Plan(
    var planId: String = "",
    var planName: String = "",
    var creationDate: Timestamp? = null,
    var creatorId: String = "",
    var medicines: List<String> = emptyList()
) : Parcelable
