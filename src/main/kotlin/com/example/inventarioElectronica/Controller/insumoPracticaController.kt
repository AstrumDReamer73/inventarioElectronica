package com.example.inventarioElectronica.Controller

import com.example.inventarioElectronica.DTO.asignacionDTO
import com.example.inventarioElectronica.DTO.insumoDTO
import com.example.inventarioElectronica.DTO.practicaDTO
import com.example.inventarioElectronica.DTO.practicaWizard
import com.example.inventarioElectronica.Service.articulosService
import com.example.inventarioElectronica.Service.insumoPracticaService
import com.example.inventarioElectronica.Views.insumosView
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

@RequestMapping("/practicas/asignarInsumos") @SessionAttributes("practicaWizard")
@Controller class insumoPracticaController(
    private var insumoPracticaService: insumoPracticaService,
    private var articulosService: articulosService
) {
    @ModelAttribute("practicaWizard") fun initWizard(): practicaWizard = practicaWizard(practicaDTO(),asignacionDTO(),mutableListOf())

    @GetMapping fun mostrarFormularioAsignar(@ModelAttribute("practicaWizard") wizard: practicaWizard, model: Model): String{
        val insumosCompletos = wizard.insumos.map {
            val articulo= articulosService.findByNumeroSerie(it.numeroSerie.trim())
            insumosView(numeroSerie = articulo.numeroSerie,
                modelo = articulo.modelo,
                descripcion = articulo.descripcion,
                categoria = articulo.categoria,
                marca = articulo.marca,
                fechaUltimoMantenimiento = articulo.fechaUltimoMantenimiento!!.toLocalDate(),
                ubicacion = articulo.ubicacion,
                estado = it.estado
            )
        }
        model.addAttribute("insumos", insumoPracticaService.findAllArticulosElegibles(wizard.asignacion.fecha.toLocalDate(), wizard.insumos.map { it.numeroSerie }))
        model.addAttribute("insumosAsignados", insumosCompletos)
        model.addAttribute("titulo","Asignar Insumos")
        model.addAttribute("accion3", "/practicas/asignarInsumos")
        return "BancoPracticas/asignarInsumos"
    }

    @PostMapping fun asignarInsumos(@RequestParam(required = false) numeroSerie: String?, @ModelAttribute("practicaWizard") wizard: practicaWizard): String {
        numeroSerie?.let{
            if(wizard.insumos.none {i -> i.numeroSerie == it}){
                wizard.insumos.add(insumoDTO(numeroSerie=it, estado = "Disponble"))
            }
        }
        return "redirect:/practicas/asignarInsumos"
    }

    @PostMapping("/eliminar") fun eliminarInsumo(@RequestParam index:Int, @ModelAttribute("practicaWizard") wizard: practicaWizard): String {
        if(index in wizard.insumos.indices){ wizard.insumos.removeAt(index) }
        return "redirect:/practicas/asignarInsumos"
    }
}
