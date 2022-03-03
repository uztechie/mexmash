package uz.techie.mexmash.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "product")
data class Product(

    @PrimaryKey
    val id:Int,
    val name:String,
    val desc:String,
    val code:String,
    val image:String,
    val point:Int,
    val dealer_point:Int,
    val is_active:String,
    val created_at:String

)
