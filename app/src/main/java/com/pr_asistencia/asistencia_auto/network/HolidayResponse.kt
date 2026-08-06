package com.pr_asistencia.asistencia_auto.network

import com.google.gson.annotations.SerializedName

data class HolidayResponse(
    val date: String,
    @SerializedName("localName")
    val localName: String,
    val global: Boolean
)
