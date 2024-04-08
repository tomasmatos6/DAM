package dam_48286.pokedex.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import dam_48286.pokedex.R
import dam_48286.pokedex.model.mocks.MockData

class RegionsActivity : BottomNavActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var listView = findViewById<RecyclerView>(R.id.regionsRecyclerView)
        listView.adapter = RegionAdapter(pkRegionList = MockData.regions, context = this)
    }

    override val contentViewId: Int
        get() = R.layout.activity_regions
    override val navigationMenuItemId: Int
        get() = R.id.navigation_regions
}