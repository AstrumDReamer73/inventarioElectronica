package com.example.inventarioElectronica.Controller

import com.example.inventarioElectronica.DTO.lugarDTO
import com.example.inventarioElectronica.Service.lugarService
import com.example.inventarioElectronica.Service.mantenimientoMesaService
import com.example.inventarioElectronica.Service.usuarioService
import jakarta.validation.Valid
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping

@RequestMapping("/lugar")
@Controller class lugarController(
    private var lugarService: lugarService,
    private val usuarioService: usuarioService,
    private val mantenimientoMesaService: mantenimientoMesaService
) {
    @GetMapping("/practicas/{IDLugar}")
    fun verPracticas(@PathVariable IDLugar: Int, model: Model): String{
        val lugar = lugarService.findById(IDLugar)
        var asignaciones = lugarService.findByMesa(IDLugar)
        if(asignaciones.isEmpty()){ asignaciones= emptyList() }
        model.addAttribute("lugar", lugar.nombre)
        model.addAttribute("asignaciones",asignaciones)
        return "BancoPracticas/listaPracticasPorMesa"
    }

    @GetMapping("/expediente/{IDLugar}")
    fun verExpediente(@PathVariable IDLugar: Int, model: Model): String{
        var expedientes = mantenimientoMesaService.expediente(IDLugar)
        val lugar = lugarService.findById(IDLugar)
        if(expedientes.isEmpty()){ expedientes = emptyList() }
        model.addAttribute("mesa", lugar.nombre)
        model.addAttribute("expedientes",expedientes)
        return "BancoPracticas/expedienteMantenimientoMesa"
    }

    @GetMapping("/mantenimiento/{IDLugar}")
    fun enviarAMantenimiento(@PathVariable IDLugar: Int, model: Model): String {
        val lugar = lugarService.findById(IDLugar)
        val personal = usuarioService.findByRol(listOf("Personal","Jefe"))
        val lugarDTO = lugarDTO(
            idlugar = lugar.IDLugar,
            nombre = lugar.nombre,
            estado = lugar.estado
        )
        model.addAttribute("personal", personal)
        model.addAttribute("lugarDTO", lugarDTO)
        return "BancoPracticas/MantenimientoMesa"
    }

    @PostMapping("/mantenimiento/{IDLugar}")
    fun guardarMantenimiento(@PathVariable IDLugar: Int, @ModelAttribute @Valid lugarDTO: lugarDTO): String{
        mantenimientoMesaService.mantenimiento(lugarDTO)
        return "redirect:/grupos"
    }

    @GetMapping("/habilitar/{IDLugar}")
    fun habilitar(@PathVariable IDLugar: Int, @ModelAttribute @Valid lugarDTO: lugarDTO): String{
        val mantenimiento = mantenimientoMesaService.findByID(IDLugar)
        val lugar = lugarService.findById(IDLugar)
        val lugarDTO = lugarDTO(
            idlugar = lugar.IDLugar,
            numeroControl = mantenimiento.usuario!!.numeroControl,
            fechaEntrada = mantenimiento.fechaEntrada,
            fechaSalida = mantenimiento.fechaSalida,
            fechaUltimoMantenimiento = lugar.fechaUltimoMantenimiento,
            fechaProximoMantenimiento = mantenimiento.fechaProximoMantenimiento
        )
        mantenimientoMesaService.habilitar(lugarDTO)
        return "redirect:/grupos"
    }
}