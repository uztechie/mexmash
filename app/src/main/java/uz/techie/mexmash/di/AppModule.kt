package uz.techie.mexmash.di

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import uz.techie.airshop.db.AppDatabase
import uz.techie.airshop.network.RetrofitApi
import uz.techie.mexmash.util.Constants
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRetrofitApi(): RetrofitApi =
        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(httpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(RetrofitApi::class.java)


    @Provides
    @Singleton
    fun provideDatabase(app: Application): AppDatabase =
        Room.databaseBuilder(app, AppDatabase::class.java, "shop.db")
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()


    fun httpClient(): OkHttpClient {
        val builder: OkHttpClient.Builder = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .callTimeout(15, TimeUnit.SECONDS)

        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        builder.addInterceptor(interceptor)
        val client: OkHttpClient = builder.build()
        return client
    }
}