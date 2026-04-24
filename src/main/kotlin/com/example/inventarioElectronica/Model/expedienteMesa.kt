package com.example.inventarioElectronica.Model

import jakarta.persistence.*
import java.sql.Date
import java.time.LocalDate

@Table(name="expediente_mesa")
@Entity data class expedienteMesa(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var IDExpediente:Int=0,

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="IDLugar")
    var IDLugar: lugar ?=null,

    @OneToOne @JoinColumn(name="numero_control")
    var numeroControl: usuario ?=null,

    var fechaEntrada: Date = Date.valueOf(LocalDate.now()),
    var fechaSalida: Date = Date.valueOf(LocalDate.now()),
    var fechaProximoMantenimiento:Date = Date.valueOf(LocalDate.now())
)