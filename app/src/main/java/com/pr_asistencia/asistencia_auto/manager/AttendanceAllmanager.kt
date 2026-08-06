package com.pr_asistencia.asistencia_auto.manager

import com.pr_asistencia.asistencia_auto.network.RetrofitClient

object AttendanceAllmanager
{
    suspend fun recuperarAsistencias(fechaInicio: String, fechaFin: String): String?
    {
        try
        {
            for (intento in 1..3)
            {
                val token = AuthManager.ensureToken() ?: return null

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

                if (AssistanceResponse.isSuccessful)
                {
                    return AssistanceResponse.body()?.string()
                }

                if (AssistanceResponse.code() == 401 && intento < 3)
                {
                    if (!AuthManager.reLogin()) return null
                    continue
                }

                return "Error assistance: ${AssistanceResponse.code()} - ${AssistanceResponse.errorBody()?.string()}"
            }

            return null
        }
        catch (e: Exception)
        {
            return e.message
        }
    }
}
