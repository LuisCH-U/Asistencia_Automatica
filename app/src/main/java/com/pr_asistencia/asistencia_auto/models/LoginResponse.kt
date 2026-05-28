package com.pr_asistencia.asistencia_auto.models

data class LoginResponse(
    val result: ResultData
)

data class ResultData(
    val accessToken: String
)