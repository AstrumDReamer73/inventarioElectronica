package com.example.inventarioElectronica.DTO

import com.example.inventarioElectronica.Model.asignacionPractica
import com.example.inventarioElectronica.Model.avanceCurricular
import com.example.inventarioElectronica.Model.practica
import com.example.inventarioElectronica.Views.insumosView

data class avanceDTO(
    val practica: practica,
    val avance: avanceCurricular,
    val asignacion: asignacionPractica?,
    val insumos:List<insumosView>,
)