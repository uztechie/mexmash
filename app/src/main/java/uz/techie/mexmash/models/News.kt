package uz.techie.mexmash.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "news")
data class News(
    @PrimaryKey
    val id:Int,
    val title:String,
    val desc:String,
    val image:String,
    val created_at:String
)
