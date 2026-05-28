package com.pr_asistencia.asistencia_auto.manager

import com.pr_asistencia.asistencia_auto.App
import com.pr_asistencia.asistencia_auto.models.AttendanceRequest
import com.pr_asistencia.asistencia_auto.models.LoginRequest
import com.pr_asistencia.asistencia_auto.network.RetrofitClient
import java.time.OffsetDateTime

object AttendanceManager {

    suspend fun marcarAsistencia(): Boolean {

        return try {

            val prefs = App.instance.securePrefs()

            val tenant = prefs.getString("tenant", "") ?: ""

            val usuario = prefs.getString("usuario", "") ?: ""

            val password = prefs.getString("password", "") ?: ""

            val loginResponse = RetrofitClient.api.login(
                                    LoginRequest(
                                        tenantName = tenant,
                                        userNameOrEmailAddress = usuario,
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
                    marcarAsistencia()
                }
            }

            val token = loginResponse.body()
                        ?.result
                        ?.accessToken
                        ?: return false

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
            attendanceResponse.isSuccessful

        } catch (e: Exception)
        {
            false
        }
    }

    private suspend fun reLogin(): String? {

        return try {

            val prefs = App.instance.securePrefs()

            val user = prefs.getString(
                "user",
                ""
            ) ?: ""

            val password = prefs.getString(
                "password",
                ""
            ) ?: ""

            val tenant = prefs.getString(
                "tenant",
                "inlearning"
            ) ?: "inlearning"

            val body = LoginRequest(
                userNameOrEmailAddress = user,
                password = password,
                tenantName = tenant,
                rememberClient = false
            )

            val response = RetrofitClient.api
                    .login(body)

            if (response.isSuccessful) {

                val token = response.body()?.result?.accessToken

                prefs.edit().putString(
                    "token",
                    token
                ).apply()

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