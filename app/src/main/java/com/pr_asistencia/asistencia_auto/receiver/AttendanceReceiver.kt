package com.pr_asistencia.asistencia_auto.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.pr_asistencia.asistencia_auto.helper.AlarmHelper
import com.pr_asistencia.asistencia_auto.helper.NotificationHelper
import com.pr_asistencia.asistencia_auto.manager.AttendanceManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class AttendanceReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent)
    {
        val tipo = intent.getIntExtra("tipo", 0)
        val now = Date()
        //Log.d("Asistencia","Receiver ejecutado tipo: $tipo, horaReal: $now")
        //NotificationHelper.show(context, "Asistencia", "Receiver ejecutado tipo: $tipo, horaReal: $now")

        CoroutineScope(Dispatchers.IO).launch {
            try
            {
                val prefs = context.getSharedPreferences("config", Context.MODE_PRIVATE)

                val horaEntrada = prefs.getString("horaEntrada", "08:25")
                val horaSalida = prefs.getString("horaSalida", "18:35")

                if (tipo == 100)
                {
                    val entrada = horaEntrada!!.split(":")
                    AlarmHelper.programarAlarma(context,entrada[0].toInt(),entrada[1].toInt(),100)
                    NotificationHelper.show(context,"Entrada - AttendanceReceiver","Asistencia programada correctamente: ${entrada[0]}:${entrada[1]}")
                }
                else if (tipo == 200)
                {
                    val salida = horaSalida!!.split(":")
                    AlarmHelper.programarAlarma(context,salida[0].toInt(),salida[1].toInt(),200)
                    NotificationHelper.show(context,"Salida - AttendanceReceiver","Asistencia programada correctamente: ${salida[0]}:${salida[1]}")
                }

                val activo = prefs.getBoolean("activo", false)
                val automatico = prefs.getBoolean("automatico", false)

                val calendar = Calendar.getInstance(TimeZone.getTimeZone("America/Lima"))
                val dayZone = calendar.get(Calendar.DAY_OF_WEEK)

                val diaActivo = when(dayZone) {
                    Calendar.MONDAY -> prefs.getBoolean("lunes", true)
                    Calendar.TUESDAY -> prefs.getBoolean("martes", true)
                    Calendar.WEDNESDAY -> prefs.getBoolean("miercoles", true)
                    Calendar.THURSDAY -> prefs.getBoolean("jueves", true)
                    Calendar.FRIDAY -> prefs.getBoolean("viernes", true)
                    Calendar.SATURDAY -> prefs.getBoolean("sabado", false)
                    Calendar.SUNDAY -> prefs.getBoolean("domingo", false)
                    else -> false
                }

                if (!diaActivo) {
                    Log.d("Dia activo", "No se marcará asistencia.")
                    NotificationHelper.show(context, "Dia activo", "No se marcará asistencia.")
                    return@launch
                }

                if (!activo || !automatico)
                {
                    Log.d("Inactivo","Asistencia marcada, solo pruebas tipo: $tipo - horaReal: $now")
                    NotificationHelper.show(context,"Inactivo","Asistencia marcadasolo pruebas tipo: $tipo - horaReal: $now")
                    return@launch
                }

                val ok = AttendanceManager.marcarAsistencia()

                if (ok) {
                    Log.d("Asistencia - OK", "Asistencia marcada correctamente - tipo:$tipo, horaReal: $now")
                    NotificationHelper.show(context, "Asistencia - OK", "Asistencia marcada correctamente - tipo: $tipo - horaReal: $now"
                    )
                } else {
                    Log.d("Asistencia - Error", "Error al marcar asistencia - tipo: $tipo, horaReal: $now")
                    NotificationHelper.show(context, "Asistencia - Error", "Error al marcar asistencia - tipo;:$tipo - horaReal: $now")
                }

            } catch (e: Exception)
            {
                Log.e("Asistencia - Ex", "Error en AttendanceReceiver", e)
                NotificationHelper.show(context, "Error AttendanceReceiver", e.message ?: "Error desconocido")
                e.printStackTrace()
            }
        }
    }
}