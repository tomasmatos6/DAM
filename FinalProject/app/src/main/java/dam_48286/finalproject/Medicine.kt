package dam_48286.finalproject

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Medicine(
    var medicineId: String = "",
    var name: String = "",
    var image: String = "",
    var label: String = "",
    var reminder: Reminder? = null
) :Parcelable

