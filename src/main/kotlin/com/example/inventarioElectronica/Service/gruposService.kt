package com.example.inventarioElectronica.Service

import com.example.inventarioElectronica.DTO.grupoDTO
import com.example.inventarioElectronica.Model.grupo
import com.example.inventarioElectronica.Model.materia
import com.example.inventarioElectronica.Repository.avanceCurricularRepository
import com.example.inventarioElectronica.Repository.gruposRepository
import com.example.inventarioElectronica.Repository.materiasRepository
import com.example.inventarioElectronica.Repository.practicaRepository
import com.example.inventarioElectronica.Repository.usuarioRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.sql.Date
import java.sql.Time
import java.time.LocalTime

@Service @Transactional class gruposService(
    private val gruposRepository: gruposRepository,
    private val usuariosRepository: usuarioRepository,
    private val materiasRepository: materiasRepository,
    private val avanceCurricularRepository: avanceCurricularRepository,
    private val practicaRepository: practicaRepository
) {
    fun saveGrupo(grupo: grupoDTO) {
        if(gruposRepository.existsById(grupo.claveGrupo))
        { throw IllegalArgumentException("ya existe un grupo con la misma clave") }
        if(grupo.horaEntrada >= grupo.horaSalida)
        {throw IllegalArgumentException("La hora de salida debe ser despues de la hora de entrada") }

        val horaEntrada = LocalTime.of(9,0)
        val horaSalida = LocalTime.of(20,0)
        if(grupo.horaEntrada.isBefore(horaEntrada) || grupo.horaSalida.isAfter(horaSalida))
        { throw IllegalArgumentException("La hora debe ser despues de las 9 de la mañana y antes de las 8:00 de la tarde") }

        if(gruposRepository.insertGrupo(salon = grupo.salon,
                                        horaEntrada = Time.valueOf(grupo.horaEntrada),
                                        horaSalida = Time.valueOf(grupo.horaSalida),
                                        diasLaboratorio = grupo.dias.joinToString(separator = ","))==1)
        { throw IllegalArgumentException("Ya existe un grupo con el mismo horario en el salon") }

        val maestro = usuariosRepository.findById(grupo.numeroControl)
            .orElseThrow { IllegalArgumentException("El maestro con numero de control ${grupo.numeroControl} no existe") }

        val materia = materiasRepository.findById(grupo.claveMateria)
            .orElseGet {
                val nueva = materia(claveMateria = grupo.claveMateria, nombreMateria = grupo.nombreMateria)
                materiasRepository.save(nueva)
            }

        val nuevoGrupo = grupo(claveGrupo = grupo.claveGrupo,
                               claveMateria = materia,
                               numeroControl = maestro,
                               salon = grupo.salon,
                               horaEntrada = Time.valueOf(grupo.horaEntrada),
                               horaSalida = Time.valueOf(grupo.horaSalida),
                               diasLaboratorio = grupo.dias.joinToString(","))

        gruposRepository.save(nuevoGrupo)
    }

    fun findAllMaterias() = materiasRepository.findAll()

    fun findByClaveMateria(claveMateria: String) = materiasRepository.findById(claveMateria)

    fun findByMaestro(numeroControl: String) = gruposRepository.findDistinctByNumeroControl_NumeroControl(numeroControl)

    fun findByClaveGrupo(claveGrupo: String) = gruposRepository.findByClaveGrupo(claveGrupo)

    fun findBySalon(salon: String) = gruposRepository.findBySalon(salon)

    fun findByMaterias(claveMateria: String) = gruposRepository.findByClaveMateria_ClaveMateria(claveMateria)

    fun findPracticasSemana(lugar:Int, fechaInicio: Date, fechaTermino:Date) = gruposRepository.findPracticasSemana(lugar, fechaInicio, fechaTermino)

    fun updateGrupo(grupo: grupoDTO) {
        val maestro = usuariosRepository.findById(grupo.numeroControl)
            .orElseThrow{ IllegalArgumentException("El maestro no existe") }

        val horaEntrada = LocalTime.of(9,0)
        val horaSalida = LocalTime.of(20,0)
        if(grupo.horaEntrada.isBefore(horaEntrada) && grupo.horaSalida.isAfter(horaSalida))
        { throw IllegalArgumentException("La hora debe ser despues de las 9 de la mañana y antes de las 8:00 de la tarde") }
        if(grupo.horaEntrada >= grupo.horaSalida){ throw IllegalArgumentException("La hora de fin debe ser despues de la hora de inicio") }

        if(gruposRepository.updateGrupo(salon =  grupo.salon,
                                        horaEntrada = Time.valueOf(grupo.horaEntrada),
                                        horaSalida =  Time.valueOf(grupo.horaSalida),
                                        claveGrupo = grupo.claveGrupo,
                                        diasLaboratorio = grupo.dias.joinToString(separator = ","))==1)
        { throw IllegalArgumentException("Ya existe un grupo con el mismo horario en el mismo salon") }

        val materia = materiasRepository.save(materia(grupo.claveMateria, grupo.nombreMateria))

        gruposRepository.save(
            grupo(claveGrupo = grupo.claveGrupo,
                          numeroControl = maestro,
                          claveMateria = materia,
                          horaEntrada = Time.valueOf(grupo.horaEntrada),
                          horaSalida = Time.valueOf(grupo.horaSalida),
                          salon = grupo.salon,
                          diasLaboratorio = grupo.dias.joinToString(","))
        )
    }

    fun deleteByClaveGrupo(claveGrupo: String) {
        val grupo = gruposRepository.findById(claveGrupo)
            .orElseThrow { IllegalArgumentException("El grupo no existe") }
        val materia = grupo.claveMateria?.claveMateria ?: throw IllegalArgumentException("La materia no existe")
        val avancesCurricular = avanceCurricularRepository.findByclaveGrupo_claveGrupo(claveGrupo)
        avanceCurricularRepository.deleteAll(avancesCurricular)
        gruposRepository.delete(grupo)
        if(!materiasRepository.existsById(materia)) {
            val practicas = practicaRepository.findByMateria_ClaveMateria(materia)
            practicaRepository.deleteAll(practicas)
            materiasRepository.deleteById(materia)
        }
    }
}