package com.example.inventarioElectronica.Views

import com.example.inventarioElectronica.DTO.asignacionDTO
import com.example.inventarioElectronica.DTO.practicaDTO

data class practicaWizardView(
    var practica: practicaDTO,
    var asignacion: asignacionDTO,
    var insumos: MutableList<insumosView>
)