package dam_48286.pokedex.model

data class PokemonDetails(
    var id: Int,
    var pokemon: Pokemon,
    var description: String,
    var weight: Float,
    var height: Float,
    var types: List<PokemonType>
)
