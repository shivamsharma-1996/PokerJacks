package com.gtgt.pokerjacks.retrofit

import com.github.salomonbrys.kotson.get
import com.github.salomonbrys.kotson.string
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.gtgt.pokerjacks.BuildConfig
import com.gtgt.pokerjacks.extensions.getModel
import com.gtgt.pokerjacks.extensions.log
import com.gtgt.pokerjacks.extensions.retrieveString
import com.gtgt.pokerjacks.retrofit.SocketFactory.sslSocketFactory
import com.gtgt.pokerjacks.retrofit.SocketFactory.trustManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object WebServicesWallet {
    const val baseUrlWalletService = com.gtgt.pokerjacks.BuildConfig.HOME_URL_WALLET

    private val okhttpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(1000, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.MINUTES)
        .sslSocketFactory(sslSocketFactory!!, trustManager!!)
        .addInterceptor { chain ->
            val original = chain.request()

            val request = original.newBuilder()
                .addHeader(
                    "Authorization",
                    getModel<JsonElement>("loginInfo")?.let {
                        if (com.gtgt.pokerjacks.BuildConfig.DEBUG) {
                            log("Authorization", "Bearer " + it["token"].string)
                        }

                        if (original.url.toUri().path.contains(
                                "createUser"
                            ) || original.url.toUri().path.contains(
                                "verifyOTP"
                            ) || original.url.toUri().path.startsWith(
                                "createMPIN"
                            ) || original.url.toUri().path.contains(
                                "login"
                            )
                        ) ""
                        else if (original.url.toUri().path.contains("walletService")) "Token " + it["token"].string
                        else if (original.url.toUri().path.contains("bonusService")) "Token " + it["token"].string
                        else "Token " + it["token"].string
                    }
                        ?: ""
                )
                .addHeader("DeviceId", retrieveString("UNIQUE_ID"))
                .method(original.method, original.body)
                .build()

            log("AddingAuth", "webServices")

            chain.proceed(request)
        }

    val retrofitWalletService = Retrofit.Builder()
        .baseUrl(baseUrlWalletService)
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().serializeNulls().create()))
        .client(okhttpClient.apply {
            if (com.gtgt.pokerjacks.BuildConfig.DEBUG) {
                addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            }
        }.build()).build()
}