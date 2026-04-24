package com.example.inventarioElectronica.Views

import com.example.inventarioElectronica.Model.lugar

data class mesaVista(
    val mesa: lugar,
    val puedeLiberar: Boolean,
    val puedeEnviar: Boolean
)