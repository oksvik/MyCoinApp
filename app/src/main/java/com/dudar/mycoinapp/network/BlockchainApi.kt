package com.dudar.mycoinapp.network

import com.dudar.mycoinapp.BASE_URL
import com.dudar.mycoinapp.model.ChartsResponse
import io.reactivex.Single
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface BlockchainApi {

    @GET("charts/market-price")
    fun getMarketPrice(@Query ("timespan") timespan:String) : Single<ChartsResponse>



    companion object {

        fun create() : BlockchainApi{

            val httpClient = OkHttpClient().newBuilder()
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            httpClient.addInterceptor(interceptor)
            val okHttpClient = httpClient.build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(BlockchainApi::class.java)
        }
    }
}