package uz.techie.mexmash.data

import uz.techie.airshop.db.AppDatabase
import uz.techie.airshop.network.RetrofitApi
import uz.techie.mexmash.models.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppRepository @Inject constructor(
    private val retrofitApi: RetrofitApi,
    private val db: AppDatabase
) {
    private val dao = db.AppDao()

    suspend fun checkPhone(phone:String) = retrofitApi.checkPhone(phone = phone)
    suspend fun checkSmsCode(phone:String, key:String) = retrofitApi.checkSmsCode(phone = phone, key = key)
    suspend fun checkAgentCode(phone:String, agentCode:String) = retrofitApi.checkAgentCode(phone = phone, agentCode = agentCode)
    suspend fun register(map:HashMap<String, Any>) = retrofitApi.register(map = map)

    //regions
    suspend fun loadRegions() = retrofitApi.loadRegions()
    suspend fun loadDistricts() = retrofitApi.loadDistricts()
    suspend fun loadQuarters() = retrofitApi.loadQuarters()

    //profile
    suspend fun loadProfile(token:String) = retrofitApi.loadProfile(token)
    suspend fun loadSliders(token: String) = retrofitApi.loadSliders(token)
    suspend fun loadPrizes(token: String) = retrofitApi.loadPrizes(token)

    //news
    suspend fun loadNews(token: String) = retrofitApi.loadNews(token)

    //product
    suspend fun loadProducts(token: String) = retrofitApi.loadProducts(token)

    // send bonus code
    suspend fun sendBonusCode(token: String, code:String) = retrofitApi.sendBonusCode(token, code)

    //bonus history
    suspend fun loadBonusCodeHistory(token: String) = retrofitApi.loadBonusCodeHistory(token)

    //update profile
    suspend fun updateProfile(token: String, map: HashMap<String, Any>) = retrofitApi.updateProfile(token, map)

    //terms
    suspend fun loadTerms(token: String) = retrofitApi.loadTerms(token)

    //telegram
    suspend fun loadTelegram() = retrofitApi.loadTelegram()


    //database
    suspend fun insertRegions(list:List<Region>) = dao.insertRegions(list)
    suspend fun insertDistricts(list:List<District>) = dao.insertDistricts(list)
    suspend fun insertQuarters(list:List<Quarter>) = dao.insertQuarters(list)

    fun getRegions() = dao.getRegions()
    fun getDistricts(id:Int) = dao.getDistricts(id)
    fun getQuarters(id:Int) = dao.getQuarters(id)

    //profile
    suspend fun insertUser(user: User) = dao.deleteInsertUser(user)
    suspend fun deleteUser() = dao.deleteUser()
    fun getUserLive() = dao.getUserLive()
    fun getUser() = dao.getUser()

    //slider
    suspend fun insertSliders(list:List<Slider>) = dao.deleteAndInsertSliders(list)
    fun getSliders() = dao.getSliders()


    //prize
    suspend fun insertPrizes(list:List<Prize>) = dao.deleteAndInsertPrizes(list)
    fun getPrizes() = dao.getPrizes()
    fun getPrizesByPoint(point:Long) = dao.getPrizeByPoint(point)

    //news
    suspend fun insertNews(list:List<News>) = dao.deleteAndInsertNews(list)
    fun getNews() = dao.getNews()

    //terms
    suspend fun insertTerms(terms: Terms) = dao.deleteAndInsertTerms(terms)
    fun getTerms() = dao.getTerms()

    //product
    suspend fun insertProducts(list:List<Product>) = dao.deleteAndInsertProducts(list)
    fun getProducts() = dao.getProducts()


}