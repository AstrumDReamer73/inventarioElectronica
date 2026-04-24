package com.example.inventarioElectronica.Controller

import com.example.inventarioElectronica.DTO.avanceDTO
import com.example.inventarioElectronica.Service.asignacionService
import com.example.inventarioElectronica.Service.avanceCurricularService
import com.example.inventarioElectronica.Service.insumoPracticaService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import kotlin.collections.emptyList

@RequestMapping("/grupos/avanceCurricular/{claveGrupo}")
@Controller class avanceCurricularController(
    private val avanceCurricularService: avanceCurricularService,
    private var asignacionService: asignacionService,
    private var insumosService: insumoPracticaService,
) {
    @GetMapping fun verAvanceGrupo(@PathVariable("claveGrupo") claveGrupo: String, model: Model): String {
        val avances = avanceCurricularService.findByGrupo(claveGrupo)
        val vista = avances.map { ac ->
            val asignacion = asignacionService.findByPracticaAndClaveGrupo(
                ac.IDPractica!!.IDPracticas,
                ac.claveGrupo!!.claveGrupo
            )
            val insumos = asignacion?.let { insumosService.findByAsignacion(it.IDAsignacion) }?: emptyList()
            avanceDTO(ac.IDPractica!!,ac,asignacion,insumos)
        }
        model.addAttribute("practicas",vista)
        model.addAttribute("claveGrupo",claveGrupo)
        return "BancoPracticas/avanceCurricular"
    }
}