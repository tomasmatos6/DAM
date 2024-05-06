package dam_48286.pokedex.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dam_48286.pokedex.R
import dam_48286.pokedex.model.mocks.MockData
import dam_48286.pokedex.model.network.NetworkModule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegionsViewModel : ViewModel() {
    private val _regions = MutableLiveData<List<PokemonRegion>?>()
    val regions: LiveData<List<PokemonRegion>?>
        get() = _regions

    fun fetchRegions() {
        viewModelScope.launch(Dispatchers.Default) {
            val response = NetworkModule.client.fetchRegionList()

            val regionsList = response?.results?.map {
                val regexToGetId = "\\/([^\\/]+)\\/?\$".toRegex()

                var regionId = regexToGetId.find(it.url!!)?.value
                regionId = regionId?.removeSurrounding("/")
                PokemonRegion(regionId?.toInt() ?: 0, it.name.toString(), R.drawable.bg_kanto, R.drawable.pk_kanto)
            }

            _regions.postValue(regionsList)
        }
    }
}