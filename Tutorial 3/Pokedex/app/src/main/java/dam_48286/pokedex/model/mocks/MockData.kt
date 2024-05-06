package dam_48286.pokedex.model.mocks

import dam_48286.pokedex.R
import dam_48286.pokedex.model.Pokemon
import dam_48286.pokedex.model.PokemonDetails
import dam_48286.pokedex.model.PokemonRegion
import dam_48286.pokedex.model.PokemonType
import kotlin.math.roundToInt
import kotlin.random.Random

object MockData {
    private val POKEMONS_SIZE = 100
    private var pokemonDetailDescription: String = "Pokem ipsum dolor " +
            "sit amet Crustle Grotle" +
            " Dragonair Palkia Shellder Terrakion. " +
            "Hive Badge Pokeball Spinda Seedot James Vullaby " +
            "Helix Fossil. Water Gun Professor Oak Marowak Spearow " +
            "Dunsparce Chimchar Nidorino." +
            " Silver Azumarill Tyranitar Trubbish " +
            "Fighting sunt in culpa qui officia Mothim. " +
            "Celadon City Mantine Clefable Piplup Scizor " +
            "excepteur sint occaecat cupidatat non proident Terrakion."


    var regions = listOf<PokemonRegion>(
        PokemonRegion(1, "Kanto", R.drawable.bg_kanto, R.drawable.pk_kanto),
        PokemonRegion(2, "Johto", R.drawable.bg_johto, R.drawable.pk_johto),
        PokemonRegion(3, "Hoenn", R.drawable.bg_hoenn, R.drawable.pk_hoenn),
        PokemonRegion(4, "Sinnoh", R.drawable.bg_sinnoh, R.drawable.pk_sinnoh),
        PokemonRegion(5, "Unova", R.drawable.bg_unova, R.drawable.pk_unova),
        PokemonRegion(6, "Kalos", R.drawable.bg_kalos, R.drawable.pk_kalos),
        PokemonRegion(7, "Alola", R.drawable.bg_alola, R.drawable.pk_alola),
        PokemonRegion(8, "Galar", R.drawable.bg_galar, R.drawable.pk_galar),
    )

    var pokemonTypeMock= listOf<PokemonType>(
        PokemonType(1,"water", R.drawable.water, R.color.water),
        PokemonType(2,"fire", R.drawable.fire, R.color.fire),
        PokemonType(3,"bug", R.drawable.bug, R.color.bug),
        PokemonType(4,"ghost", R.drawable.ghost, R.color.ghost),
        PokemonType(5,"grass", R.drawable.grass, R.color.grass),
        PokemonType(6,"ground", R.drawable.ground, R.color.ground),
        PokemonType(7,"rock", R.drawable.rock, R.color.rock),
        PokemonType(8,"dark", R.drawable.dark, R.color.dark),
        PokemonType(9,"dragon", R.drawable.dragon, R.color.dragon),
        PokemonType(10,"electric", R.drawable.electric, R.color.electric),
        PokemonType(11,"fairy", R.drawable.fairy, R.color.fairy),
        PokemonType(12,"fighting", R.drawable.fighting, R.color.fighting),
        PokemonType(13,"ice", R.drawable.ice, R.color.ice),
        PokemonType(14,"normal", R.drawable.normal, R.color.normal),
        PokemonType(15,"psychic", R.drawable.psychic, R.color.psychic),
        PokemonType(16,"flying", R.drawable.flying, R.color.flying),
        PokemonType(17,"poison", R.drawable.poison, R.color.poison),
        PokemonType(18,"steel", R.drawable.steel, R.color.steel)
    )

    /*var pokemons = (1..POKEMONS_SIZE).map {
        Pokemon(it,
            "bulbasaur",
            "https://raw.githubusercontent.com/PokeAPI/sprites/master" +
                    "/sprites/pokemon/other/official-artwork/${it}.png",
            regions.random(), pokemonTypeMock.asSequence().shuffled().take(2).toList()
        )
    }*/

    var pokemons = listOf(
         Pokemon(1,
             "bulbasaur",
             "https://raw.githubusercontent.com/PokeAPI/sprites/master" +
                     "/sprites/pokemon/other/official-artwork/1.png"

         ),
         Pokemon(4,
             "charmander",
             "https://raw.githubusercontent.com/PokeAPI/sprites/master" +
                     "/sprites/pokemon/other/official-artwork/4.png"
         ),
         Pokemon(7,
             "squirtle",
             "https://raw.githubusercontent.com/PokeAPI/sprites/master" +
                     "/sprites/pokemon/other/official-artwork/7.png",
         ),
         Pokemon(10,
             "caterpie",
             "https://raw.githubusercontent.com/PokeAPI/sprites/master" +
                     "/sprites/pokemon/other/official-artwork/10.png",),
         Pokemon(13,
             "weedle",
             "https://raw.githubusercontent.com/PokeAPI/sprites/master" +
                     "/sprites/pokemon/other/official-artwork/13.png"),
         Pokemon(16,
             "pidgey",
             "https://raw.githubusercontent.com/PokeAPI/sprites/master" +
                     "/sprites/pokemon/other/official-artwork/16.png"),
         Pokemon(19,
             "rattata",
             "https://raw.githubusercontent.com/PokeAPI/sprites/master" +
                     "/sprites/pokemon/other/official-artwork/19.png"),
         Pokemon(21,
             "spearow",
             "https://raw.githubusercontent.com/PokeAPI/sprites/master" +
                     "/sprites/pokemon/other/official-artwork/21.png"),
         Pokemon(23,
             "ekans",
             "https://raw.githubusercontent.com/PokeAPI/sprites/master" +
                     "/sprites/pokemon/other/official-artwork/23.png"),
         Pokemon(25,
             "pikachu",
             "https://raw.githubusercontent.com/PokeAPI/sprites/master" +
                     "/sprites/pokemon/other/official-artwork/25.png"),
         Pokemon(27,
             "sandshrew",
             "https://raw.githubusercontent.com/PokeAPI/sprites/master" +
                     "/sprites/pokemon/other/official-artwork/27.png"),
         Pokemon(29,
             "nidoran",
             "https://raw.githubusercontent.com/PokeAPI/sprites/master" +
                     "/sprites/pokemon/other/official-artwork/29.png"),
         Pokemon(35,
             "clefairy",
             "https://raw.githubusercontent.com/PokeAPI/sprites/master" +
                     "/sprites/pokemon/other/official-artwork/35.png"),
         Pokemon(37,
             "vulpix",
             "https://raw.githubusercontent.com/PokeAPI/sprites/master" +
                     "/sprites/pokemon/other/official-artwork/37.png"),
         Pokemon(39,
             "jigglypuff",
             "https://raw.githubusercontent.com/PokeAPI/sprites/master" +
                     "/sprites/pokemon/other/official-artwork/39.png"),
         Pokemon(41,
             "zubat",
             "https://raw.githubusercontent.com/PokeAPI/sprites/master" +
                     "/sprites/pokemon/other/official-artwork/41.png"),


         )

      var pokemonDetail = pokemons.map {
          PokemonDetails(
              it,
              pokemonDetailDescription,
              ( Random.nextDouble(20.0,50.0) * 100.0).roundToInt() / 100.0,
              (Random.nextDouble(0.20, 2.0) * 100.0).roundToInt() / 100.0,
              pokemonTypeMock.asSequence().shuffled().take(1).toList(),
              /*PokemonStats(),
              generateSequence {
                  PokemonEvolution(1, pokemons.random(), false,
                      0,"", 0, "")
              }.take(Random.nextInt(1,3)).toList()*/
          )
      }
}