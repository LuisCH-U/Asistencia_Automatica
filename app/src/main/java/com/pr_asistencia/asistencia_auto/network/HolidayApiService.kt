package com.pr_asistencia.asistencia_auto.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface HolidayApiService {

    @GET("api/v3/PublicHolidays/{year}/{country}")
    suspend fun getHolidays(
        @Path("year") year: Int,
        @Path("country") country: String = "PE"
    ): Response<List<HolidayResponse>>
}
