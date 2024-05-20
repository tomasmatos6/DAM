package dam_48286.pokedex.domain.entities

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@kotlinx.parcelize.Parcelize
@Entity(tableName = "pokemon")
data class Pokemon(
    @PrimaryKey
    @ColumnInfo(name = "pokemon_id")
    var id: Int,

    @ColumnInfo(name = "pokemon_name")
    var name:String,

    @ColumnInfo(name = "pokemon_imgUrl")
    var imageUrl: String,

    @ColumnInfo(name = "region_id")
    var regionId: Int? = null
) : Parcelable
