package com.example.inventarioElectronica.Controller

import com.example.inventarioElectronica.DTO.articuloDTO
import com.example.inventarioElectronica.DTO.asignacionDTO
import com.example.inventarioElectronica.DTO.practicaDTO
import com.example.inventarioElectronica.DTO.practicaWizard
import com.example.inventarioElectronica.Service.articulosService
import com.example.inventarioElectronica.Service.asignacionService
import com.example.inventarioElectronica.Service.gruposService
import com.example.inventarioElectronica.Service.insumoPracticaService
import com.example.inventarioElectronica.Service.practicaService
import jakarta.validation.Valid
import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.SessionAttributes
import org.springframework.web.bind.support.SessionStatus
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import java.io.File

@RequestMapping("/practicas") @SessionAttributes("practicaWizard")
@Controller class practicasController(
    private var practicasService: practicaService,
    private var asignacionService: asignacionService,
    private var insumosService: insumoPracticaService,
    private var gruposService: gruposService,
    private var articulosService: articulosService,
) {
    @ModelAttribute("practicaWizard")
    fun initWizard(): practicaWizard = practicaWizard(practicaDTO(),asignacionDTO(),mutableListOf())

    @GetMapping fun listarPracticas(model: Model): String {
        model.addAttribute("practicas", practicasService.findAll())
        return "BancoPracticas/listaPracticas"
    }

    @GetMapping("/{IDLugar}")
    fun listarPracticasPorMesa(@PathVariable idlugar:Int, model: Model): String{
        model.addAttribute("asignaciones", asignacionService.findByLugar(idlugar))
        return "BancoPracticas/listaPracticasPorMesa"
    }

    @GetMapping("/pdf/{IDPractica}")
    fun descargarPDF(@PathVariable IDPractica:Int): ResponseEntity<Resource>{
        val practica = practicasService.findByIDPractica(IDPractica)
        val file = File(practica.archivo)
        val resource = UrlResource(file.toURI())
        if(!resource.exists() ||!resource.isReadable){ throw RuntimeException("Archivo no encontrado") }
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"${file.name}\"")
            .contentType(MediaType.APPLICATION_PDF)
            .body(resource)
    }

    @GetMapping("/registrar")
    fun mostrarFormularioRegistrar(@ModelAttribute("practicaWizard") wizard: practicaWizard, model: Model): String {
        val auth = SecurityContextHolder.getContext().authentication
        val userName = auth!!.name
        val roles = auth.authorities.map {it.authority}
        val materias = if(roles.contains("Maestro")){
            val grupos = gruposService.findByMaestro(userName)
            grupos.map { it.claveMateria }.distinct()
        }else{ gruposService.findAllMaterias() }
        model.addAttribute("practicaDTO",wizard.practica)
        model.addAttribute("materias",materias)
        model.addAttribute("titulo","Registrar practica")
        model.addAttribute("accion1","/practicas/registrar")
        return "BancoPracticas/registrarPractica"
    }

    @PostMapping("/registrar")
    fun guardarPractica(@ModelAttribute("practicaWizard") wizard: practicaWizard,
                        @Valid @ModelAttribute practica: practicaDTO,
                        result: BindingResult,
                        flash: RedirectAttributes,
                        model: Model): String {
        if(practica.archivo == null || practica.archivo!!.isEmpty)
        { result.rejectValue("archivo","error.archivo","el archivo esta vacio") }
        if (result.hasErrors()) {
            val auth = SecurityContextHolder.getContext().authentication
            val userName = auth!!.name
            val roles = auth.authorities.map {it.authority}
            val materias = if(roles.contains("Maestro")){
                val grupos = gruposService.findByMaestro(userName)
                grupos.map { it.claveMateria }.distinct()
            }else{ gruposService.findAllMaterias() }
            model.addAttribute("practicaDTO",wizard.practica)
            model.addAttribute("materias",materias)
            model.addAttribute("titulo","Registrar practica")
            model.addAttribute("accion1","/practicas/registrar")
            return "BancoPracticas/registrarPractica"
        }
        wizard.practica = practica
        practicasService.savePractica(practica)
        flash.addFlashAttribute("success", "la practica ha sido registrada con exito")
        return "redirect:/practicas"
    }

    @GetMapping("/editar/{IDPractica}")
    fun mostrarFormularioEditar(@PathVariable IDPractica:Int, @ModelAttribute("practicaWizard") wizard: practicaWizard, model: Model): String{
        val practica = practicasService.findByIDPractica(IDPractica)
        wizard.practica = practicaDTO(
            IDPractica = IDPractica,
            nombre = practica.nombre,
            materia = practica.materia!!.claveMateria,
            archivo = null
        )

        val auth = SecurityContextHolder.getContext().authentication
        val userName = auth!!.name
        val roles = auth.authorities.map {it.authority}
        val materias = if(roles.contains("Maestro")){
            val grupos = gruposService.findByMaestro(userName)
            grupos.map { it.claveMateria }.distinct()
        }else{ gruposService.findAllMaterias() }

        model.addAttribute("practicaDTO", wizard.practica)
        model.addAttribute("materias", materias)
        model.addAttribute("titulo","Editar practica")
        model.addAttribute("accion1", "/practicas/registrar")
        model.addAttribute("archivoActual", practica.archivo)
        return "BancoPracticas/registrarPractica"
    }

    @PostMapping("/editar/{IDPractica}")
    fun editarPractica(@PathVariable IDPractica: Int,
                       @ModelAttribute("practicaWizard") wizard: practicaWizard,
                       @ModelAttribute @Valid practica: practicaDTO,
                       result: BindingResult,
                       flash: RedirectAttributes,
                       model: Model): String {
        if(result.hasErrors()){
            val auth = SecurityContextHolder.getContext().authentication
            val userName = auth!!.name
            val roles = auth.authorities.map {it.authority}
            val materias = if(roles.contains("Maestro")){
                val grupos = gruposService.findByMaestro(userName)
                grupos.map { it.claveMateria }.distinct()
            }else{ gruposService.findAllMaterias() }

            model.addAttribute("practicaDTO", wizard.practica)
            model.addAttribute("materias", materias)
            model.addAttribute("titulo","Editar practica")
            model.addAttribute("accion1", "/practicas/registrar")
            model.addAttribute("archivoActual", practica.archivo)
            return "BancoPracticas/registrarPractica"
        }

        practicasService.updatePractica(IDPractica, practica)
        flash.addFlashAttribute("success", "Práctica actualizada correctamente")
        return "redirect:/practicas"
    }

    @GetMapping("/eliminar/{idPractica}")
    fun eliminarPractica(@PathVariable idPractica: Int, flash: RedirectAttributes): String{
        practicasService.deletePractica(idPractica)
        flash.addFlashAttribute("success","practica eliminada con exito")
        return "redirect:/practicas"
    }

    @GetMapping("/confirmar")
    fun pantallaReview(@ModelAttribute("practicaWizard") wizard: practicaWizard, model: Model): String {
        val grupo =gruposService.findByClaveGrupo(wizard.asignacion.claveGrupo)

        val insumos = wizard.insumos.map {
            val numeroSerie= it.numeroSerie.trim()
            val articulo = articulosService.findByNumeroSerie(numeroSerie)
            articuloDTO(modelo = articulo.modelo,
                descripcion = articulo.descripcion,
                categoria = articulo.categoria,
                marca = articulo.marca,
                numeroSerie = articulo.numeroSerie,
                estado = articulo.estado,
                plazoGarantia = articulo.plazoGarantia,
                fechaIngreso = articulo.fechaIngreso,
                fechaUltimoMantenimiento = articulo.fechaUltimoMantenimiento,
                fechaProximoMantenimiento = articulo.fechaProximoMantenimiento,
                ubicacion = articulo.ubicacion)
        }

        model.addAttribute("practica", wizard.practica)
        model.addAttribute("asignacion", wizard.asignacion)
        model.addAttribute("grupo", grupo)
        model.addAttribute("insumos", insumos)
        model.addAttribute("titulo", "confirmar practica")
        model.addAttribute("accion5", "/practicas/confirmar")
        return "/BancoPracticas/practicaReview"
    }

    @PostMapping("/confirmar") fun confirmar(@ModelAttribute("practicaWizard") wizard: practicaWizard,
                                             flash: RedirectAttributes,
                                             session: SessionStatus): String{
        val asignacion = asignacionService.saveAsignacion(wizard.asignacion)
        insumosService.saveInsumos(asignacion.IDAsignacion, wizard.insumos)
        flash.addFlashAttribute("success","practica confirmada con exito")
        session.setComplete()
        return "redirect:/grupos/avanceCurricular/${asignacion.claveGrupo!!.claveGrupo}"
    }
}