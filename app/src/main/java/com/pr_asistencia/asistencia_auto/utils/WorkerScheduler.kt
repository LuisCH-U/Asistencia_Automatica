package com.pr_asistencia.asistencia_auto.utils

import android.content.Context
import androidx.work.*
import java.util.Calendar
import java.util.concurrent.TimeUnit

//object WorkerScheduler {

    //fun scheduleWorker(context: Context, hour: Int, minute: Int)
    //{
    //    val now = Calendar.getInstance()
    //    val target = Calendar.getInstance()
    //    target.set(Calendar.HOUR_OF_DAY, hour)
    //    target.set(Calendar.MINUTE, minute)
    //    target.set(Calendar.SECOND, 0)
    //    if (target.before(now))
    //    {
    //        target.add(Calendar.DAY_OF_MONTH, 1)
    //    }
    //    val delay = target.timeInMillis - now.timeInMillis
    //    val workRequest = OneTimeWorkRequestBuilder<AttendanceWorker>().setInitialDelay(delay, TimeUnit.MILLISECONDS).build()
    //    WorkManager.getInstance(context).enqueueUniqueWork("attendance_$hour$minute", ExistingWorkPolicy.REPLACE,workRequest)
    //}

    //fun scheduleNextExecution(context: Context)
    //{
    //    val prefs =context.getSharedPreferences("config",Context.MODE_PRIVATE)
    //    val entrada = prefs.getString("horaEntrada", "08:27") ?: "08:28"
    //    val salida = prefs.getString("horaSalida", "18:36") ?: "18:33"
    //    val entradaSplit = entrada.split(":")
    //    val salidaSplit = salida.split(":")
    //    scheduleWorker(context, entradaSplit[0].toInt(), entradaSplit[1].toInt())
    //    scheduleWorker(context, salidaSplit[0].toInt(), salidaSplit[1].toInt())
    //}
//}