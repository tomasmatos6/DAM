package dam_48286.pokedex.domain.database

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dam_48286.pokedex.domain.dao.PokemonRegionDao
import dam_48286.pokedex.domain.entities.PokemonRegion
import dam_48286.pokedex.model.network.responses.PokemonAPI

class RegionRepository(private val pokemonApi: PokemonAPI,
                       private val regionDao: PokemonRegionDao
)
{
    suspend fun getRegions() : LiveData<List<PokemonRegion>>
    {
        val hasRegions = regionDao.count()
        if(hasRegions > 0)
        {
            val regions = regionDao.getRegions()
            Log.d("REGIÕES DAO", regions.toString())
            val regionsResponse = pokemonApi.fetchRegionList()
            Log.d("REGIÕES REQUEST", pokemonApi.fetchRegionList().toString())
            return MutableLiveData(regions)
        }
        try {
            val regionsResponse = pokemonApi.fetchRegionList()
            Log.d("REGIÕES REQUEST", pokemonApi.fetchRegionList().toString())
            val regions = regionsResponse.results?.map {
                val regexToGetId = "/([^/]+)/?\$".toRegex()
                var regionId = regexToGetId.find(it.url!!)?.value
                regionId = regionId?.removeSurrounding("/")
                PokemonRegion(regionId?.toInt() ?: 0, it.name.toString())

            }
            regions?.forEach {
                regionDao.insertRegion(it) }
            return MutableLiveData(regions)
        }catch (e: java.lang.Exception)
        {
            Log.e("ERROR", e.toString())
        }
        return MutableLiveData()
    }
}