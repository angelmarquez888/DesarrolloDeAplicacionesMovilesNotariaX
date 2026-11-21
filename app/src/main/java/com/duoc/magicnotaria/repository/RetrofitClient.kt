package com.duoc.magicnotaria.repository

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val URL_BASE = "https://x8ki-letl-twmt.n7.xano.io/api:FYyAtHhC/"

    val notariaApi: NotariaApi by lazy {
        Retrofit.Builder()
            .baseUrl(URL_BASE)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NotariaApi::class.java)
    }
}