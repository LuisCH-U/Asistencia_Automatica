package com.pr_asistencia.asistencia_auto.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    val api: ApiService by lazy {

        Retrofit.Builder()
            .baseUrl("https://api.portalempleado.softwareclock.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}