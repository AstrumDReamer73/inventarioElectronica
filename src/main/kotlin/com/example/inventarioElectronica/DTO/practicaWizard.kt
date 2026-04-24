package com.example.inventarioElectronica.DTO

data class practicaWizard(
    var practica: practicaDTO,
    var asignacion: asignacionDTO,
    var insumos: MutableList<insumoDTO>,
)