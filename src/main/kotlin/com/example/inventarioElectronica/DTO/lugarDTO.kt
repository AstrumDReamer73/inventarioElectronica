package com.example.inventarioElectronica.DTO

import java.sql.Date
import java.time.LocalDate

data class lugarDTO(
    var idlugar: Int = 0,
    var nombre: String = "",
    var estado: String = "",
    var idMantenimiento: Int = 0,
    var numeroControl: String = "",
    var fechaEntrada: Date = Date.valueOf(LocalDate.now()),
    var fechaSalida: Date = Date.valueOf(LocalDate.now()),
    var fechaUltimoMantenimiento: Date? = Date.valueOf(LocalDate.now()),
    var fechaProximoMantenimiento: Date =Date.valueOf(LocalDate.now())
)