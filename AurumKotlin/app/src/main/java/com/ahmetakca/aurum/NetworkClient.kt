package com.ahmetakca.aurum

import android.content.Context
import com.ahmetakca.aurum.service.ApiService
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object NetworkClient {
    private const val BASE_URL = "https://aurum.fastapicloud.dev/"
    fun createApiService(): ApiService {
        val gson = GsonBuilder().create()
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build().create(ApiService::class.java)
    }
}