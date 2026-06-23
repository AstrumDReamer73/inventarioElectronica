package com.example.inventarioElectronica.Controller

import com.example.inventarioElectronica.Service.usuarioService
import com.example.inventarioElectronica.commonUtil
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpSession
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.util.ObjectUtils
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.util.UUID

@RequestMapping("/")
@Controller class homeController(
    private val usuarioService: usuarioService,
    private val commonUtil: commonUtil
) {
    @GetMapping fun login(): String = "login"

    @GetMapping("/index") fun indexJefe(): String = "index"

    @GetMapping("/forgotPassword") fun olvidoContraseña():String = "confirmarEmail"

    @PostMapping("/forgotPassword")
    fun procesarNuevaContraseña(@RequestParam email: String, session: HttpSession, request: HttpServletRequest): String{
        val usuario = usuarioService.findByCorreo(email)
        if(ObjectUtils.isEmpty(usuario)){
            session.setAttribute("errorMessage","correo invalido")
            return "redirect:/forgotPassword"
        }else{
            val resetToken = UUID.randomUUID().toString()
            usuarioService.updateResetToken(email, resetToken)
            val url = commonUtil.generateUrl(request)+"/resetPassword?token="+resetToken
            val correoEnviado = commonUtil.sendMail(url, email)
            if(correoEnviado){ session.setAttribute("errorMessage","Correo enviado, por favor cheque su correo") }
            else{ session.setAttribute("errorMessage","el correo no fue enviado") }
        }
        return "redirect:/resetPassword"
    }

    @GetMapping("/resetPassword")
    fun mostrarCambioContraseña(@RequestParam token: String, session: HttpSession, model: Model): String{
        val user = usuarioService.findByResetToken(token)
        return if(user == null){
            session.setAttribute("errorMessage","El link es invalido o ya expiro")
            "redirect:/forgotPassword"
        }else{
            model.addAttribute("token", token)
            "nuevaContraseña"
        }
    }

    @PostMapping("/resetPassword")
    fun cambiarContraseña(@RequestParam token: String,
                          @RequestParam password: String,
                          model: Model): String{
        val user = usuarioService.findByResetToken(token)
        return if(user == null){
            model.addAttribute("errorMsg","el link es invalido o expirado")
            "redirect:/forgotPassword"
        } else{
            usuarioService.updatePassword(token, password)
            model.addAttribute("errorMsg","contraseña actualizada con exito")
            "redirect:/"
        }
    }
}