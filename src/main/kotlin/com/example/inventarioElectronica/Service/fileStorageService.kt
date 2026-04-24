package com.example.inventarioElectronica.Service

import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.util.UUID

@Service class fileStorageService {
    @Value("\${app.upload.dir}")
    lateinit var uploadPath: String
    private lateinit var uploadDir:File

    @PostConstruct fun init(){
        uploadDir = File(uploadPath)
        if(!uploadDir.exists()){ uploadDir.mkdirs() }
    }

    fun guardarPDF(archivo: MultipartFile?): String{
        if(archivo==null || archivo.isEmpty){ throw IllegalArgumentException("Debe subir un archivo") }
        if(archivo.contentType != "application/pdf"){ throw IllegalArgumentException("Solo se permiten archivos PDF") }

        val nombreOriginal = archivo.originalFilename?:""
        if(!nombreOriginal.lowercase().endsWith(".pdf")){ throw IllegalArgumentException("El archivo debe tener extension .pdf") }

        val nombre = UUID.randomUUID().toString()+".pdf"
        val destino = File(uploadDir, nombre)
        archivo.transferTo(destino)
        return destino.absolutePath
    }

    fun eliminarArchivo(ruta: String?){
        if(ruta.isNullOrBlank()) return
        val file = File(ruta)
        if(file.exists()){ file.delete() }
    }
}