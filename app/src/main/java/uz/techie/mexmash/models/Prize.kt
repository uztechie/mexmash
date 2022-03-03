package uz.techie.mexmash.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "prize")
data class Prize(
    @PrimaryKey
    val id:Int,
    val type_name:String? = null,
    var name:String? = null,
    var image:String? = null,
    val type:Int? = null,
    var point:Long? = null,
    var kg:Long? = null,
    var level_name:String? = null,
    var level_id:Int? = null
)
