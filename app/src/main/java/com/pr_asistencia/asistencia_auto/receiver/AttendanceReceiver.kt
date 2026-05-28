package com.pr_asistencia.asistencia_auto.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.pr_asistencia.asistencia_auto.manager.AttendanceManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AttendanceReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent)
    {
        Log.d(
            "ASISTENCIA",
            "Receiver ejecutado"
        )

        CoroutineScope(Dispatchers.IO).launch {
            try
            {
                AttendanceManager.marcarAsistencia()
            } catch (e: Exception)
            {
                e.printStackTrace()
            }
        }
    }
}