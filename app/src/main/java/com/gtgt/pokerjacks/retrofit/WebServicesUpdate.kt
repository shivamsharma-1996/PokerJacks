package com.gtgt.pokerjacks.retrofit

import com.google.gson.GsonBuilder
import com.gtgt.pokerjacks.BuildConfig
import com.gtgt.pokerjacks.retrofit.SocketFactory.sslSocketFactory
import com.gtgt.pokerjacks.retrofit.SocketFactory.trustManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object WebServicesUpdate {
    const val baseUrl = BuildConfig.HOME_URL_UPDATE

    private val okhttpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(1000, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.MINUTES)
        .sslSocketFactory(sslSocketFactory!!, trustManager!!)
        .addInterceptor { chain ->
            val original = chain.request()

            val request = original.newBuilder()
                .method(original.method, original.body)
                .build()

//            log("AddingAuth", "webServices")

            chain.proceed(request)
        }


    val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ").serializeNulls().create()))
        .client(okhttpClient.apply {
            if (BuildConfig.DEBUG) {
                addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            }
        }.build()).build()
}