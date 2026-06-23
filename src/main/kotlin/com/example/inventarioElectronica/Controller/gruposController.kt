package com.example.inventarioElectronica.Controller

import com.example.inventarioElectronica.DTO.grupoDTO
import com.example.inventarioElectronica.Model.usuario
import com.example.inventarioElectronica.Service.gruposService
import com.example.inventarioElectronica.Service.lugarService
import com.example.inventarioElectronica.Service.usuarioService
import com.example.inventarioElectronica.Views.mesaVista
import jakarta.validation.Valid
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import java.time.LocalTime

@RequestMapping("grupos")
@Controller class gruposController(
    private var usuarioService: usuarioService,
    private var gruposService: gruposService,
    private var lugarService: lugarService
) {
    fun normalizarDias(dias: String): String =
        dias.split(",").joinToString(", ") { it.trim().replaceFirstChar { it.uppercaseChar() } }

    @GetMapping fun mostrarSalones(model: Model): String {
        val mesas = lugarService.findAll().filter { it.IDLugar!=1 }
        val usuarioActual = SecurityContextHolder.getContext().authentication!!.principal as usuario
        val vistaMesas = mesas.map { mesa ->
            val puedeLiberar = mesa.estado == "En mantenimiento" &&
                    (usuarioActual.rol == "Jefe" ||
                            (usuarioActual.rol == "Personal" && usuarioActual.nombre == mesa.nombrePersonal))

            val puedeEnviar = mesa.estado == "Disponible" &&
                    (usuarioActual.rol == "Jefe" || usuarioActual.rol == "Personal")

            mesaVista(mesa = mesa,
                      puedeLiberar = puedeLiberar,
                      puedeEnviar = puedeEnviar)
        }
        model.addAttribute("mesas", vistaMesas)
        return "listas/listaSalones"
    }

    @GetMapping("/obtenerMateria/{claveMateria}") @ResponseBody
    fun obtenerMateria(@PathVariable claveMateria: String) = gruposService.findByClaveMateria(claveMateria)

    @GetMapping("/obtenerGrupo/{claveGrupo}") @ResponseBody
    fun obtenerGrupo(@PathVariable claveGrupo: String) = gruposService.findByClaveGrupo(claveGrupo)

    @GetMapping("/listaGrupos/{salon}")
    fun listarGruposPorSalon(@PathVariable salon: String, model: Model): String{
        val grupos = gruposService.findBySalon(salon)
        //grupos.forEach { gruposView -> normalizarDias(gruposView.getDias()) }
        //grupos.forEach {  = normalizarDias(it.getDias()) }

        model.addAttribute("salon", salon)
        model.addAttribute("grupos", grupos)
        return "listas/listaGrupos"
    }

    @GetMapping("/horario/{salon}")
    fun verHorario(@PathVariable salon: String, model: Model): String{
        val grupos = gruposService.findBySalon(salon)
        val diasSemana = listOf("lunes","martes","miercoles","jueves","viernes","sabado")
        val horas =(7..20).map{ LocalTime.of(it,0) }

        model.addAttribute("salon",salon)
        model.addAttribute("grupos",grupos)
        model.addAttribute("diasSemana",diasSemana)
        model.addAttribute("horas",horas)
        return "listas/horarioSalon"
    }

    @GetMapping("/registrar")
    fun mostrarFormularioRegistrar(model: Model): String{
        val auth = SecurityContextHolder.getContext().authentication
        val userName = auth!!.name
        val roles = auth.authorities.map {it.authority}

        val materias = if(roles.contains("Maestro")){
            val grupos = gruposService.findByMaestro(userName)
            grupos.map { it.claveMateria }.distinct()
        }else{ gruposService.findAllMaterias() }

        model.addAttribute("grupoDTO", grupoDTO())
        model.addAttribute("titulo","Registrar grupo")
        model.addAttribute("listaMaterias",materias)
        model.addAttribute("listaMaestros",usuarioService.findByRol(listOf("Maestro")))
        model.addAttribute("accion","/grupos/registrar")
        return "registrar/registrarGrupo"
    }

    @PostMapping("/registrar")
    fun guardarGrupo(@Valid @ModelAttribute grupo: grupoDTO,
                     result: BindingResult,
                     flash: RedirectAttributes,
                     model: Model): String{
        if(result.hasErrors()){
            val auth = SecurityContextHolder.getContext().authentication
            val userName = auth!!.name
            val roles = auth.authorities.map {it.authority}

            val materias = if(roles.contains("Maestro")){
                val grupos = gruposService.findByMaestro(userName)
                grupos.map { it.claveMateria }.distinct()
            }else{ gruposService.findAllMaterias() }

            model.addAttribute("grupoDTO", grupo)
            model.addAttribute("titulo","Registrar grupo")
            model.addAttribute("listaMaterias",materias)
            model.addAttribute("listaMaestros",usuarioService.findByRol(listOf("Maestro")))
            model.addAttribute("accion","/grupos/registrar")
            return "registrar/registrarGrupo"
        }
        gruposService.saveGrupo(grupo)
        flash.addFlashAttribute("success","Grupo registrado correctamente")
        return "redirect:/grupos/horario/${grupo.salon}"
    }

    @GetMapping("/editar/{claveGrupo}") fun mostrarFormularioEditar(@PathVariable claveGrupo: String, model: Model): String {
        val auth = SecurityContextHolder.getContext().authentication
        val userName = auth!!.name
        val roles = auth.authorities.map {it.authority}
        val grupo = gruposService.findByClaveGrupo(claveGrupo)

        val grupoDTO= grupoDTO(
            claveGrupo = grupo.claveGrupo,
            numeroControl = grupo.numeroControl!!.numeroControl,
            claveMateria = grupo.claveMateria!!.claveMateria,
            nombreMateria = grupo.claveMateria!!.nombreMateria,
            salon = grupo.salon,
            horaEntrada = grupo.horaEntrada.toLocalTime(),
            horaSalida = grupo.horaSalida.toLocalTime()
        )

        val materias = if(roles.contains("Maestro")){
            val grupos = gruposService.findByMaestro(userName)
            grupos.map { it.claveMateria }.distinct()
        }else{ gruposService.findAllMaterias() }

        model.addAttribute("grupoDTO", grupoDTO)
        model.addAttribute("titulo", "Editar grupo")
        model.addAttribute("listaMaterias",materias)
        model.addAttribute("listaMaestros",usuarioService.findByRol(listOf("Maestro")))
        model.addAttribute("accion","/grupos/editar/${grupo.claveGrupo}")
        return "registrar/registrarGrupo"
    }

    @PostMapping("/editar/{claveGrupo}")
    fun actualizarGrupo(@PathVariable claveGrupo: String,
                        @Valid @ModelAttribute grupo: grupoDTO,
                        result: BindingResult,
                        flash: RedirectAttributes,
                        model: Model): String{
        if(result.hasErrors()){
            val auth = SecurityContextHolder.getContext().authentication
            val userName = auth!!.name
            val roles = auth.authorities.map {it.authority}

            val materias = if(roles.contains("Maestro")){
                val grupos = gruposService.findByMaestro(userName)
                grupos.map { it.claveMateria }.distinct()
            }else{ gruposService.findAllMaterias() }

            model.addAttribute("grupoDTO", grupo)
            model.addAttribute("titulo","Editar grupo")
            model.addAttribute("listaMaterias",materias)
            model.addAttribute("listaMaestros",usuarioService.findByRol(listOf("Maestro")))
            model.addAttribute("accion","/grupos/editar/${claveGrupo}")
            return "registrar/registrarGrupo"
        }
        gruposService.updateGrupo(grupo)
        flash.addFlashAttribute("success","Grupo actualizado con exito")
        return "redirect:/grupos/horario/${grupo.salon}"
    }

    @GetMapping("/eliminar/{claveGrupo}")
    fun eliminarGrupo(@PathVariable claveGrupo: String, flash: RedirectAttributes): String{
        val grupo = gruposService.findByClaveGrupo(claveGrupo)
        gruposService.deleteByClaveGrupo(claveGrupo)
        flash.addFlashAttribute("success", "Grupo eliminado")
        return "redirect:/grupos/horario/${grupo.salon}"
    }
}