//package com.pr_asistencia.asistencia_auto.worker


import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.pr_asistencia.asistencia_auto.manager.AttendanceManager
import java.text.SimpleDateFormat
import java.util.*
import com.pr_asistencia.asistencia_auto.helper.NotificationHelper

//class AttendanceWorker(context: Context, workerParams: WorkerParameters) : CoroutineWorker(context, workerParams)
//{

    //override suspend fun doWork(): Result
    // {
    //    try {
    //        NotificationHelper.show(applicationContext,"Asistencia","Worker ejecutado")
    //        val prefs = applicationContext.getSharedPreferences("config", Context.MODE_PRIVATE)
    //        val activo = prefs.getBoolean("activo", false)
    //        val automatico = prefs.getBoolean("automatico", false)
    //        if (!activo || !automatico)
    //        {
    //            return Result.success()
    //        }
    //        val calendar = Calendar.getInstance(TimeZone.getTimeZone("America/Lima"))
    //        val day = calendar.get(Calendar.DAY_OF_WEEK)
    //        val diaActivo = when(day)
    //        {
    //            Calendar.MONDAY -> prefs.getBoolean("lunes",true)
    //            Calendar.TUESDAY -> prefs.getBoolean("martes", true)
    //            Calendar.WEDNESDAY -> prefs.getBoolean("miercoles",true)
    //            Calendar.THURSDAY -> prefs.getBoolean( "jueves",true)
    //            Calendar.FRIDAY -> prefs.getBoolean("viernes",true)
    //            else -> false
    //        }
    //        if (!diaActivo)
    //        {
    //            return Result.success()
    //        }
    //        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    //        sdf.timeZone = TimeZone.getTimeZone("America/Lima")
    //        val horaActual = sdf.format(Date())
    //        val horaEntrada = prefs.getString("horaEntrada", "08:00")
    //        val horaSalida = prefs.getString("horaSalida", "18:00")
    //        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    //        if (horaActual != horaEntrada && horaActual != horaSalida) {
    //            return Result.success()
    //        }
    //        val marcaKey = if (horaActual == horaEntrada) "ultimaEntrada" else "ultimaSalida"
    //        val ultimaMarca = prefs.getString(marcaKey, "")
    //        val currentMark = today + "_" + horaActual
    //        NotificationHelper.show(applicationContext,"Asistencia","Intentando marcar asistencia")
    //        if (ultimaMarca != currentMark)
    //        {
    //            val ok = AttendanceManager.marcarAsistencia()
    //            if (ok) {
    //                NotificationHelper.show(applicationContext,"Asistencia","Asistencia marcada correctamente")
    //            }
    //            else {
    //                NotificationHelper.show(applicationContext,"Asistencia","Error al marcar asistencia")
    //            }
    //            prefs.edit().putString(marcaKey, currentMark).commit()
    //        }
    //        return Result.success()
    //    }
    //    catch (e: Exception)
    //    {
    //        Log.e("WORKER",e.message.toString())
    //        return Result.retry()
    //    }
    //}
//}