package dam_48286.pokedex.model

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes

data class PokemonType(
    var id: Int,
    var name: String,
    @DrawableRes val icon: Int,
    @ColorRes val color: Int
)
