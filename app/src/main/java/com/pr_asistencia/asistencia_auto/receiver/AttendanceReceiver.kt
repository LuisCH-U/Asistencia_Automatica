package com.pr_asistencia.asistencia_auto.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.pr_asistencia.asistencia_auto.helper.AlarmHelper
import com.pr_asistencia.asistencia_auto.helper.HolidayHelper
import com.pr_asistencia.asistencia_auto.helper.NotificationHelper
import com.pr_asistencia.asistencia_auto.helper.dayHelper.DayActive
import com.pr_asistencia.asistencia_auto.manager.AttendanceManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class AttendanceReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent)
    {
        val tipo = intent.getIntExtra("tipo", 0)
        val now = Date()
        val ahora = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale("es", "PE")).format(Date())
        val pendingResult = goAsync()

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
                val fechaHoy = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)

                val diaActivo = DayActive(dayZone, fechaHoy, prefs, context)

                if (!diaActivo) {
                    Log.d("Dia activo", "No se marcará asistencia.")
                    val feriado = HolidayHelper.holidayName(context, fechaHoy)
                    if (feriado != null) {
                        NotificationHelper.show(context, "Asistencia automática", "Feriado: $feriado. No se marca asistencia.")
                    } else {
                        NotificationHelper.show(context, "Asistencia automática", "Asistencia no programado para hoy.")
                    }
                    return@launch
                }

                if (!activo || !automatico)
                {
                    Log.d("Inactivo","Asistencia marcada, solo pruebas tipo: $tipo - horaReal: $ahora")
                    NotificationHelper.show(context,"Asistencia automática","No se marco tu asistencia.")
                    return@launch
                }

                val yaMarco = AttendanceManager.yaMarcoHoy(tipo)

                if (yaMarco == true) {
                    Log.d("Asistencia - Ya marcada", "Ya existe una asistencia registrada hoy - Tipo: $tipo, Hora: $ahora")
                    NotificationHelper.show(context, "Asistencia automática", "Ya existe una asistencia registrada hoy. No se marcó de nuevo.")
                } else {
                    val ok = AttendanceManager.marcarAsistencia()

                    if (ok) {
                        Log.d("Asistencia - OK", "Asistencia marcada correctamente - Tipo:$tipo, Hora: $ahora")
                        NotificationHelper.show(context, "Asistencia automática", "Tu asistencia se registró correctamente.")
                    } else {
                        Log.d("Asistencia - Error", "Error al marcar asistencia - Tipo: $tipo, Hora: $ahora")
                        NotificationHelper.show(context, "Asistencia automática", "No fue posible registrar la asistencia. Se intentará nuevamente.")
                    }
                }

                if (tipo == 100) {
                    val entrada = horaEntrada!!.split(":")
                    AlarmHelper.programarAlarma(context,entrada[0].toInt(),entrada[1].toInt(),100)
                    //NotificationHelper.show(context,"Entrada - AttendanceReceiver","Asistencia programada correctamente: ${entrada[0]}:${entrada[1]}")
                }
                else if (tipo == 200) {
                    val salida = horaSalida!!.split(":")
                    AlarmHelper.programarAlarma(context,salida[0].toInt(),salida[1].toInt(),200)
                    //NotificationHelper.show(context,"Salida - AttendanceReceiver","Asistencia programada correctamente: ${salida[0]}:${salida[1]}")
                }

            } catch (e: Exception) {
                Log.e("Asistencia - Ex", "Error en AttendanceReceiver", e)
                NotificationHelper.show(context, "Asistencia automática", e.message ?: "Error desconocido")
                e.printStackTrace()
            }
            finally {
                pendingResult.finish()
            }
        }
    }
}