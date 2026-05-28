package com.pr_asistencia.asistencia_auto.helper

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.pr_asistencia.asistencia_auto.receiver.AttendanceReceiver
import java.util.Calendar

object AlarmHelper {

    @SuppressLint("ScheduleExactAlarm")
    fun programarAlarma(context: Context, hora: Int, minuto: Int, requestCode: Int)
    {

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AttendanceReceiver::class.java)

        intent.putExtra("tipo", requestCode)
        val pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        alarmManager.cancel(pendingIntent)

        val calendar = Calendar.getInstance()

        calendar.set(Calendar.HOUR_OF_DAY, hora)
        calendar.set(Calendar.MINUTE, minuto)
        calendar.set(Calendar.SECOND, 0)

        if (calendar.timeInMillis < System.currentTimeMillis())
        {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
            android.util.Log.d("Alarma(AlarmHelper)", "Programada para: ${calendar.time}")
            NotificationHelper.show(context,"Alarma(AlarmHelper)","Su alarma se ha programado para: ${calendar.time}")
        }
        else
        {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        }
    }
}