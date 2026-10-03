package com.pr_asistencia.asistencia_auto.manager

import com.pr_asistencia.asistencia_auto.network.RetrofitClient

object ProfileManager
{
    suspend fun obtenerPerfil(): String?
    {
        try
        {
            for (intento in 1..3)
            {
                val token = AuthManager.ensureToken() ?: return null

                val profileResponse = RetrofitClient.api.getMyProfile(
                    "Bearer $token"
                )

                if (profileResponse.isSuccessful)
                {
                    return profileResponse.body()?.string()
                }

                if (profileResponse.code() == 401 && intento < 3)
                {
                    if (!AuthManager.reLogin()) return null
                    continue
                }

                return "Error profile: ${profileResponse.code()} - ${profileResponse.errorBody()?.string()}"
            }

            return null
        }
        catch (e: Exception)
        {
            return e.message
        }
    }
}
