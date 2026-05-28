package com.pr_asistencia.asistencia_auto.network

import com.pr_asistencia.asistencia_auto.models.AttendanceRequest
import com.pr_asistencia.asistencia_auto.models.LoginRequest
import com.pr_asistencia.asistencia_auto.models.LoginResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

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
}