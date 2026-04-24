package com.example.inventarioElectronica.Model

import jakarta.persistence.*
import java.sql.Date
import java.time.LocalDate

@Table(name="mantenimiento_mesa")
@Entity data class mantenimientoMesa(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var IDMantenimiento: Int = 0,

    @OneToOne @JoinColumn(name = "IDLugar")
    var IDLugar:lugar ?= null,

    @OneToOne @JoinColumn(name="numero_control")
    var usuario: usuario ?= null,

    var fechaEntrada: Date = Date.valueOf(LocalDate.now()),
    var fechaSalida: Date = Date.valueOf(LocalDate.now()),
    var fechaProximoMantenimiento: Date = Date.valueOf(LocalDate.now())
)