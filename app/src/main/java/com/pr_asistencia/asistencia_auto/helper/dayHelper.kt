package com.pr_asistencia.asistencia_auto.helper

import android.content.SharedPreferences
import java.util.Calendar

object dayHelper {

    fun DayActive(day: Int, prefs: SharedPreferences) : Boolean
    {
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
        return activo
    }
}