package com.example.inventarioElectronica.Views

import java.sql.Date
import java.sql.Time

data class asignacionView(
    var nombrePractica: String,
    var nombreLugar: String,
    var fecha: Date,
    var claveMateria: String,
    var claveGrupo: String,
    var nombreMaestro: String,
    var horaEntrada: Time,
    var horaSalida:Time,
    var estado: String
)