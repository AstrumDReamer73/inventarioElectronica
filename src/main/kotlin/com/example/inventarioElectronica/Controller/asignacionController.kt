package com.example.inventarioElectronica.Controller

import com.example.inventarioElectronica.DTO.asignacionDTO
import com.example.inventarioElectronica.DTO.practicaDTO
import com.example.inventarioElectronica.DTO.practicaWizard
import com.example.inventarioElectronica.Model.lugar
import com.example.inventarioElectronica.Service.asignacionService
import com.example.inventarioElectronica.Service.gruposService
import com.example.inventarioElectronica.Service.practicaService
import jakarta.validation.Valid
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import java.sql.Date

@RequestMapping("/practicas/asignacion/") @SessionAttributes("practicaWizard")
@Controller class asignacionController(
    private val gruposService: gruposService,
    private val practicasService: practicaService,
    private val asignacionService: asignacionService,
) {
    @ModelAttribute("practicaWizard")
    fun initWizard() = practicaWizard(practicaDTO(), asignacionDTO(),mutableListOf())

    @GetMapping("/lugaresDisponibles/{claveGrupo}") @ResponseBody
    fun verLugaresDisponibles(@PathVariable claveGrupo: String,
                              @RequestParam fecha:Date,
                              @RequestParam(required = false) IDLugar:Int?): List<lugar> {
        val grupo = gruposService.findByClaveGrupo(claveGrupo)
        val horaEntrada = grupo.horaEntrada
        val horaSalida = grupo.horaSalida
        val lugares = asignacionService.findLugaresDisponibles(fecha, horaEntrada, horaSalida, IDLugar)
        return lugares
    }

    @GetMapping("/asignar/{IDPractica}")
    fun mostrarFormularioAsignar(@ModelAttribute("practicaWizard") wizard: practicaWizard,
                                 @PathVariable IDPractica: Int,
                                 @RequestParam claveGrupo:String,
                                 model: Model): String {
        val grupo= gruposService.findByClaveGrupo(claveGrupo)
        val practica = practicasService.findByIDPractica(IDPractica)
        wizard.practica = practicaDTO(IDPractica = practica.IDPracticas,
            nombre = practica.nombre,
            materia = practica.materia!!.claveMateria,
            archivo = null)
        val asignacionExistente = asignacionService.findByPracticaAndClaveGrupo(IDPractica, claveGrupo)
        wizard.asignacion = asignacionExistente?.let {
            asignacionDTO(
                IDAsignacion = it.IDAsignacion,
                IDPractica = practica.IDPracticas,
                IDLugar = it.lugar!!.IDLugar,
                claveGrupo = claveGrupo,
                fecha = it.fecha,
                estado = it.estado,
            )
        } ?: asignacionDTO(IDPractica = practica.IDPracticas,claveGrupo = claveGrupo)
        println(wizard.asignacion)

        val lugares = asignacionService.findLugaresDisponibles(wizard.asignacion.fecha, grupo.horaEntrada, grupo.horaSalida, wizard.asignacion.IDLugar)
        println(lugares)
        val ruta = if(wizard.asignacion.IDAsignacion == 0) "/practicas/asignacion/asignar/$IDPractica?claveGrupo=$claveGrupo"
        else "/practicas/asignacion/editar/$IDPractica?claveGrupo=$claveGrupo"
        val titulo = if(wizard.asignacion.IDAsignacion == 0) "Registrar asignacion"
        else "Editar asignacion"

        model.addAttribute("asignacionDTO", wizard.asignacion)
        model.addAttribute("lugares", lugares)
        model.addAttribute("grupo", grupo)
        model.addAttribute("accion2", ruta)
        model.addAttribute("titulo", titulo)
        return "BancoPracticas/asignarPractica"
    }

    @PostMapping("/asignar/{IDPractica}")
    fun Asignar(@ModelAttribute("practicaWizard") wizard: practicaWizard,
                @PathVariable IDPractica: Int,
                @RequestParam claveGrupo: String,
                @Valid @ModelAttribute asignacionDTO: asignacionDTO,
                result: BindingResult,
                flash: RedirectAttributes): String {
        if (result.hasErrors()) { return "redirect:/practicas/asignacion/asignar/${IDPractica}?claveGrupo=${claveGrupo}" }
        wizard.asignacion = asignacionDTO
        flash.addFlashAttribute("success", "Asignación registrada correctamente")
        return "redirect:/practicas/asignarInsumos"
    }

    @PostMapping("/editar/{IDPractica}")
    fun editar(@ModelAttribute("practicaWizard") wizard: practicaWizard,
               @PathVariable IDPractica: Int,
               @RequestParam claveGrupo: String,
               @Valid @ModelAttribute asignacionDTO: asignacionDTO,
               result: BindingResult,
               flash: RedirectAttributes): String {
        if (result.hasErrors()) { return "redirect:/practicas/asignacion/asignar/${IDPractica}?claveGrupo=${claveGrupo}" }
        wizard.asignacion = asignacionDTO
        asignacionService.updateAsignacion(wizard.asignacion.IDAsignacion, wizard.asignacion)
        flash.addFlashAttribute("success", "Asignación editada correctamente")
        return "redirect:/grupos/avanceCurricular/$claveGrupo"
    }
}