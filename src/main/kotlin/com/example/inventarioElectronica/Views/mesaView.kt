package com.example.inventarioElectronica.Views

import java.sql.Date
import java.time.LocalDate

data class mesaView(
    var IDLugar:Int,
    var nombre: String,
    var estado: String,
    var fechaUltimoMantenimiento: Date,
    var fechaProximoMantenimiento:Date,
    var fechaSalida:Date
)