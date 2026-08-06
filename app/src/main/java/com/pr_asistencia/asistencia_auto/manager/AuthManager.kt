package com.pr_asistencia.asistencia_auto.manager

import com.pr_asistencia.asistencia_auto.App
import com.pr_asistencia.asistencia_auto.models.LoginRequest
import com.pr_asistencia.asistencia_auto.network.RetrofitClient

object AuthManager {

    private fun prefs() = App.instance.securePrefs()

    fun getToken(): String? =
        prefs().getString("token", null)?.takeIf { it.isNotBlank() }

    suspend fun ensureToken(): String? {
        getToken()?.let { return it }
        return if (reLogin()) getToken() else null
    }

    suspend fun reLogin(): Boolean {
        return try {
            val p = prefs()
            val tenant = p.getString("tenant", "") ?: ""
            val user = p.getString("user", "") ?: ""
            val password = p.getString("password", "") ?: ""
            if (user.isBlank() || password.isBlank()) return false

            val resp = RetrofitClient.api.login(
                LoginRequest(
                    tenantName = tenant,
                    userNameOrEmailAddress = user,
                    password = password,
                    rememberClient = false
                )
            )
            if (!resp.isSuccessful) return false

            val token = resp.body()?.result?.accessToken
            if (token.isNullOrBlank()) return false

            p.edit().putString("token", token).apply()
            true
        } catch (e: Exception) {
            false
        }
    }
}
