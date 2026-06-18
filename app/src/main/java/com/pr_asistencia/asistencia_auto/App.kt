@file:Suppress("DEPRECATION")

package com.pr_asistencia.asistencia_auto

import android.app.Application
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

class App : Application() {

    companion object {
        lateinit var instance: App
    }

    override fun onCreate() {
        super.onCreate()

        instance = this
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