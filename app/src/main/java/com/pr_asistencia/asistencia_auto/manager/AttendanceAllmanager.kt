package com.pr_asistencia.asistencia_auto.manager

import com.pr_asistencia.asistencia_auto.App
import com.pr_asistencia.asistencia_auto.models.LoginRequest
import com.pr_asistencia.asistencia_auto.network.RetrofitClient

object AttendanceAllmanager
{
    suspend fun recuperarAsistencias(fechaInicio: String, fechaFin:String): String?
    {
        try
        {
            val prefs = App.instance.securePrefs()

            val tenant = prefs.getString("tenant", "") ?: ""
            val user = prefs.getString("user", "") ?: ""
            val password = prefs.getString("password", "") ?: ""

            val loginResponse = RetrofitClient.api.login(
                request = LoginRequest(
                    tenantName = tenant,
                    userNameOrEmailAddress = user,
                    password = password,
                    rememberClient = false
                )
            )

            if (!loginResponse.isSuccessful)
            {
                return null
            }

            val token = loginResponse.body()?.result?.accessToken?: return null

            val AssistanceResponse = RetrofitClient.api.getAllAttendances(
                "Bearer $token",
                1,
                fechaInicio,
                fechaFin,
                "",
                0,
                "",
                0,
                1000
            )

            if (!AssistanceResponse.isSuccessful)
            {
                return "Error assistance: ${AssistanceResponse.code()} - ${AssistanceResponse.errorBody()?.string()}"
            }

            return AssistanceResponse.body()?.string()
        }
        catch (e: Exception)
        {
            return e.message
        }
    }

}