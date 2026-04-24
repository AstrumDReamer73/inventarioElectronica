package com.example.inventarioElectronica.Service

import com.example.inventarioElectronica.DTO.practicaDTO
import com.example.inventarioElectronica.Model.avanceCurricular
import com.example.inventarioElectronica.Model.practica
import com.example.inventarioElectronica.Repository.asignacionRepository
import com.example.inventarioElectronica.Repository.avanceCurricularRepository
import com.example.inventarioElectronica.Repository.gruposRepository
import com.example.inventarioElectronica.Repository.materiasRepository
import com.example.inventarioElectronica.Repository.practicaRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.io.File
import java.sql.Date
import java.time.LocalDate

@Service @Transactional class practicaService(
    private val practicasRepository: practicaRepository,
    private val materiasRepository: materiasRepository,
    private val gruposRepository: gruposRepository,
    private val avanceCurricularRepository: avanceCurricularRepository,
    private val asignacionRepository: asignacionRepository,
    private val fileStorageService: fileStorageService
) {
    fun savePractica(practicaDTO: practicaDTO): practica {
        val materia = materiasRepository.findById(practicaDTO.materia)
            .orElseThrow { IllegalArgumentException("La materia no existe") }
        val grupos = gruposRepository.findByClaveMateria_ClaveMateria(materia.claveMateria)
        val ruta = fileStorageService.guardarPDF(practicaDTO.archivo)
        val practica = practicasRepository.save(
            practica(materia = materia,
                nombre = practicaDTO.nombre.trim(),
                archivo = ruta))

        val avances = grupos.map {
            avanceCurricular(IDPractica = practica,
                claveGrupo = it,
                fecha = Date.valueOf(LocalDate.now()),
                estado = "No programada")
        }
        avanceCurricularRepository.saveAll(avances)
        return practica
    }

    fun findAll() = practicasRepository.findAll()

    fun findByIDPractica(ID: Int) = practicasRepository.findByIDPracticas(ID)

    fun updatePractica(ID:Int,dto: practicaDTO){
        val practica = practicasRepository.findById(ID)
            .orElseThrow { IllegalArgumentException("La practica no existe") }
        val materiaNueva = materiasRepository.findById(dto.materia)
            .orElseThrow { IllegalArgumentException("La materia no existe") }
        val materiaViejaID = practica.materia!!.claveMateria
        val materiaNuevaID = dto.materia
        val archivoNuevo = dto.archivo

        practica.apply {
            this.materia = materiaNueva
            this.nombre = dto.nombre.trim()

            if (archivoNuevo != null && !archivoNuevo.isEmpty) {
                fileStorageService.eliminarArchivo(archivo)
                this.archivo = fileStorageService.guardarPDF(dto.archivo)
            }
        }

        practicasRepository.save(practica)
        if(materiaViejaID != materiaNuevaID){
            val avancesViejos = avanceCurricularRepository.findByIDPractica_IDPracticas(ID)
            avanceCurricularRepository.deleteAll(avancesViejos)
            avanceCurricularRepository.flush()

            val gruposNuevos = gruposRepository.findByClaveMateria_ClaveMateria(materiaNuevaID)
            val nuevosAvances = gruposNuevos.map {
                avanceCurricular(IDPractica = practica,
                                 claveGrupo = it,
                                 fecha = Date.valueOf(LocalDate.now()),
                                 estado = "No programada")
            }.distinctBy { it.claveGrupo!!.claveGrupo }
            avanceCurricularRepository.saveAll(nuevosAvances)
        }else{
            val gruposActuales = gruposRepository.findByClaveMateria_ClaveMateria(materiaNuevaID)
            val avancesExistentes = avanceCurricularRepository.findByIDPractica_IDPracticas(ID)
            val gruposConAvance = avancesExistentes.map { it.claveGrupo!!.claveGrupo }.toSet()

            val avancesFaltantes = gruposActuales.filter { it.claveGrupo !in gruposConAvance }
                .distinctBy { it.claveGrupo }
                .map{ avanceCurricular(IDPractica = practica,
                                       claveGrupo = it,
                                       fecha = Date.valueOf(LocalDate.now()),
                                       estado = "No programada") }
            if(avancesFaltantes.isNotEmpty()){ avanceCurricularRepository.saveAll(avancesFaltantes) }
        }
    }

    fun deletePractica(ID: Int) {
        val practica = practicasRepository.findById(ID)
                                          .orElseThrow { IllegalArgumentException("La practica no existe") }

        if(asignacionRepository.findByIDPractica_IDPracticas(ID).isNotEmpty())
        { throw IllegalStateException("No se puede eliminar la practica debido a que hay asignaciones pendientes") }

        val ruta = practica.archivo
        if(ruta.isNotBlank()){
            val file = File(ruta)
            if(file.exists()) file.delete()
        }
        fileStorageService.eliminarArchivo(practica.archivo)
        avanceCurricularRepository.deleteAll(avanceCurricularRepository.findByIDPractica_IDPracticas(ID))
        practicasRepository.delete(practica)
    }
}