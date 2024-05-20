package dam_48286.pokedex.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import dam_48286.pokedex.R
import dam_48286.pokedex.databinding.ItemRegionBinding
import dam_48286.pokedex.domain.entities.PokemonRegion

class RegionAdapter(
    private val pkRegionList: List<dam_48286.pokedex.domain.entities.PokemonRegion>,
    private val context: Context,
    private val onItemClick: (PokemonRegion) -> Unit
) : RecyclerView.Adapter<RegionAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val regionItemBinding = ItemRegionBinding.bind(itemView)
        fun bindView(region: dam_48286.pokedex.domain.entities.PokemonRegion, itemClickedListener: (PokemonRegion) -> Unit) {
            regionItemBinding.region = region
            itemView.setOnClickListener{
                itemClickedListener.invoke(region)
            }
        }
        val bgImageView = itemView.findViewById<AppCompatImageView>(R.id.regionBgImage)
        val startersImageView = itemView.findViewById<AppCompatImageView>(R.id.regionStartersImageView)
        val regionTitleTextView = itemView.findViewById<AppCompatTextView>(R.id.regionNameTextView)
        val regionSubtitleTextView = itemView.findViewById<AppCompatTextView>(R.id.regionIdTextView)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.item_region, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val region = pkRegionList[position]
        holder.bindView(region, onItemClick)
        //holder.bgImageView.setImageResource(region.bg)
        //holder.startersImageView.setImageResource(region.starters)
        holder.regionTitleTextView.text = region.name
        holder.regionSubtitleTextView.text = region.id.toString() + " Generation"
        holder.itemView.setOnClickListener {
            onItemClick(region) // 3. Call interface method
        }
    }

    override fun getItemCount(): Int {
        return pkRegionList.size
    }
}