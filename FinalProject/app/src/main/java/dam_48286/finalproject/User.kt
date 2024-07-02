package dam_48286.finalproject

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    var name: String = "",
    var email: String = "",
    var image: String = "",
    var plans: List<String> = emptyList(),
    var type: String = "default"
) : Parcelable
