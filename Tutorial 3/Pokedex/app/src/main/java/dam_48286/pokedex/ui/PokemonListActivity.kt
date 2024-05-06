package dam_48286.pokedex.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import dam_48286.pokedex.R
import dam_48286.pokedex.model.Pokemon
import dam_48286.pokedex.model.PokemonDetails
import dam_48286.pokedex.model.PokemonsAdapter
import dam_48286.pokedex.model.mocks.MockData

class PokemonListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pokemon_list)
        var listView = findViewById<RecyclerView>(R.id.pksRecyclerView)
        listView.adapter = PokemonsAdapter(pokemonList = MockData.pokemons, context = this) { pokemon ->
            navigateToPokemonDetails(pokemon)
        }
    }

    private fun navigateToPokemonDetails(pokemon: Pokemon) {
        val intent = Intent(this, PokemonDetails::class.java)
        intent.putExtra("pokemon_id", pokemon.id)
        intent.putExtra("pokemon_name", pokemon.name)
        intent.putExtra("pokemon_url", pokemon.imageUrl)
        startActivity(intent)
    }
}