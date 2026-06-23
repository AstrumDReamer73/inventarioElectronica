package com.example.inventarioElectronica.Views

import java.time.LocalDate

data class articulosView(
    var modelo: String,
    var descripcion: String,
    var categoria: String,
    var marca: String,

    var numeroSerie: String,
    var estado: String,
    var plazoGarantia: String,
    var ubicacion: String,

    var fechaIngreso: LocalDate,
    var fechaUltimoMantenimiento : LocalDate,
    var fechaProximoMantenimiento: LocalDate
)