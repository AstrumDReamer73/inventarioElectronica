package com.example.inventarioElectronica.DTO

import jakarta.validation.constraints.Future
import java.sql.Date
import java.time.LocalDate

data class asignacionDTO(
    var IDAsignacion: Int=0,
    val IDPractica: Int=0,
    var IDLugar:Int ?= null,
    var claveGrupo: String = "",
    var estado: String = "",

    @field:Future var fecha: Date = Date.valueOf(LocalDate.now()),
)