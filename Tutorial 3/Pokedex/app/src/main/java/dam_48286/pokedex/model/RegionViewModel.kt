package dam_48286.pokedex.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dam_48286.pokedex.R
import dam_48286.pokedex.domain.database.RegionRepository
import dam_48286.pokedex.model.mocks.MockData
import dam_48286.pokedex.model.network.NetworkModule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import dam_48286.pokedex.domain.entities.PokemonRegion

class RegionsViewModel : ViewModel() {
    private val _regions = MutableLiveData<List<PokemonRegion>?>()
    val regions: LiveData<List<PokemonRegion>?>
        get() = _regions

    private lateinit var _repository: RegionRepository
    fun initViewMode(repository: RegionRepository) {
        _repository = repository
    }
    fun fetchRegions() {

        //_regions.value = MockData.regions
        viewModelScope.launch(Dispatchers.Default) {
            val regionsList = _repository.getRegions()
            _regions.postValue(regionsList.value)
        }
    }
}