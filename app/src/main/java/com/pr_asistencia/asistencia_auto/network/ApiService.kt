package com.pr_asistencia.asistencia_auto.network

import com.pr_asistencia.asistencia_auto.models.AttendanceRequest
import com.pr_asistencia.asistencia_auto.models.LoginRequest
import com.pr_asistencia.asistencia_auto.models.LoginResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    @POST("api/TokenAuth/Authenticate")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @POST("api/services/app/Attendance/CreateAttendance")
    suspend fun createAttendance(
        @Header("Authorization") token: String,
        @Body request: AttendanceRequest
    ): Response<ResponseBody>

    @GET("api/services/app/Attendance/GetAllAttendances")
    suspend fun getAllAttendances(
        @Header("Authorization") token: String,
        @Query("PageNumber") pageNumber: Int = 1,
        @Query("DateFrom") dateFrom: String = "",
        @Query("DateTo") dateTo: String = "",
        @Query("Order") order: String = "",
        @Query("OrderDirection") orderDirection: Int = 0,
        @Query("Keyword") keyword: String = "",
        @Query("SkipCount") skipCount: Int = 0,
        @Query("MaxResultCount") maxResultCount: Int = 0
    ): Response<ResponseBody>
}