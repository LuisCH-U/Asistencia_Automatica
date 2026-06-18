@file:Suppress("DEPRECATION")

package com.pr_asistencia.asistencia_auto.helper

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import com.pr_asistencia.asistencia_auto.helper.dayHelper.DayActive
import com.pr_asistencia.asistencia_auto.receiver.AttendanceReceiver
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

object AlarmHelper {

    @SuppressLint("ScheduleExactAlarm")
    fun programarAlarma(context: Context, hora: Int, minuto: Int, requestCode: Int)
    {

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AttendanceReceiver::class.java)

        intent.putExtra("tipo", requestCode)

        val openOrExit = if (requestCode == 100) "Entrada" else "Salida"

        val OldpendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        alarmManager.cancel(OldpendingIntent)

        val pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val calendar = Calendar.getInstance(TimeZone.getTimeZone("America/Lima"))

        calendar.set(Calendar.HOUR_OF_DAY, hora)
        calendar.set(Calendar.MINUTE, minuto)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        if (calendar.timeInMillis < System.currentTimeMillis())
        {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
            val prefs = context.getSharedPreferences("config", Context.MODE_PRIVATE)
            siguienteDiaLaborable(calendar, prefs)
        }

        val sdf = SimpleDateFormat("EEEE dd/MM/yyyy HH:mm:ss", Locale("es", "PE"))

        val fecha = sdf.format(calendar.time).replaceFirstChar {it.uppercase()}

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
            android.util.Log.d("Alarma.v1", "Programado para: ${calendar.time} - Tipo: $requestCode")
            NotificationHelper.show(context,"Asistencia automática","Tu asistencia fue programado para: $fecha - $openOrExit")
        }
        else
        {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
            android.util.Log.d("Alarma.v2", "Programado para: ${calendar.time} - Tipo: $requestCode")
            NotificationHelper.show(context,"Asistencia automática","Tu asistencia fue programado para: $fecha - $openOrExit")
        }
    }

    private fun siguienteDiaLaborable( calendar: Calendar, prefs: SharedPreferences)
    {
        while (true)
        {
            val day = calendar.get(Calendar.DAY_OF_WEEK)
            /*
            val activo = when(day) {
                Calendar.MONDAY -> prefs.getBoolean("lunes", true)
                Calendar.TUESDAY -> prefs.getBoolean("martes", true)
                Calendar.WEDNESDAY -> prefs.getBoolean("miercoles", true)
                Calendar.THURSDAY -> prefs.getBoolean("jueves", true)
                Calendar.FRIDAY -> prefs.getBoolean("viernes", true)
                Calendar.SATURDAY -> prefs.getBoolean("sabado", false)
                Calendar.SUNDAY -> prefs.getBoolean("domingo", false)
                else -> false
            }
            */

            val activeday = DayActive(day, prefs)
            if (activeday)
            {
                break
            }

            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
    }
}