package dam_48286.pokedex.model

import androidx.annotation.DrawableRes

data class Pokemon(
    var id: Int,
    var name: String,
    @DrawableRes var imageUrl: Int
)
