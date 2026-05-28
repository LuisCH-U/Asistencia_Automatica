package com.pr_asistencia.asistencia_auto.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.pr_asistencia.asistencia_auto.helper.AlarmHelper
import com.pr_asistencia.asistencia_auto.helper.NotificationHelper

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent)
    {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED)
        {
            return
        }

        val prefs = context.getSharedPreferences("config",Context.MODE_PRIVATE)
        val entrada = prefs.getString("horaEntrada", "08:25") ?: "08:25"
        val salida = prefs.getString("horaSalida", "18:35") ?: "18:35"

        val entradaSplit = entrada.split(":")
        val salidaSplit = salida.split(":")

        AlarmHelper.programarAlarma(context, entradaSplit[0].toInt(), entradaSplit[1].toInt(), 100)
        AlarmHelper.programarAlarma(context, salidaSplit[0].toInt(), salidaSplit[1].toInt(), 200)

        Log.d("BootReceiver", "Alarma programada")
        NotificationHelper.show(context,"Alarma(BootReceiver)","Alarma programada")
    }
}