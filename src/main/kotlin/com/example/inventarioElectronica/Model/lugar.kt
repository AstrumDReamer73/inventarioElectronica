package com.example.inventarioElectronica.Model

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.*
import java.sql.Date

@Table(name = "lugar")
@Entity data class lugar(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("IDLugar")
    var IDLugar: Int = 0,

    var nombre: String = "",
    var estado: String = "",
    var fechaUltimoMantenimiento: Date ?= null,
    var fechaProximoMantenimiento: Date ?= null,
    var fechaSalidaMantenimiento: Date ?= null,
    var nombrePersonal: String ?= null
)