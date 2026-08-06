package com.pr_asistencia.asistencia_auto.manager

import com.pr_asistencia.asistencia_auto.App
import com.pr_asistencia.asistencia_auto.helper.NotificationHelper
import com.pr_asistencia.asistencia_auto.models.AttendanceRequest
import com.pr_asistencia.asistencia_auto.network.RetrofitClient
import java.time.OffsetDateTime

object AttendanceManager {

    suspend fun marcarAsistencia(): Boolean
    {
        return try {

            val prefs = App.instance.securePrefs()
            val marcaActual = OffsetDateTime.now()
            val ultimaMarca = prefs.getString("ultimaMarcaAsistencia", "")

            NotificationHelper.show(App.instance, "Asistencia automática", "Anterior: $ultimaMarca - Actual: $marcaActual")

            if (!ultimaMarca.isNullOrBlank())
            {
                val ultimaMarcaFecha = OffsetDateTime.parse(ultimaMarca)
                val minutosDesdeUltimaMarca = java.time.Duration.between(ultimaMarcaFecha, marcaActual).toMinutes()

                if (minutosDesdeUltimaMarca in 0..30)
                {
                    NotificationHelper.show(App.instance, "Asistencia automática", "Asistencia registrada hace $minutosDesdeUltimaMarca minutos.")
                    return true
                }
            }

            for (intento in 1..3)
            {
                val token = AuthManager.ensureToken() ?: return false

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

                if (attendanceResponse.isSuccessful)
                {
                    prefs.edit().putString("ultimaMarcaAsistencia", marcaActual.toString()).apply()
                    val saveAsistance = prefs.getString("ultimaMarcaAsistencia", "")
                    NotificationHelper.show(App.instance, "Asistencia automática", "Hora: $saveAsistance")
                    return true
                }

                if (attendanceResponse.code() == 401 && intento < 3)
                {
                    if (!AuthManager.reLogin()) return false
                    continue
                }

                return false
            }

            false

        } catch (e: Exception)
        {
            false
        }
    }
}
