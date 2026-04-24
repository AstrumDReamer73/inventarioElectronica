package com.example.inventarioElectronica.DTO

import org.hibernate.validator.constraints.Length
import org.springframework.web.multipart.MultipartFile

data class practicaDTO(
    var IDPractica:Int=0,
    var archivo: MultipartFile?=null,
    @field:Length(min=0, max=50) var nombre: String="",
    var materia: String="",
)