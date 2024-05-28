package dam_48286.pokedex.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dam_48286.pokedex.domain.database.PokemonRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import dam_48286.pokedex.domain.entities.PokemonRegion
import dam_48286.pokedex.domain.entities.Pokemon

class PokemonListViewModel : ViewModel() {
    private val _pokemons = MutableLiveData<List<Pokemon>?>()
    val pokemons: LiveData<List<Pokemon>?>
        get() = _pokemons

    private lateinit var _repository: PokemonRepository
    fun initViewMode(repository: PokemonRepository) {
        _repository = repository
    }

    fun fetchPokemons(region: PokemonRegion) {
        viewModelScope.launch(Dispatchers.Default) {
            val pkList = _repository.getPokemonsByRegion(region)
            _pokemons.postValue(pkList.value)
        }
    }
}