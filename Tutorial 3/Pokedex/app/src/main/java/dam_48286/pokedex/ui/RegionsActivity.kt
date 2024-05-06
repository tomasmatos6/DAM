package dam_48286.pokedex.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import dam_48286.pokedex.R
import dam_48286.pokedex.databinding.ActivityRegionsBinding
import dam_48286.pokedex.model.PokemonRegion
import dam_48286.pokedex.model.RegionsViewModel
import dam_48286.pokedex.model.mocks.MockData

class RegionsActivity : BottomNavActivity() {

    val viewModel: RegionsViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val regionBinding = binding as ActivityRegionsBinding
        var listView = regionBinding.regionsRecyclerView

        viewModel.regions.observe(this) {
            listView.adapter = it?.let { it1 ->
                RegionAdapter(pkRegionList = it1, context = this) { region ->
                    navigateToPokemonList(region)
                }
            }
        }

        viewModel.fetchRegions()
    }

    private fun navigateToPokemonList(region: PokemonRegion) {
        val intent = Intent(this, PokemonListActivity::class.java)
        intent.putExtra("region_id", region.id) // Assuming you need to pass region id
        startActivity(intent)
    }

    override val contentViewId: Int
        get() = R.layout.activity_regions
    override val navigationMenuItemId: Int
        get() = R.id.navigation_regions
}