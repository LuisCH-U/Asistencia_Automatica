@file:Suppress("DEPRECATION")

package com.pr_asistencia.asistencia_auto

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.pr_asistencia.asistencia_auto.helper.HolidayHelper
import java.util.Calendar

class App : Application() {

    companion object {
        lateinit var instance: App
    }

    override fun onCreate() {
        super.onCreate()

        instance = this

        aplicarTema()
        refreshHolidaysIfNeeded()
    }

    private fun aplicarTema() {
        val prefs = getSharedPreferences("config", MODE_PRIVATE)

        val modoTema = when {
            prefs.contains("modoTema") -> prefs.getInt("modoTema", 0)
            prefs.contains("darkMode") -> if (prefs.getBoolean("darkMode", false)) 2 else 1
            else -> 0
        }

        AppCompatDelegate.setDefaultNightMode(
            when (modoTema) {
                1 -> AppCompatDelegate.MODE_NIGHT_NO
                2 -> AppCompatDelegate.MODE_NIGHT_YES
                else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            }
        )
    }

    private fun refreshHolidaysIfNeeded() {
        val year = Calendar.getInstance().get(Calendar.YEAR)
        if (HolidayHelper.cachedYear(this) != year) {
            HolidayHelper.refreshHolidays(this, year)
        }
    }

    fun securePrefs() =
        EncryptedSharedPreferences.create(
            "secure_data",
            MasterKeys.getOrCreate(
                MasterKeys.AES256_GCM_SPEC
            ),
            this,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
}