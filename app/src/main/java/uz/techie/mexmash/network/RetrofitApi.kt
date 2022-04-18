package uz.techie.airshop.network

import retrofit2.Response
import retrofit2.http.*
import uz.techie.mexmash.models.*
import uz.techie.mexmash.util.Constants

interface RetrofitApi {

    @FormUrlEncoded
    @POST("send-sms/")
    suspend fun checkPhone(
        @Header("MyToken") myToken: String = Constants.MY_TOKEN,
        @Field("phone_number") phone: String
    ): Response<Login>

    @FormUrlEncoded
    @POST("check-code/")
    suspend fun checkSmsCode(
        @Header("MyToken") myToken: String = Constants.MY_TOKEN,
        @Field("phone_number") phone: String,
        @Field("key") key: String
    ): Response<Login>


    @FormUrlEncoded
    @POST("check-agent-code/")
    suspend fun checkAgentCode(
        @Header("MyToken") myToken: String = Constants.MY_TOKEN,
        @Field("phone_number") phone: String,
        @Field("agent_code") agentCode: String
    ): Response<Login>

    @FormUrlEncoded
    @POST("sign-up/")
    suspend fun register(
        @Header("MyToken") myToken: String = Constants.MY_TOKEN,
        @FieldMap map:HashMap<String, Any>
    ): Response<Login>


    //regions
    @GET("regions/")
    suspend fun loadRegions(
        @Header("MyToken") myToken: String = Constants.MY_TOKEN
    ):Response<List<Region>>

    @GET("districts/")
    suspend fun loadDistricts(
        @Header("MyToken") myToken: String = Constants.MY_TOKEN
    ):Response<List<District>>

    @GET("quarters/")
    suspend fun loadQuarters(
        @Header("MyToken") myToken: String = Constants.MY_TOKEN
    ):Response<List<Quarter>>


    //profile
    @GET("profile/")
    suspend fun loadProfile(
        @Header("Authorization") token: String,
        @Header("MyToken") myToken: String = Constants.MY_TOKEN
    ): Response<Login>

    @GET("slider/")
    suspend fun loadSliders(
        @Header("Authorization") token: String,
        @Header("MyToken") myToken: String = Constants.MY_TOKEN
    ): Response<List<Slider>>

    @GET("tropht/")
    suspend fun loadPrizes(
        @Header("Authorization") token: String,
        @Header("MyToken") myToken: String = Constants.MY_TOKEN
    ): Response<List<Prize>>




    //news
    @GET("news-list/")
    suspend fun loadNews(
        @Header("Authorization") token: String,
        @Header("MyToken") myToken: String = Constants.MY_TOKEN
    ): Response<List<News>>


    //product
    @GET("products-list/")
    suspend fun loadProducts(
        @Header("Authorization") token: String,
        @Header("MyToken") myToken: String = Constants.MY_TOKEN
    ): Response<List<Product>>


    //send bonus code
    @FormUrlEncoded
    @POST("bonus-code/")
    suspend fun sendBonusCode(
        @Header("Authorization") token: String,
        @Field("code") code:String,
        @Header("MyToken") myToken: String = Constants.MY_TOKEN
    ): Response<PromoCode>

    //bonus code history
    @GET("bonus-code-history/")
    suspend fun loadBonusCodeHistory(
        @Header("Authorization") token: String,
        @Header("MyToken") myToken: String = Constants.MY_TOKEN
    ): Response<PromoCodeHistory>


    //update profile
    @FormUrlEncoded
    @POST("edit-profile/")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @FieldMap map:HashMap<String, Any>,
        @Header("MyToken") myToken: String = Constants.MY_TOKEN
    ): Response<Login>


    //terms
    @GET("privacy-policy/")
    suspend fun loadTerms(
        @Header("Authorization") token: String,
        @Header("MyToken") myToken: String = Constants.MY_TOKEN
    ): Response<Terms>

    //telegram
    @GET("data/")
    suspend fun loadTelegram(
        @Header("MyToken") myToken: String = Constants.MY_TOKEN
    ): Response<TelegramResponse>




}