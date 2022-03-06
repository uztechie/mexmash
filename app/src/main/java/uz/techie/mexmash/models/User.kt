package uz.techie.mexmash.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class User(
    @PrimaryKey
    val id:Int,
    val phone:String? = null,
    val birthday:String? = null,
    var first_name:String? = null,
    var last_name:String? = null,
    val token:String? = null,
    val region:String? = null,
    val districts:String? = null,
    val quarters:String? = null,
    val street:String? = null,
    val type:String? = null,
    val type_name:String? = null,
    val level_name:String? = null,
    val level_image:String? = null,
    val point:Long = 0,
    val total_kg:Long = 0
)
