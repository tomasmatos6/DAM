package dam_48286.pokedex.model

data class PokemonDetails(
    var id: Int,
    var pokemon: Pokemon,
    var description: String,
    var weight: Double,
    var height: Double,
    var types: List<PokemonType>
)
