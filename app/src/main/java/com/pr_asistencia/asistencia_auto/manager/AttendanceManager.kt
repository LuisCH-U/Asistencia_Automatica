package com.pr_asistencia.asistencia_auto.manager

import com.pr_asistencia.asistencia_auto.App
import com.pr_asistencia.asistencia_auto.helper.NotificationHelper
import com.pr_asistencia.asistencia_auto.models.AttendanceRequest
import com.pr_asistencia.asistencia_auto.models.LoginRequest
import com.pr_asistencia.asistencia_auto.network.RetrofitClient
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

object AttendanceManager {

    suspend fun marcarAsistencia():  Boolean
    {

        return try {

            val prefs = App.instance.securePrefs()
            val marcaActual = OffsetDateTime.now()
            val ultimaMarca = prefs.getString("ultimaMarcaAsistencia", "")
            NotificationHelper.show(App.instance, "Ultima Asistenicia", "Ultima marca = $ultimaMarca, Marca actual = $marcaActual")
            if (!ultimaMarca.isNullOrBlank()) {
                val ultimaMarcaFecha = OffsetDateTime.parse(ultimaMarca)
                val minutosDesdeUltimaMarca = java.time.Duration.between(ultimaMarcaFecha, marcaActual).toMinutes()

                if (minutosDesdeUltimaMarca in 0..30) {
                    NotificationHelper.show(App.instance, "Asistencia duplicada", "Ya se marcó asistencia hace $minutosDesdeUltimaMarca minutos. No se enviará otra marca.")
                    return true
                }
            }

            val tenant = prefs.getString("tenant", "") ?: ""
            val user = prefs.getString("user", "") ?: ""
            val password = prefs.getString("password", "") ?: ""
            val loginResponse = RetrofitClient.api.login(
                                    LoginRequest(
                                        tenantName = tenant,
                                        userNameOrEmailAddress = user,
                                        password = password,
                                        rememberClient = false
                                    )
                                )

            if (!loginResponse.isSuccessful)
            {
                return false
            }
            else if(loginResponse.code() == 401)
            {
                val nuevoToken = reLogin()

                if(nuevoToken != null)
                {
                    return marcarAsistencia()
                }
            }

            val token = loginResponse.body()?.result?.accessToken?: return false

            val attendanceResponse = RetrofitClient.api.createAttendance(
                                         "Bearer $token",
                                         AttendanceRequest(
                                             attendance = true,
                                             comments = null,
                                             costCenterId = null,
                                             issued = OffsetDateTime.now().toString(),
                                             latitude = null,
                                             longitude = null
                                         )
                                     )

            if (attendanceResponse.isSuccessful) {
                prefs.edit().putString("ultimaMarcaAsistencia", marcaActual.toString()).apply()
                val guardado = prefs.getString("ultimaMarcaAsistencia", "")
                NotificationHelper.show(App.instance,"Última asistencia guardada","Valor guardado = $guardado")
            }

            attendanceResponse.isSuccessful

        } catch (e: Exception)
        {
            false
        }
    }


    private suspend fun reLogin(): String?
    {
        return try {

            val prefs = App.instance.securePrefs()
            val user = prefs.getString("user","") ?: ""
            val password = prefs.getString("password","") ?: ""
            val tenant = prefs.getString("tenant","inlearning") ?: "inlearning"
            val body = LoginRequest(userNameOrEmailAddress = user, password = password, tenantName = tenant, rememberClient = false )
            val response = RetrofitClient.api.login(body)

            if (response.isSuccessful)
            {
                val token = response.body()?.result?.accessToken
                prefs.edit().putString("token",token).apply()
                token
            }
            else
            {
                null
            }

        } catch (e: Exception) {
            null
        }
    }
}