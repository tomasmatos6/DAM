package dam_48286.pokedex.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import dam_48286.pokedex.R
import dam_48286.pokedex.model.PokemonsAdapter
import dam_48286.pokedex.model.mocks.MockData

class PokemonListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pokemonlist)
        var listView = findViewById<RecyclerView>(R.id.pksRecyclerView)
        listView.adapter = PokemonsAdapter(pokemonList = MockData.pokemons, context = this)
    }
}