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
import java.util.Calendar
import java.util.Date
import java.util.TimeZone

class AttendanceReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent)
    {
        val tipo = intent.getIntExtra("tipo", 0)
        val now = Date()

        Log.d("Asistencia","Receiver ejecutado")
        Log.d("Alarma","AttendanceReceiver ejecutado")
        Log.d("Asistencia","Receiver ejecutado tipo=$tipo")
        NotificationHelper.show(context, "Asistencia","Receiver ejecutado tipo=$tipo horaReal=$now")

        CoroutineScope(Dispatchers.IO).launch {
            try
            {
                val prefs = context.getSharedPreferences("config", Context.MODE_PRIVATE)

                val horaEntrada = prefs.getString("horaEntrada", "08:25")
                val horaSalida = prefs.getString("horaSalida", "18:35")

                val activo = prefs.getBoolean("activo", false)
                val automatico = prefs.getBoolean("automatico", false)

                val calendar = Calendar.getInstance(TimeZone.getTimeZone("America/Lima"))
                val dayZone = calendar.get(Calendar.DAY_OF_WEEK)
                val dayDefault = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)

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
                    return@launch
                }

                val tipo = intent.getIntExtra("tipo", 0)

                if (!activo || !automatico)
                {
                    Log.d("Asistencia(AttendanceReceiver)","Asistencia marcada para pruebas")
                    NotificationHelper.show(context,"Asistencia(AttendanceReceiver)","Asistencia marcada para pruebas")
                    return@launch
                }

                AttendanceManager.marcarAsistencia()
                Log.d("Asistencia(AttendanceReceiver)","Asistencia marcada con éxito")
                NotificationHelper.show(context,"Asistencia(AttendanceReceiver)","Asistencia marcada con éxito")

                if (tipo == 100)
                {
                    val entrada = horaEntrada!!.split(":")
                    AlarmHelper.programarAlarma(context,entrada[0].toInt(),entrada[1].toInt(),100)
                    NotificationHelper.show(context,"Asistencia Programada - Entrada(AttendanceReceiver)","Entrada - Asistencia programada correctamente a las ${entrada[0]}:${entrada[1]}")
                }
                else if (tipo == 200)
                {
                    val salida = horaSalida!!.split(":")
                    AlarmHelper.programarAlarma(context,salida[0].toInt(),salida[1].toInt(),200)
                    NotificationHelper.show(context,"Asistencia Programada - Salida(AttendanceReceiver)","Salida - Asistencia programada correctamente a las ${salida[0]}:${salida[1]}")
                }
            } catch (e: Exception)
            {
                e.printStackTrace()
            }
        }
    }
}