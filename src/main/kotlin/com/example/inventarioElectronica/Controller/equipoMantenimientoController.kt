package com.example.inventarioElectronica.Controller

import com.example.inventarioElectronica.DTO.equipoMantenimientoDTO
import com.example.inventarioElectronica.Exporters.mantenimientoExporterExcel
import com.example.inventarioElectronica.Exporters.mantenimientoExporterPDF
import com.example.inventarioElectronica.Service.articulosService
import com.example.inventarioElectronica.Service.mantenimientoArticuloService
import com.example.inventarioElectronica.Service.usuarioService
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import org.springframework.web.bind.support.SessionStatus
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import java.time.LocalDate

@RequestMapping("/mantenimiento")
@Controller class equipoMantenimientoController(
    private var equiposMantenimientoService: mantenimientoArticuloService,
    private var articulosService: articulosService,
    private  var usuarioService: usuarioService
) {
    @GetMapping fun listarEquiposMantenimiento(model: Model): String{
        model.addAttribute("equipos",equiposMantenimientoService.findAllEquiposMantenimiento())
        return "listas/listaEquiposMantenimiento"
    }

    @GetMapping("/registrar")
    fun mostrarFormularioAgregar(model: Model): String{
        model.addAttribute("equipoMantenimientoDTO", equipoMantenimientoDTO())
        model.addAttribute("listaArticulos", articulosService.findAllArticulosDisponibles().filter{ it.estado == "Disponible"})
        model.addAttribute("listaUsuarios",usuarioService.findByRol(listOf("Personal","Jefe")))
        model.addAttribute("titulo","Agregar articulo en mantenimiento")
        model.addAttribute("accion","/mantenimiento/registrar")
        model.addAttribute("edicion",false)
        return "registrar/registrarEquipoMantenimiento"
    }

    @GetMapping("/registrar/{numeroSerie}")
    fun mostrarFormularioRegistar(@PathVariable numeroSerie: String, model: Model): String{
        val articulo = articulosService.findByNumeroSerie(numeroSerie)
        val equipoMantenimientoDTO = equipoMantenimientoDTO(numeroSerie=articulo.numeroSerie)
        return "registrar/registrarEquipoMantenimiento"
    }

    @PostMapping("/registrar")
    fun guardarArticulo(@Valid @ModelAttribute equipoMantenimiento: equipoMantenimientoDTO,
                              flash: RedirectAttributes,
                              result: BindingResult,
                              session: SessionStatus,
                              model: Model): String{
        if(result.hasErrors()){
            model.addAttribute("equipoMantenimientoDTO", equipoMantenimiento)
            model.addAttribute("listaArticulos", articulosService.findAllArticulosDisponibles().filter{ it.estado == "Disponible"})
            model.addAttribute("listaUsuarios",usuarioService.findByRol(listOf("Personal","Jefe")))
            model.addAttribute("titulo","Agregar articulo en mantenimiento")
            model.addAttribute("accion","/mantenimiento/registrar")
            model.addAttribute("edicion",false)
        }
        flash.addFlashAttribute("success","Articulo registrado con exito")
        equiposMantenimientoService.saveEquipoMantenimiento(equipoMantenimiento)
        session.setComplete()
        return "redirect:/mantenimiento"
    }

    @GetMapping("editar/{IDEquipoMantenimiento}") fun mostrarFormularioEditar(@PathVariable IDEquipoMantenimiento:Int, model: Model): String{
        val equipoMantenimiento = equiposMantenimientoService.findByIDEquipoMantenimiento(IDEquipoMantenimiento)
        val articulo = equipoMantenimiento!!.equipo
        val usuario = equipoMantenimiento.personalEncargado

        val equipoMantenimientoDTO = equipoMantenimientoDTO(IDEquipoMantenimiento = equipoMantenimiento.IDMantenimiento,
                                                            numeroSerie = articulo!!.numeroSerie,
                                                            numeroControl = usuario!!.numeroControl,
                                                            descripcion = articulo.modelo!!.descripcion,
                                                            motivo = equipoMantenimiento.motivo,
                                                            estado = equipoMantenimiento.estado,
                                                            fechaEntrada = equipoMantenimiento.fechaEntrada,
                                                            fechaSalida = equipoMantenimiento.fechaSalida,
                                                            fechaProximoMantenimiento = equipoMantenimiento.fechaProximoMantenimiento)
        println("equipo en mantenimiento: $equipoMantenimientoDTO")
        model.addAttribute("equipoMantenimientoDTO",equipoMantenimientoDTO)
        model.addAttribute("listaArticulos",listOf(equipoMantenimientoDTO))
        model.addAttribute("listaUsuarios",usuarioService.findByRol(listOf("Personal","Jefe")))
        model.addAttribute("titulo","Editar articulo en mantenimiento")
        model.addAttribute("accion","/mantenimiento/editar/${IDEquipoMantenimiento}")
        model.addAttribute("edicion",true)
        return "registrar/registrarEquipoMantenimiento"
    }

    @PostMapping("/editar/{IDEquipoMantenimiento}")
    fun actualizarEquipoMantenimiento(@PathVariable IDEquipoMantenimiento: Int,
                                      @Valid @ModelAttribute equipoMantenimiento: equipoMantenimientoDTO,
                                      result: BindingResult,
                                      flash: RedirectAttributes,
                                      session: SessionStatus,
                                      model: Model): String {
        if(result.hasErrors()){
            model.addAttribute("equipoMantenimientoDTO", equipoMantenimiento)
            model.addAttribute("listaArticulos", articulosService.findAllArticulosDisponibles().filter{ it.estado == "Disponible"})
            model.addAttribute("listaUsuarios",usuarioService.findByRol(listOf("Personal","Jefe")))
            model.addAttribute("titulo","Agregar articulo en mantenimiento")
            model.addAttribute("accion","/mantenimiento/registrar")
            model.addAttribute("edicion",false)
        }
        if(equipoMantenimiento.estado in listOf("Cancelado","finalizado")){ flash.addFlashAttribute("success","El equipo en mantenimiento ha sido eliminado exitosamente") }
        else{ flash.addFlashAttribute("success", "El equipo en mantenimiento ha sido actualizado con éxito") }
        equiposMantenimientoService.updateEquipoMantenimiento(equipoMantenimiento)
        session.setComplete()
        return "redirect:/mantenimiento"
    }

    @GetMapping("/exportarPDF")
    fun exportarPDF(response: HttpServletResponse){
        response.contentType="application/pdf"
        response.setHeader("Content-Disposition", "attachment; filename=articulos en mantenimiento ${LocalDate.now()}.pdf")
        val exporter = mantenimientoExporterPDF(equiposMantenimientoService.findAllEquiposMantenimiento())
        exporter.exportar(response)
    }

    @GetMapping("/exportarExcel")
    fun exportarExcel(response: HttpServletResponse){
        response.contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        response.setHeader("Content-Disposition","attachment; filename=articulos en mantenimiento ${LocalDate.now()}.xlsx")
        val exporter = mantenimientoExporterExcel(equiposMantenimientoService.findAllEquiposMantenimiento())
        exporter.exportar(response)
    }
}