package dam_48286.pokedex.domain.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.media3.common.util.Log
import dam_48286.pokedex.domain.dao.PokemonDao
import dam_48286.pokedex.domain.entities.Pokemon
import dam_48286.pokedex.domain.entities.PokemonRegion
import dam_48286.pokedex.model.network.responses.PokemonAPI

class PokemonRepository(private val pokemonApi: PokemonAPI,
                        private val pokemonDao: PokemonDao
) {

    suspend fun getPokemonsByRegion(region: PokemonRegion): LiveData<List<Pokemon>> {
        try {

            var regionWithPokemons = pokemonDao.getPokemonByRegion(region.id)

            if (regionWithPokemons.pokemon.isEmpty()) {
                var pkByRegionResponse = pokemonApi.fetchPokemonByRegionId(region.id)
                val pokemons = pkByRegionResponse.pokemons.map {
                    val regexToGetId = "/([^/]+)/?\$".toRegex()
                    var pkId = regexToGetId.find(it.url!!)?.value
                    pkId = pkId?.removeSurrounding("/")
                    val pkName = it.name ?: ""
                    val pkIdInt = pkId?.toInt() ?: 0
                    val pkImageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master" +
                            "/sprites/pokemon/other/official-artwork/${pkId}.png"
                    Pokemon(pkIdInt, pkName, pkImageUrl, regionId = region.id)
                }
                savePokemonsinDB(pokemons)
                return MutableLiveData(pokemons)
            } else {
                val pks = regionWithPokemons.pokemon
                return MutableLiveData(pks)
            }
        } catch (e: java.lang.Exception) {
            Log.e("ERROR", e.toString())
        }

        return MutableLiveData<List<Pokemon>>()
    }

    private fun savePokemonsinDB(pokemons: List<Pokemon>) {
        pokemons.forEach {
            it.let { it1 -> pokemonDao.insertPokemon(it1) }
        }
    }
}