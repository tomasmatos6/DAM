package dam_48286.pokedex.model

import android.os.Parcelable
import androidx.annotation.DrawableRes

@kotlinx.parcelize.Parcelize
data class Pokemon(
    var id: Int,
    var name: String,
    var imageUrl: String
) : Parcelable
