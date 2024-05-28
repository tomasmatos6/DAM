package dam_48286.pokedex.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import dam_48286.pokedex.R
import dam_48286.pokedex.databinding.ActivityPokemonListBinding
import dam_48286.pokedex.domain.database.DBModule
import dam_48286.pokedex.domain.entities.PokemonRegion
import dam_48286.pokedex.model.PokemonDetails
import dam_48286.pokedex.model.PokemonListViewModel
import dam_48286.pokedex.model.PokemonsAdapter

class PokemonListActivity : AppCompatActivity() {

    val viewModel: PokemonListViewModel by viewModels()
    private lateinit var binding: ViewDataBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = DataBindingUtil.setContentView(this, R.layout.activity_pokemon_list)
        val pokemonListBinding = binding as ActivityPokemonListBinding
        var listView = pokemonListBinding.pksRecyclerView

        viewModel.initViewMode(DBModule.getInstance(this).pokemonRepository)
        viewModel.pokemons.observe(this) {
            listView.adapter = it?.let { it1 ->
                PokemonsAdapter(pokemonList = it1, context = this) { pokemon ->
                    navigateToPokemonDetails(pokemon)
                }
            }
        }

        viewModel.fetchPokemons(PokemonRegion(intent.getIntExtra("region_id", 1),
            intent.getStringExtra("region_name").toString()
        ))
    }

    private fun navigateToPokemonDetails(pokemon: dam_48286.pokedex.domain.entities.Pokemon) {
        val intent = Intent(this, PokemonDetails::class.java)
        intent.putExtra("pokemon_id", pokemon.id)
        intent.putExtra("pokemon_name", pokemon.name)
        intent.putExtra("pokemon_url", pokemon.imageUrl)
        startActivity(intent)
    }
}