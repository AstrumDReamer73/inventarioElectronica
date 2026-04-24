package com.example.inventarioElectronica

import jakarta.servlet.http.HttpServletRequest
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Component

@Component class commonUtil(private val mailSender: JavaMailSender) {
    fun sendMail(url: String, email: String): Boolean{
        return try{
            val message = mailSender.createMimeMessage()
            val helper = MimeMessageHelper(message)
            helper.setFrom("no-reply@cdmadero.tecnm.mx","Sistema de laboratorio de ingeniera electronica - ITCM")
            helper.setTo(email)
            val content = """
                <p>Hola,</p>
                <p>Ha solicitado modificar su contraseña.</p>
                <p>Haga clic en el siguiente enlace para cambiar su contraseña:</p>
                <p><a href="$url">Cambiar mi contraseña</a></p>
            """.trimIndent()
            helper.setSubject("Cambiar mi contraseña")
            helper.setText(content, true)
            mailSender.send(message)
            true
        }catch (e: Exception){
            e.printStackTrace()
            false
        }
    }

    fun generateUrl(request: HttpServletRequest): String {
        val siteUrl =request.requestURL.toString()
        return siteUrl.replace(request.servletPath, "")
    }
}