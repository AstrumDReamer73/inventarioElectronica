package com.example.inventarioElectronica.Model

import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.sql.Time
import java.time.LocalTime

@Table(name = "grupos")
@Entity data class grupo(
    @Id var claveGrupo: String = "",

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "numero_control")
    var numeroControl: usuario ?= null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clave_materia")
    var claveMateria: materia ?= null,

    @JdbcTypeCode(SqlTypes.TIME) @Column(name = "hora_entrada")
    var horaEntrada: Time = Time.valueOf(LocalTime.now().withNano(0)),

    @JdbcTypeCode(SqlTypes.TIME) @Column(name = "hora_salida")
    var horaSalida: Time = Time.valueOf(LocalTime.now().plusHours(1).withNano(0)),

    var diasLaboratorio: String = "",
    var salon: String = "",
)