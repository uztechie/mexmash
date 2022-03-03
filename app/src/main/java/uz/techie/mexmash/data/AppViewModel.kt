package uz.techie.mexmash.data

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import uz.techie.mexmash.models.*
import uz.techie.mexmash.util.Resource
import java.io.InterruptedIOException
import java.net.UnknownHostException
import javax.inject.Inject


@HiltViewModel
class AppViewModel @Inject constructor(
    private val repository: AppRepository
):ViewModel() {

    private val TAG = "AppViewModel"

    val checkPhone:MutableLiveData<Resource<Login>> = MutableLiveData()
    val checkSmsCode:MutableLiveData<Resource<Login>> = MutableLiveData()
    val checkAgentCode:MutableLiveData<Resource<Login>> = MutableLiveData()
    var register:MutableLiveData<Resource<Login>> = MutableLiveData()

    var profile:MutableLiveData<Resource<Login>> = MutableLiveData()
    var prizes:MutableLiveData<Resource<List<Prize>>> = MutableLiveData()
    var sliders:MutableLiveData<Resource<List<Slider>>> = MutableLiveData()
    var news:MutableLiveData<Resource<List<News>>> = MutableLiveData()
    var products:MutableLiveData<Resource<List<Product>>> = MutableLiveData()


    val regions:MutableLiveData<Resource<List<Region>>> = MutableLiveData()
    val districts:MutableLiveData<Resource<List<District>>> = MutableLiveData()
    val quarters:MutableLiveData<Resource<List<Quarter>>> = MutableLiveData()

    var promoCode:MutableLiveData<Resource<PromoCode>> = MutableLiveData()
    val promoCodeHistory:MutableLiveData<Resource<PromoCodeHistory>> = MutableLiveData()
    var updateProfileResponse:MutableLiveData<Resource<Login>> = MutableLiveData()





    //login
    fun checkPhone(phone:String) = viewModelScope.launch {
        checkPhone.postValue(Resource.Loading())
        try {
            val response = repository.checkPhone(phone)
            checkPhone.postValue(handleLogin(response))
        }catch (e: UnknownHostException) {
            checkPhone.postValue(Resource.Error("Интернетга боғланишда хатолик!"))
        } catch (e: InterruptedIOException) {
            checkPhone.postValue(Resource.Error("Интернетга боғланишда хатолик!"))
        } catch (e: Exception) {
            checkPhone.postValue(Resource.Error(message = e.toString()))
        }
    }

    fun checkSmsCode(phone:String, key:String) = viewModelScope.launch {
        checkSmsCode.postValue(Resource.Loading())
        try {
            val response = repository.checkSmsCode(phone, key)
            checkSmsCode.postValue(handleLogin(response))
        }catch (e: UnknownHostException) {
            checkSmsCode.postValue(Resource.Error("Интернетга боғланишда хатолик!"))
        } catch (e: InterruptedIOException) {
            checkSmsCode.postValue(Resource.Error("Интернетга боғланишда хатолик!"))
        } catch (e: Exception) {
            checkSmsCode.postValue(Resource.Error(message = e.toString()))
        }
    }

    fun checkAgentCode(phone:String, agentCode:String) = viewModelScope.launch {
        checkAgentCode.postValue(Resource.Loading())
        try {
            val response = repository.checkAgentCode(phone, agentCode)
            checkAgentCode.postValue(handleLogin(response))
        }catch (e: UnknownHostException) {
            checkAgentCode.postValue(Resource.Error("Интернетга боғланишда хатолик!"))
        } catch (e: InterruptedIOException) {
            checkAgentCode.postValue(Resource.Error("Интернетга боғланишда хатолик!"))
        } catch (e: Exception) {
            checkAgentCode.postValue(Resource.Error(message = e.toString()))
        }
    }

    fun register(map:HashMap<String, Any>) = viewModelScope.launch {
        register.postValue(Resource.Loading())
        try {
            val response = repository.register(map)
            register.postValue(handleLogin(response))
        }catch (e: UnknownHostException) {
            register.postValue(Resource.Error("Интернетга боғланишда хатолик!"))
        } catch (e: InterruptedIOException) {
            register.postValue(Resource.Error("Интернетга боғланишда хатолик!"))
        } catch (e: Exception) {
            register.postValue(Resource.Error(message = e.toString()))
        }
    }

    //regions
    fun loadRegions() = viewModelScope.launch {
        regions.postValue(Resource.Loading())
        try {
            val response = repository.loadRegions()
            regions.postValue(handleregions(response))
        }catch (e: UnknownHostException) {
            regions.postValue(Resource.Error("Интернетга боғланишда хатолик!"))
        } catch (e: InterruptedIOException) {
            regions.postValue(Resource.Error("Интернетга боғланишда хатолик!"))
        } catch (e: Exception) {
            regions.postValue(Resource.Error(message = e.toString()))
        }
    }
    fun loadDistricts() = viewModelScope.launch {
        districts.postValue(Resource.Loading())
        try {
            val response = repository.loadDistricts()
            districts.postValue(handleDistricts(response))
        }catch (e: UnknownHostException) {
            districts.postValue(Resource.Error("Интернетга боғланишда хатолик!"))
        } catch (e: InterruptedIOException) {
            districts.postValue(Resource.Error("Интернетга боғланишда хатолик!"))
        } catch (e: Exception) {
            districts.postValue(Resource.Error(message = e.toString()))
        }
    }
    fun loadQuarters() = viewModelScope.launch {
        quarters.postValue(Resource.Loading())
        try {
            val response = repository.loadQuarters()
            quarters.postValue(handleQuarters(response))
        }catch (e: UnknownHostException) {
            quarters.postValue(Resource.Error("Интернетга боғланишда хатолик!"))
        } catch (e: InterruptedIOException) {
            quarters.postValue(Resource.Error("Интернетга боғланишда хатолик!"))
        } catch (e: Exception) {
            quarters.postValue(Resource.Error(message = e.toString()))
        }
    }

    //profile
    fun loadProfile(token:String) = viewModelScope.launch {
        profile.postValue(Resource.Loading())
        try {
            val response = repository.loadProfile(token)
            profile.postValue(handleLogin(response))
        }catch (e: UnknownHostException) {
            profile.postValue(Resource.Error("Интернетга боғланишда хатолик!"))
        } catch (e: InterruptedIOException) {
            profile.postValue(Resource.Error("Интернетга боғланишда хатолик!"))
        } catch (e: Exception) {
            profile.postValue(Resource.Error(message = e.toString()))
        }
    }

    fun loadPrizes(token:String) = viewModelScope.launch {
        prizes.postValue(Resource.Loading())
        try {
            val response = repository.loadPrizes(token)
            prizes.postValue(handlePrizes(response))
        }catch (e: UnknownHostException) {
            prizes.postValue(Resource.Error("Интернетга боғланишда хатолик!"))
        } catch (e: InterruptedIOException) {
            prizes.postValue(Resource.Error("Интернетга боғланишда хатолик!"))
        } catch (e: Exception) {
            prizes.postValue(Resource.Error(message = e.toString()))
        }
    }

    fun loadSliders(token:String) = viewModelScope.launch {
        sliders.postValue(Resource.Loading())
        try {
            val response = repository.loadSliders(token)
            sliders.postValue(handleSliders(response))
        }catch (e: UnknownHostException) {
            sliders.postValue(Resource.Error("Интернетга боғланишда хатолик!"))
        } catch (e: InterruptedIOException) {
            sliders.postValue(Resource.Error("Интернетга боғланишда хатолик!"))
        } catch (e: Exception) {
            sliders.postValue(Resource.Error(message = e.toString()))
        }
    }


    fun loadNews(token: String) = viewModelScope.launch {
        news.postValue(Resource.Loading())
        try {
            val response = repository.loadNews(token)
            news.postValue(handleNews(response))
        }catch (e: UnknownHostException) {
            news.postValue(Resource.Error("Интернетга боғланишда хатолик!"))
        } catch (e: InterruptedIOException) {
            news.postValue(Resource.Error("Интернетга боғланишда хатолик!"))
        } catch (e: Exception) {
            news.postValue(Resource.Error(message = e.toString()))
        }
    }


    fun loadProducts(token: String) = viewModelScope.launch {
        products.postValue(Resource.Loading())
        try {
            val response = repository.loadProducts(token)
            products.postValue(handleProducts(response))
        }catch (e: UnknownHostException) {
            products.postValue(Resource.Error("Интернетга боғланишда хатолик!"))
        } catch (e: InterruptedIOException) {
            products.postValue(Resource.Error("Интернетга боғланишда хатолик!"))
        } catch (e: Exception) {
            products.postValue(Resource.Error(message = e.toString()))
        }
    }


    //send bonus code
    fun sendPromoCode(token: String, code:String) = viewModelScope.launch {
        Log.d(TAG, "sendBonusCode: "+token)
        promoCode.postValue(Resource.Loading())
        try {
            val response = repository.sendBonusCode(token, code)
            promoCode.postValue(handleSendBonusCode(response))
        }catch (e: UnknownHostException) {
            promoCode.postValue(Resource.Error("Интернетга боғланишда хатолик!"))
        } catch (e: InterruptedIOException) {
            promoCode.postValue(Resource.Error("Интернетга боғланишда хатолик!"))
        } catch (e: Exception) {
            promoCode.postValue(Resource.Error(message = e.toString()))
        }
    }

    //send bonus code history
    fun loadPromoCodeHistory(token: String) = viewModelScope.launch {
        promoCodeHistory.postValue(Resource.Loading())
        try {
            val response = repository.loadBonusCodeHistory(token)
            promoCodeHistory.postValue(handleSendBonusCodeHistory(response))
        }catch (e: UnknownHostException) {
            promoCodeHistory.postValue(Resource.Error("Интернетга боғланишда хатолик!"))
        } catch (e: InterruptedIOException) {
            promoCodeHistory.postValue(Resource.Error("Интернетга боғланишда хатолик!"))
        } catch (e: Exception) {
            promoCodeHistory.postValue(Resource.Error(message = e.toString()))
        }
    }

    //update profile
    fun updateProfile(token: String, map: HashMap<String, Any>) = viewModelScope.launch {
        updateProfileResponse.postValue(Resource.Loading())
        try {
            val response = repository.updateProfile(token, map)
            updateProfileResponse.postValue(handleUpdateProfile(response))
        }catch (e: UnknownHostException) {
            updateProfileResponse.postValue(Resource.Error("Интернетга боғланишда хатолик!"))
        } catch (e: InterruptedIOException) {
            updateProfileResponse.postValue(Resource.Error("Интернетга боғланишда хатолик!"))
        } catch (e: Exception) {
            updateProfileResponse.postValue(Resource.Error(message = e.toString()))
        }
    }









    //handles
    private fun handleLogin(response:Response<Login>):Resource<Login>{
        Log.d(TAG, "handleLogin: "+response.body())
        if (response.isSuccessful){
            response.body()?.let {
                return Resource.Success(it)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleregions(response:Response<List<Region>>):Resource<List<Region>>{
        Log.d(TAG, "handleregions: "+response.body())
        if (response.isSuccessful){
            response.body()?.let {
                return Resource.Success(it)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleDistricts(response:Response<List<District>>):Resource<List<District>>{
        Log.d(TAG, "handleDistricts: "+response.body())
        if (response.isSuccessful){
            response.body()?.let {
                return Resource.Success(it)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleQuarters(response:Response<List<Quarter>>):Resource<List<Quarter>>{
        Log.d(TAG, "handleQuarters: "+response.body())
        if (response.isSuccessful){
            response.body()?.let {
                return Resource.Success(it)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleSliders(response:Response<List<Slider>>):Resource<List<Slider>>{
        Log.d(TAG, "handleSliders: "+response.body())
        if (response.isSuccessful){
            response.body()?.let {
                return Resource.Success(it)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handlePrizes(response:Response<List<Prize>>):Resource<List<Prize>>{
        Log.d(TAG, "handlePrizes: "+response.body())
        if (response.isSuccessful){
            response.body()?.let {
                return Resource.Success(it)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleNews(response:Response<List<News>>):Resource<List<News>>{
        Log.d(TAG, "handleNews: "+response.body())
        if (response.isSuccessful){
            response.body()?.let {
                return Resource.Success(it)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleProducts(response:Response<List<Product>>):Resource<List<Product>>{
        Log.d(TAG, "handleProducts: "+response.body())
        if (response.isSuccessful){
            response.body()?.let {
                return Resource.Success(it)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleSendBonusCode(response:Response<PromoCode>):Resource<PromoCode>{
        Log.d(TAG, "handleSendBonusCode: "+response.body())
        if (response.isSuccessful){
            response.body()?.let {
                return Resource.Success(it)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleSendBonusCodeHistory(response:Response<PromoCodeHistory>):Resource<PromoCodeHistory>{
        Log.d(TAG, "handleSendBonusCodeHistory: "+response.body())
        if (response.isSuccessful){
            response.body()?.let {
                return Resource.Success(it)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleUpdateProfile(response:Response<Login>):Resource<Login>{
        Log.d(TAG, "handleUpdateProfile: "+response.body())
        if (response.isSuccessful){
            response.body()?.let {
                return Resource.Success(it)
            }
        }
        return Resource.Error(response.message())
    }





    //database

    fun insertRegions(list:List<Region>) = viewModelScope.launch {
        repository.insertRegions(list)
    }
    fun insertDistricts(list:List<District>) = viewModelScope.launch {
        repository.insertDistricts(list)
    }
    fun insertQuarters(list:List<Quarter>) = viewModelScope.launch {
        repository.insertQuarters(list)
    }
    fun getRegions() = repository.getRegions()
    fun getDistricts(id:Int) = repository.getDistricts(id)
    fun getQuarters(id:Int) = repository.getQuarters(id)


    //profile
    fun insertUser(user: User) = viewModelScope.launch {
        repository.insertUser(user)
    }
    fun deleteUser() = viewModelScope.launch {
        repository.deleteUser()
    }
    fun getUserLive() = repository.getUserLive()
    fun getUser() = repository.getUser()


    //slider
    fun insertSliders(list:List<Slider>) = viewModelScope.launch {
        repository.insertSliders(list)
    }
    fun getSliders() = repository.getSliders()


    //prize
    fun insertPrizes(list:List<Prize>) = viewModelScope.launch {
        repository.insertPrizes(list)
    }
    fun getPrizes() = repository.getPrizes()
    fun getPrizeByPoint(point:Long) = repository.getPrizesByPoint(point)


    //news
    fun insertNews(list:List<News>) = viewModelScope.launch {
        repository.insertNews(list)
    }
    fun getNews() = repository.getNews()


    //products
    fun insertProducts(list:List<Product>) = viewModelScope.launch {
        repository.insertProducts(list)
    }
    fun getProducts() = repository.getProducts()



}