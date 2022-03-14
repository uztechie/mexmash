package uz.techie.mexmash.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "product")
data class Product(

    @PrimaryKey
    val id:Int,
    val name:String? = null,
    val desc:String? = null,
    val code:String? = null,
    val image:String? = null,
    val point:Int? = null,
    val dealer_point:Int? = null,
    val is_active:String? = null,
    val created_at:String? = null

)
