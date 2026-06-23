package com.example.inventarioElectronica.Views

import java.time.LocalDate

data class equiposMantenimientoView(
    var IDEquipoMantenimiento: Int,
    var numeroSerie: String,
    var modelo: String,
    var descripcion: String,
    var marca: String,

    var motivo: String,
    var nombre: String,
    var numeroControl: String,
    var fechaEntrada: LocalDate,
    var fechaSalida: LocalDate,
    var estado: String,
)