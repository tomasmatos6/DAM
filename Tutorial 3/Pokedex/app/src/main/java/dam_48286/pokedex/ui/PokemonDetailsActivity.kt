package dam_48286.pokedex.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import dam_48286.pokedex.R

class PokemonDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val pokemonName = intent.getStringExtra("pokemon_name")
        val pokemonId = intent.getStringExtra("pokemon_id")
        val pokemonImageUrl = intent.getStringExtra("pokemon_url")
    }
}