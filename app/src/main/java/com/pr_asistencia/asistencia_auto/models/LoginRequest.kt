package com.pr_asistencia.asistencia_auto.models

data class LoginRequest(
    val tenantName: String,
    val userNameOrEmailAddress: String,
    val password: String,
    val rememberClient: Boolean
)