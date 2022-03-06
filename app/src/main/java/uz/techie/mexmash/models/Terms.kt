package uz.techie.mexmash.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "terms")
data class Terms(

    @PrimaryKey(autoGenerate = true)
    val id:Int,
    val status: Int? = null,
    val data: String? = null,
    val updated_at: String? = null
)