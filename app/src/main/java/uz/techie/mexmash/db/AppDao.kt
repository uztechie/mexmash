package uz.techie.airshop.db

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import uz.techie.mexmash.models.*

@Dao
interface AppDao {

    //profile
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Query("delete from user")
    suspend fun deleteUser()

    @Transaction
    suspend fun deleteInsertUser(user: User){
        deleteUser()
        insertUser(user)
    }

    @Query("select * from user limit 1")
    fun getUser():List<User>

    @Query("select * from user limit 1")
    fun getUserLive():LiveData<List<User>>




    //regions
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRegions(list:List<Region>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDistricts(list:List<District>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuarters(list:List<Quarter>)


    @Query("select * from region order by name")
    fun getRegions():LiveData<List<Region>>

    @Query("select * from district where region=:id order by name")
    fun getDistricts(id:Int):LiveData<List<District>>

    @Query("select * from quarter where district=:id order by name")
    fun getQuarters(id:Int):LiveData<List<Quarter>>


    //slider
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSliders(list:List<Slider>)

    @Query("delete from slider")
    suspend fun deleteSliders()

    @Transaction
    suspend fun deleteAndInsertSliders(list:List<Slider>){
        deleteSliders()
        insertSliders(list)
    }

    @Query("select * from slider")
    fun getSliders():LiveData<List<Slider>>


    //Prize
    @Insert
    suspend fun insertPrizes(list:List<Prize>)

    @Query("delete from prize")
    suspend fun deletePrizes()

    @Transaction
    suspend fun deleteAndInsertPrizes(list:List<Prize>){
        deletePrizes()
        insertPrizes(list)
    }

    @Query("select * from prize order by point desc")
    fun getPrizes():LiveData<List<Prize>>

    @Query("select * from prize where point>=:point and id>=0 order by point asc limit 1")
    fun getPrizeByPoint(point:Long):LiveData<List<Prize>>


    //products
    @Insert
    suspend fun insertProducts(list:List<Product>)

    @Query("delete from product")
    suspend fun deleteProducts()

    @Transaction
    suspend fun deleteAndInsertProducts(list:List<Product>){
        deleteProducts()
        insertProducts(list)
    }

    @Query("select * from product  order by point desc")
    fun getProducts():LiveData<List<Product>>


    //news
    @Insert
    suspend fun insertNews(list:List<News>)

    @Query("delete from news")
    suspend fun deleteNews()

    @Transaction
    suspend fun deleteAndInsertNews(list:List<News>){
        deleteNews()
        insertNews(list)
    }

    @Query("select * from news  order by created_at desc")
    fun getNews():LiveData<List<News>>

    //terms
    @Insert
    suspend fun insertTerms(term:Terms)

    @Query("delete from terms")
    suspend fun deleteTerms()

    @Transaction
    suspend fun deleteAndInsertTerms(term: Terms){
        deleteTerms()
        insertTerms(term)
    }

    @Query("select * from terms limit 1")
    fun getTerms():LiveData<List<Terms>>




}