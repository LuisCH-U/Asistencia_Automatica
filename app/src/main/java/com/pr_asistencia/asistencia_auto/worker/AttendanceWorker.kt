package com.pr_asistencia.asistencia_auto.worker


import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.pr_asistencia.asistencia_auto.manager.AttendanceManager
import java.text.SimpleDateFormat
import java.util.*

class AttendanceWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {

        try {

            val prefs = applicationContext.getSharedPreferences(
                            "config",
                            Context.MODE_PRIVATE
                        )

            val activo = prefs.getBoolean(
                             "activo",
                             false
                         )

            val automatico = prefs.getBoolean(
                                 "automatico",
                                 false
                             )

            if (!activo || !automatico)
            {
                return Result.success()
            }

            val calendar = Calendar.getInstance()

            val day = calendar.get(Calendar.DAY_OF_WEEK)

            val diaActivo = when(day)
            {

                Calendar.MONDAY ->
                    prefs.getBoolean(
                        "lunes",
                        true
                    )

                Calendar.TUESDAY ->
                    prefs.getBoolean(
                        "martes",
                        true
                    )

                Calendar.WEDNESDAY ->
                    prefs.getBoolean(
                        "miercoles",
                        true
                    )

                Calendar.THURSDAY ->
                    prefs.getBoolean(
                        "jueves",
                        true
                    )

                Calendar.FRIDAY ->
                    prefs.getBoolean(
                        "viernes",
                        true
                    )

                else -> false
            }

            if (!diaActivo)
            {
                return Result.success()
            }

            val sdf = SimpleDateFormat(
                          "HH:mm",
                          Locale.getDefault()
                      )

            val horaActual = sdf.format(Date())

            val horaEntrada = prefs.getString(
                                  "horaEntrada",
                                  "08:00"
                              )

            val horaSalida = prefs.getString(
                                 "horaSalida",
                                 "18:00"
                             )

            if (horaActual == horaEntrada || horaActual == horaSalida)
            {
                AttendanceManager.marcarAsistencia()
            }

            return Result.success()

        }
        catch (e: Exception)
        {
            return Result.retry()
        }
    }
}