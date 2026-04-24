package com.example.inventarioElectronica.Views

import java.sql.Time
import java.time.LocalTime

data class gruposView(
    var claveMateria: String,
    var nombreMateria: String,
    var claveGrupo: String,
    var numeroControl: String,

    var nombre: String,
    var horaEntrada: LocalTime,
    var horaSalida: LocalTime,
    var dias: String,
) {
    fun ocurreEn(dia: String, hora: LocalTime): Boolean {
        return dias.contains(dia)
                && !hora.isBefore(horaEntrada)
                && hora.isBefore(horaSalida)
    }
}