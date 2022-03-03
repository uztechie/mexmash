package uz.techie.airshop.db

import android.transition.Slide
import androidx.room.Database
import androidx.room.RoomDatabase
import uz.techie.mexmash.models.*

@Database(
    entities = [
        User::class,
        Region::class,
        District::class,
        Quarter::class,
        Slider::class,
        Prize::class,
        News::class,
        Product::class], version = 7
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun AppDao(): AppDao
}