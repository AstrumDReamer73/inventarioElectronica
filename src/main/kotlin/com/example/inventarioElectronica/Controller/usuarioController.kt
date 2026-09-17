package com.example.inventarioElectronica.Controller

import com.example.inventarioElectronica.DTO.usuarioDTO
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

@RequestMapping("/usuarios")
@Controller class usuarioController(private var usuarioService: usuarioService) {
    @GetMapping fun listarUsuarios(model: Model): String{
        model.addAttribute("usuarios",usuarioService.findAllUsuarios())
        return "listas/listaUsuarios"
    }

    @GetMapping("/usuario/{numeroControl}") @ResponseBody
    fun getUsuario(@PathVariable numeroControl: String): usuarioDTO = usuarioService.findByNumeroControl(numeroControl)

    @GetMapping("/registrar")
    fun mostrarFormularioRegistrar(model: Model): String{
        model.addAttribute("usuario", usuarioDTO())
        model.addAttribute("titulo","Registrar usuario")
        model.addAttribute("accion","/usuarios/registrar")
        return "registrar/registrarUsuario"
    }

    @PostMapping("/registrar")
    fun guardarUsuario(@ModelAttribute("usuario") @Valid usuario: usuarioDTO,
                       result: BindingResult,
                       flash: RedirectAttributes,
                       status: SessionStatus,
                       model: Model): String{
        if (result.hasErrors()) {
            model.addAttribute("titulo","Registrar usuario")
            model.addAttribute("accion","/usuarios/registrar")
            return "registrar/registrarUsuario"
        }
        usuarioService.saveUsuario(usuario)
        flash.addFlashAttribute("success","el usuario a sido registrado con exito")
        status.setComplete()
        return "redirect:/usuarios"
    }

    @GetMapping("/editar/{numeroControl}")
    fun mostrarFormularioEditar(@PathVariable numeroControl: String, model: Model): String{
        val usuario= usuarioService.findByNumeroControl(numeroControl)
        model.addAttribute("usuario", usuario)
        model.addAttribute("titulo","Editar usuario")
        model.addAttribute("accion","/usuarios/editar/${usuario.numeroControl}")
        return "registrar/registrarUsuario"
    }

    @PostMapping("/editar/{numeroControl}")
    fun actualizarUsuario(@PathVariable numeroControl: String,
                          @Valid @ModelAttribute("usuario") usuario: usuarioDTO,
                          result: BindingResult,
                          flash: RedirectAttributes,
                          model: Model): String{
        if(result.hasErrors()){
            model.addAttribute("titulo","Editar usuario")
            model.addAttribute("accion","/usuarios/editar/${numeroControl}")
            return "registrar/registrarUsuario"
        }
        usuarioService.updateUsuario(usuario)
        flash.addFlashAttribute("success","el usuario ha sido actualizado con exito")
        return "redirect:/usuarios"
    }

    @GetMapping("/eliminar/{numeroControl}")
    fun eliminarUsuario(@PathVariable numeroControl: String, flash: RedirectAttributes, status: SessionStatus): String{
        usuarioService.deleteByNumeroControl(numeroControl)
        flash.addFlashAttribute("success","el usuario ha sido eliminado con exito")
        status.setComplete()
        return "redirect:/usuarios"
    }
}