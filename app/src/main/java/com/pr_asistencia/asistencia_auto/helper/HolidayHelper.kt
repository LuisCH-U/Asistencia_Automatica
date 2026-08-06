package com.pr_asistencia.asistencia_auto.helper

import android.content.Context
import com.pr_asistencia.asistencia_auto.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

object HolidayHelper {

    private const val PREFS_NAME = "holidays_cache"
    private const val KEY_YEAR = "cached_year"
    private const val KEY_DATES = "holiday_dates"
    private const val KEY_NAMES = "holiday_names"
    private const val COUNTRY = "PE"

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun isHoliday(context: Context, date: String): Boolean {
        val configPrefs = context.getSharedPreferences("config", Context.MODE_PRIVATE)
        if (!configPrefs.getBoolean("feriadosActivos", true)) return false

        val dates = prefs(context).getStringSet(KEY_DATES, emptySet()) ?: emptySet()
        return date in dates
    }

    fun holidayName(context: Context, date: String): String? {
        if (!isHoliday(context, date)) return null
        val namesJson = prefs(context).getString(KEY_NAMES, "{}") ?: "{}"
        return try {
            JSONObject(namesJson).optString(date, null)
        } catch (e: Exception) {
            null
        }
    }

    fun cachedYear(context: Context): Int =
        prefs(context).getInt(KEY_YEAR, -1)

    fun refreshHolidays(context: Context, year: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.holidayApi.getHolidays(year, COUNTRY)
                if (!response.isSuccessful) return@launch

                val body = response.body() ?: return@launch
                val nacionales = body.filter { it.global }

                val dates = nacionales.map { it.date }.toSet()
                val namesMap = nacionales.associate { it.date to it.localName }

                prefs(context).edit()
                    .putInt(KEY_YEAR, year)
                    .putStringSet(KEY_DATES, dates)
                    .putString(KEY_NAMES, JSONObject(namesMap).toString())
                    .apply()
            } catch (e: Exception) {
                // cache anterior se mantiene
            }
        }
    }
}
