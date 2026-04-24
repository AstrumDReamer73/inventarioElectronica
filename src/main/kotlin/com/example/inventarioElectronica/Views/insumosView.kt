package com.example.inventarioElectronica.Views

import java.sql.Date
import java.time.LocalDate

data class insumosView(
    val modelo: String,
    val descripcion: String,
    val categoria: String,
    val marca: String,

    val numeroSerie: String,
    val estado: String,
    val ubicacion: String,
    val fechaUltimoMantenimiento: LocalDate?
)