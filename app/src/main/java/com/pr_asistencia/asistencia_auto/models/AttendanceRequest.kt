package com.pr_asistencia.asistencia_auto.models

data class AttendanceRequest(
    val attendance: Boolean,
    val comments: String?,
    val costCenterId: Int?,
    val issued: String,
    val latitude: Double?,
    val longitude: Double?
)