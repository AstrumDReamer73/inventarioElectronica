package com.example.inventarioElectronica.Model

import jakarta.persistence.*
import java.sql.Date
import java.time.LocalDate

@Table(name = "avance_curricular")
@Entity data class avanceCurricular(
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id var IDAvance:Int = 0,

    @OneToOne(fetch = FetchType.LAZY) @JoinColumn(name = "IDPractica")
    var IDPractica: practica ?= null,

    @OneToOne(fetch = FetchType.LAZY) @JoinColumn(name = "clave_grupo")
    var claveGrupo: grupo ?= null,

    var fecha: Date = Date.valueOf(LocalDate.now()),
    var estado : String =""
)