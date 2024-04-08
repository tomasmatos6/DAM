package dam_48286.pokedex.model

import androidx.annotation.DrawableRes

data class PokemonRegion(
    var id: Int,
    var name: String,
    @DrawableRes val bg: Int,
    @DrawableRes val starters: Int
)