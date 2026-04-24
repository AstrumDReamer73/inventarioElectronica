package com.example.inventarioElectronica.Controller

import com.example.inventarioElectronica.DTO.articuloDTO
import com.example.inventarioElectronica.Exporters.articulosExporterExcel
import com.example.inventarioElectronica.Exporters.articulosExporterPDF
import com.example.inventarioElectronica.Service.articulosService
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import java.time.LocalDate

@RequestMapping("/articulos")
@Controller class articulosController(private var articulosService: articulosService){
    @GetMapping fun listarArticulos(model: Model): String {
        model.addAttribute("articulos",articulosService.findAllArticulos())
        return "listas/listaArticulos"
    }

    @GetMapping("/articulo/{numeroSerie}") @ResponseBody
    fun obtenerArticulo(@PathVariable numeroSerie: String) = articulosService.findByNumeroSerie(numeroSerie)

    @GetMapping("/modelo/{modelo}") @ResponseBody
    fun obtenerModelo(@PathVariable modelo: String) = articulosService.findByModelo(modelo)

    @GetMapping("/expediente/{numeroSerie}")
    fun mostrarExpediente(@PathVariable numeroSerie: String, model: Model): String{
        val expediente = articulosService.hacerExpediente(numeroSerie)
        model.addAttribute("articulo", numeroSerie)
        model.addAttribute("expedientes", expediente)
        return "/listas/expediente"
    }

    @GetMapping("/registrar")
    fun mostrarFormularioRegistrar(model: Model): String {
        model.addAttribute("articuloDTO", articuloDTO(estado = "Disponible"))
        model.addAttribute("titulo", "Registrar articulos")
        model.addAttribute("accion", "/articulos/registrar")
        model.addAttribute("listaArticulos",articulosService.findAllArticulos())
        model.addAttribute("listaModelos", articulosService.findAllModelos())
        model.addAttribute("listaCategorias", articulosService.findAllCategorias())
        model.addAttribute("listaUbicacion", articulosService.findAllUbicaciones())
        return "registrar/registrarArticulos"
    }

    @PostMapping("/registrar")
    fun guardarArticulo(@Valid @ModelAttribute articulo: articuloDTO,
                        result: BindingResult,
                        flash: RedirectAttributes,
                        model: Model): String {
        if (result.hasErrors()) {
            model.addAttribute("articuloDTO", articulo)
            model.addAttribute("titulo", "Registrar articulos")
            model.addAttribute("accion", "/articulos/registrar")
            model.addAttribute("listaArticulos",articulosService.findAllArticulos())
            model.addAttribute("listaModelos", articulosService.findAllModelos())
            model.addAttribute("listaCategorias", articulosService.findAllCategorias())
            model.addAttribute("listaUbicacion", articulosService.findAllUbicaciones())
            return "registrar/registrarArticulos"
        }
        articulosService.saveArticulo(articulo)
        flash.addFlashAttribute("success", "el Articulo ha sido registrado exitosamente")
        return "redirect:/articulos"
    }

    @GetMapping("/editar/{numeroSerie}")
    fun mostrarFormularioEditar(@PathVariable numeroSerie: String, model: Model): String {
        val articuloDTO = articulosService.findByNumeroSerie(numeroSerie)
        model.addAttribute("articuloDTO",articuloDTO)
        model.addAttribute("listaArticulos",articulosService.findAllArticulos())
        model.addAttribute("listaModelos", articulosService.findAllModelos())
        model.addAttribute("listaCategorias", articulosService.findAllCategorias())
        model.addAttribute("listaUbicacion", articulosService.findAllUbicaciones())
        model.addAttribute("titulo","Actualizar articulo")
        model.addAttribute("accion","/articulos/editar/${articuloDTO.numeroSerie}")
        return "registrar/registrarArticulos"
    }

    @PostMapping("/editar/{numeroSerie}")
    fun actualizarArticulo(@PathVariable numeroSerie: String,
                           @ModelAttribute @Valid articulo: articuloDTO,
                           result: BindingResult,
                           flash: RedirectAttributes,
                           model: Model): String {
        if(result.hasErrors()){
            model.addAttribute("articuloDTO",articulo)
            model.addAttribute("listaArticulos",articulosService.findAllArticulos())
            model.addAttribute("listaModelos", articulosService.findAllModelos())
            model.addAttribute("listaCategorias", articulosService.findAllCategorias())
            model.addAttribute("listaUbicacion", articulosService.findAllUbicaciones())
            model.addAttribute("titulo","Actualizar articulo")
            model.addAttribute("accion","/articulos/editar/${numeroSerie}")
            return "registrar/registrarArticulos"
        }
        articulosService.updateArticulo(articulo)
        flash.addFlashAttribute("success","articulo modificado exitosamente")
        return "redirect:/articulos"
    }

    @GetMapping("/exportarPDF")
    fun exportarPDF(response: HttpServletResponse){
        response.contentType="application/pdf"
        response.setHeader("Content-Disposition","attachment; filename=articulos ${LocalDate.now()}.pdf")
        val exporter= articulosExporterPDF(articulosService.findAllArticulos().sortedBy{ it.ubicacion })
        exporter.exportar(response)
    }

    @GetMapping("/exportarExcel")
    fun exportarExcel(response: HttpServletResponse){
        response.contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        response.setHeader("Content-Disposition","attachment; filename=articulos ${LocalDate.now()}.xlsx")
        val exporter = articulosExporterExcel(articulosService.findAllArticulos().sortedBy{ it.ubicacion })
        exporter.exportar(response)
    }
}