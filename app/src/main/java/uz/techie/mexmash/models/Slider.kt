package uz.techie.mexmash.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "slider")
data class Slider(
    @PrimaryKey
    val id:Int,
    val image:String,
    val type:Int,
)
