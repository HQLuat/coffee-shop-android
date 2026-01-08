package vn.edu.hcmuaf.fit.ttltmobile.data.api

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import vn.edu.hcmuaf.fit.ttltmobile.BuildConfig
import vn.edu.hcmuaf.fit.ttltmobile.data.api.service.AuthApiService
import vn.edu.hcmuaf.fit.ttltmobile.data.api.service.CartApiService
import java.util.concurrent.TimeUnit

object ApiConfig {
    const val BASE_URL = BuildConfig.BASE_URL

    private fun provideOkHttpClient(context: Context? = null): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val builder = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)

        context?.let {
            builder.addInterceptor(AuthInterceptor(it))
        }

        builder.addInterceptor(loggingInterceptor)

        return builder.build()
    }

    private fun getRetrofit(context: Context? = null): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(provideOkHttpClient(context))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // No token
    fun <T> createService(serviceClass: Class<T>): T {
        return getRetrofit().create(serviceClass)
    }

    // Has token
    fun <T> createService(serviceClass: Class<T>, context: Context): T {
        return getRetrofit(context).create(serviceClass)
    }

    // Service providers
    fun getAuthService(): AuthApiService {
        return getRetrofit().create(AuthApiService::class.java)
    }

    fun getAuthService(context: Context): AuthApiService {
        return getRetrofit(context).create(AuthApiService::class.java)
    }

    // Cart Service
    fun getCartService(context: Context): CartApiService {
        return getRetrofit(context).create(CartApiService::class.java)
    }
}