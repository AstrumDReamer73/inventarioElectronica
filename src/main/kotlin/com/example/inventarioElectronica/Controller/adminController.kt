package com.example.inventarioElectronica.Controller

import com.example.inventarioElectronica.Service.backupService
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import java.io.File

@RequestMapping("/admin")
@Controller class adminController(private val backupService: backupService) {
    @GetMapping("/backup")
    fun backup(response: HttpServletResponse) {
        val nombreBD = "ProyectoResidencias"
        val ruta = "C:\\Backups\\ProyectoResidencias_${System.currentTimeMillis()}.bak"
        val file = File(ruta)
        backupService.backupDatabase(nombreBD, ruta)

        if(!file.exists()){ throw RuntimeException("no se pudo generar el backup") }
        response.contentType="application/octet-stream"
        response.setHeader("Content-Disposition", "attachment; filename=\"${file.name}")

        file.inputStream().use{
                input ->  response.outputStream.use{
                output -> input.copyTo(output)
            }
        }
    }

    @PostMapping("/restore/upload")
    fun restoreFromFile(@RequestParam file: MultipartFile, flash: RedirectAttributes): String {
        val ruta = "C:\\Backups\\ProyectoResidencias_${System.currentTimeMillis()}.bak"
        val limpio = File(ruta)
        file.inputStream.use{
                input -> limpio.outputStream().use{
                output -> input.copyTo(output)
            }
        }

        backupService.restoreDatabase("ProyectoResidencias", limpio.absolutePath)
        limpio.delete()
        flash.addFlashAttribute("success", "Backup restaurado desde archivo")
        return "redirect:/index"
    }
}