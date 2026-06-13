package com.pr_asistencia.asistencia_auto.receiver

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.pr_asistencia.asistencia_auto.helper.AlarmHelper
import com.pr_asistencia.asistencia_auto.helper.NotificationHelper

class BootReceiver : BroadcastReceiver() {

    @SuppressLint("ServiceCast")
    override fun onReceive(context: Context, intent: Intent)
    {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED && intent.action != "android.intent.action.QUICKBOOT_POWERON")
        {
            return
        }

        try
        {
            val appContext = context.applicationContext
            val prefs = appContext.getSharedPreferences("config", Context.MODE_PRIVATE)

//            val activo = prefs.getBoolean("activo", true)
//            val automatico = prefs.getBoolean("automatico", true)
//            if (!activo || !automatico) {
//                Log.d("BootReceiver", "No se programan alarmas: activo=$activo, automatico=$automatico")
//                NotificationHelper.show(appContext, "Alarma(BootReceiver)", "No se programan alarmas porque la asistencia automática está desactivada")
//                return
//            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            {
                val alarmManager = appContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager

                if (!alarmManager.canScheduleExactAlarms()) {
                    Log.d("BootReceiver", "No se pueden programar alarmas exactas: permiso no concedido")
                    NotificationHelper.show(appContext, "BootReceiver", "No se pueden programar alarmas exactas. Falta permiso.")
                    return
                }
            }

            val entrada = prefs.getString("horaEntrada", "08:25") ?: "08:25"
            val salida = prefs.getString("horaSalida", "18:35") ?: "18:35"

            val entradaSplit = entrada.split(":")
            val salidaSplit = salida.split(":")

            if (entradaSplit.size != 2 || salidaSplit.size != 2) {
                Log.e("BootReceiver", "Formato de hora inválido: entrada: $entrada - salida: $salida")
                NotificationHelper.show(appContext, "Error BootReceiver", "Formato de hora inválido: entrada: $entrada - salida: $salida")
                return
            }

            val entradaHora = entradaSplit[0].toIntOrNull()
            val entradaMinuto = entradaSplit[1].toIntOrNull()
            val salidaHora = salidaSplit[0].toIntOrNull()
            val salidaMinuto = salidaSplit[1].toIntOrNull()

            if (entradaHora == null || entradaMinuto == null || salidaHora == null || salidaMinuto == null)
            {
                Log.e("BootReceiver", "Hora inválida: entrada: $entrada, salida: $salida")
                NotificationHelper.show(appContext,"Error BootReceiver","Hora inválida: entrada: $entrada - salida: $salida")
                return
            }

            AlarmHelper.programarAlarma(appContext, entradaHora, entradaMinuto, 100)
            AlarmHelper.programarAlarma(appContext, salidaHora, salidaMinuto, 200)

            Log.d("BootReceiver", "Alarmas programadas al reiniciar: entrada: $entrada - salida: $salida")
            NotificationHelper.show(appContext, "Alarma(BootReceiver)", "Alarmas programadas al reiniciar - Entrada: $entrada - Salida: $salida")
        }
        catch (e: Exception) {
            Log.e("BootReceiver", "Error al programar alarmas después del reinicio", e)
            NotificationHelper.show(context.applicationContext, "Error BootReceiver", e.message ?: "Error desconocido")
        }

        //val prefs = context.getSharedPreferences("config",Context.MODE_PRIVATE)
        //val entrada = prefs.getString("horaEntrada", "08:25") ?: "08:25"
        //val salida = prefs.getString("horaSalida", "18:35") ?: "18:35"
        //val entradaSplit = entrada.split(":")
        //val salidaSplit = salida.split(":")
        //AlarmHelper.programarAlarma(context, entradaSplit[0].toInt(), entradaSplit[1].toInt(), 100)
        //AlarmHelper.programarAlarma(context, salidaSplit[0].toInt(), salidaSplit[1].toInt(), 200)
        //Log.d("BootReceiver", "Alarma programada")
        //NotificationHelper.show(context,"Alarma(BootReceiver)","Alarma programada")
    }
}