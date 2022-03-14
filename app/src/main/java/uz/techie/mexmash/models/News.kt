package uz.techie.mexmash.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "news")
data class News(
    @PrimaryKey
    val id:Int,
    val title:String? = null,
    val desc:String? = null,
    val image:String? = null,
    val created_at:String? = null
)
