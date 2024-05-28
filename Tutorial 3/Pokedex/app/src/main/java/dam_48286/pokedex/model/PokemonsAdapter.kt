package dam_48286.pokedex.model

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.request.target.Target
import dam_48286.pokedex.R
import dam_48286.pokedex.databinding.ItemPokemonBinding
import dam_48286.pokedex.domain.entities.Pokemon

class PokemonsAdapter(
    private val pokemonList: List<dam_48286.pokedex.domain.entities.Pokemon>,
    private val context: Context,
    private val onItemClick: (Pokemon) -> Unit
) : RecyclerView.Adapter<PokemonsAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val pokemonItemBinding = ItemPokemonBinding.bind(itemView)

        fun bindView(pokemon: dam_48286.pokedex.domain.entities.Pokemon, itemClickListener: (Pokemon) -> Unit) {
            pokemonItemBinding.pokemon = pokemon
            itemView.setOnClickListener {
                itemClickListener.invoke(pokemon)
            }
        }

        val cardView = itemView.findViewById<CardView>(R.id.cardView)
        val pkImageView = itemView.findViewById<AppCompatImageView>(R.id.pkImage)
        val pkNameTextView = itemView.findViewById<AppCompatTextView>(R.id.pkName)
        val pkIDTextView = itemView.findViewById<AppCompatTextView>(R.id.pkID)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.item_pokemon, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pokemon = pokemonList[position]

        holder.bindView(pokemon, onItemClick)

        Glide.with(holder.pkImageView.context)
            .asBitmap()
            .load(pokemon.imageUrl)
            .listener(object : RequestListener<Bitmap>
            {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Bitmap>,
                    isFirstResource: Boolean
                ): Boolean {

                    Log.d("TAG", e?.message.toString())
                    return false
                }

                override fun onResourceReady(
                    resource: Bitmap,
                    model: Any,
                    p2: Target<Bitmap>?,
                    dataSource: DataSource,
                    p4: Boolean
                ): Boolean {
                    Log.d("TAG", "OnResourceReady")
                    if (resource != null) {
                        val p: Palette = Palette.from(resource).generate()

                        val rgb = p?.lightMutedSwatch?.rgb
                        if (rgb != null) {
                            holder.cardView.setCardBackgroundColor(rgb)
                        }
                    }
                    return false
                }
            })
            .into(holder.pkImageView)
        holder.pkNameTextView.text = pokemon.name
        holder.pkIDTextView.text = "#" + pokemon.id
    }

    override fun getItemCount(): Int {
        return pokemonList.size
    }
}