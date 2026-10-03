package com.pr_asistencia.asistencia_auto.models

import org.json.JSONObject

data class Profile(
    val nombreCompleto: String,
    val nombre: String,
    val apellido: String,
    val correo: String,
    val codigo: String,
    val telefono: String,
    val ciudad: String,
    val pais: String,
    val cumpleanhos: String,
    val foto: String,
    val roles: List<String>
) {
    val iniciales: String
        get() {
            val iniciales = listOf(nombre, apellido)
                .filter { it.isNotBlank() }
                .mapNotNull { it.trim().firstOrNull()?.uppercaseChar() }
            return if (iniciales.isEmpty()) "?" else iniciales.joinToString("")
        }

    val rolPrincipal: String
        get() = roles.firstOrNull() ?: "Colaborador"

    companion object {
        fun desdeJson(json: String): Profile? {
            return try {
                val raiz = JSONObject(json)
                val resultado = if (raiz.has("result")) raiz.getJSONObject("result") else raiz

                val nombre = texto(resultado, "name")
                val apellido = texto(resultado, "surname")

                val pais = resultado.optJSONObject("country")
                    ?.let { texto(it, "name") }
                    ?: texto(resultado, "country")

                val roles = resultado.optJSONArray("roleNames")
                    ?.let { array ->
                        (0 until array.length()).mapNotNull { array.opt(it)?.toString() }
                    }
                    ?: emptyList()

                Profile(
                    nombreCompleto = texto(resultado, "fullName")
                        .ifBlank { (nombre + " " + apellido).trim() },
                    nombre = nombre,
                    apellido = apellido,
                    correo = texto(resultado, "email"),
                    codigo = texto(resultado, "code"),
                    telefono = texto(resultado, "phoneNumber").ifBlank {
                        texto(resultado, "phone")
                    },
                    ciudad = texto(resultado, "city"),
                    pais = pais,
                    cumpleanhos = formatearCumpleanhos(texto(resultado, "birthday")),
                    foto = texto(resultado, "picture"),
                    roles = roles
                )
            } catch (e: Exception) {
                null
            }
        }

        private fun texto(obj: JSONObject, key: String): String {
            val valor = obj.opt(key) ?: return ""
            return when (valor) {
                is String -> valor.trim()
                is Int, is Long, is Double, is Float, is Boolean -> valor.toString()
                else -> ""
            }
        }

        private fun formatearCumpleanhos(valor: String): String {
            if (valor.length < 10) return valor
            return try {
                val partes = valor.substring(0, 10).split("-")
                if (partes.size == 3) "${partes[2]}/${partes[1]}/${partes[0]}" else valor
            } catch (e: Exception) {
                valor
            }
        }
    }
}
