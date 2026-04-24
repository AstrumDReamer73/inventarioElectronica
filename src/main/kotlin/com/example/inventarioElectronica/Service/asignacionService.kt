package com.example.inventarioElectronica.Service

import com.example.inventarioElectronica.DTO.asignacionDTO
import com.example.inventarioElectronica.Model.asignacionPractica
import com.example.inventarioElectronica.Model.mantenimientoArticulo
import com.example.inventarioElectronica.Repository.articuloParticularRepository
import com.example.inventarioElectronica.Repository.asignacionRepository
import com.example.inventarioElectronica.Repository.avanceCurricularRepository
import com.example.inventarioElectronica.Repository.mantenimientoArticuloRepository
import com.example.inventarioElectronica.Repository.gruposRepository
import com.example.inventarioElectronica.Repository.insumosRepository
import com.example.inventarioElectronica.Repository.lugarRepository
import com.example.inventarioElectronica.Repository.practicaRepository
import com.example.inventarioElectronica.Repository.usuarioRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.sql.Date
import java.sql.Time
import java.time.LocalDate

@Service @Transactional class asignacionService(
    private val asignacionRepository: asignacionRepository,
    private val practicasRepository: practicaRepository,
    private val gruposRepository: gruposRepository,
    private val avanceCurricularRepository: avanceCurricularRepository,
    private val insumosRepository: insumosRepository,
    private val usuariosRepository: usuarioRepository,
    private val lugarRepository: lugarRepository,
    private val equipoMantenimientoRepository: mantenimientoArticuloRepository,
    private val articulosRepository: articuloParticularRepository
) {
    fun saveAsignacion(asignacionDTO: asignacionDTO): asignacionPractica {
        val practica = practicasRepository.findByIDPracticas(asignacionDTO.IDPractica)
        val grupo = gruposRepository.findById(asignacionDTO.claveGrupo)
            .orElseThrow { IllegalArgumentException("El grupo no existe") }
        val lugar = lugarRepository.findById(asignacionDTO.IDLugar!!)
            .orElseThrow { IllegalArgumentException("El lugar no existe") }
        avanceCurricularRepository.findByIDPractica_IDPracticasAndClaveGrupo_ClaveGrupo(practica.IDPracticas, grupo.claveGrupo)?.
        apply { fecha=asignacionDTO.fecha
            estado= asignacionDTO.estado }
        return asignacionRepository.save(
            asignacionPractica(IDPractica = practica,
                                       claveGrupo = grupo,
                                       fecha = asignacionDTO.fecha,
                                       estado = asignacionDTO.estado,
                                       lugar = lugar))
    }

    fun findById(IDAsignacion: Int) = asignacionRepository.findById(IDAsignacion)
        .orElseThrow { IllegalArgumentException("La asignacion no existe") }

    fun findAll() = asignacionRepository.findAll()

    fun findByLugar(IDLugar:Int) = asignacionRepository.findByIDLugar_IDLugar(IDLugar)

    fun findLugaresDisponibles(fecha: Date, horaEntrada:Time, horaSalida: Time, IDLugar: Int?) = lugarRepository.checkDisponibilidad(fecha, horaEntrada, horaSalida, IDLugar)

    fun updateAsignacion(id: Int, dto: asignacionDTO) {
        val asignacion = findById(id)
        val insumos = insumosRepository.findByIDAsignacion_IDAsignacion(asignacion.IDAsignacion)
        when(dto.estado){
            "Programada" -> {
                asignacion.fecha = dto.fecha
                asignacion.estado = dto.estado
                asignacion.lugar = lugarRepository.findById(dto.IDLugar!!).orElseThrow { IllegalArgumentException("Wl lugar no existe") }
                asignacionRepository.save(asignacion)
            }

            "Cancelada" ->{
                insumos.forEach {
                    val articulo = it.articulo!!
                    articulo.estado = "Disponible"
                    articulosRepository.save(articulo)
                }
                val avanceCurricular = avanceCurricularRepository.findByIDPractica_IDPracticasAndClaveGrupo_ClaveGrupo(asignacion.IDPractica!!.IDPracticas, asignacion.claveGrupo!!.claveGrupo)
                avanceCurricular!!.estado="Cancelada"
                avanceCurricular.fecha=asignacion.fecha
                avanceCurricularRepository.save(avanceCurricular)
                insumosRepository.deleteAll(insumos)
                asignacionRepository.delete(asignacion)
            }

            "Finalizada" -> {
                insumos.forEach {
                    val articulo = it.articulo!!
                    articulo.estado="Mantenimiento NA"
                    val equipo = mantenimientoArticulo(equipo = articulo,
                                                       personalEncargado = usuariosRepository.findByRolIn(listOf("Personal")).random(),
                                                       motivo = "Inspeccion despues de practica",
                                                       estado = "Asignacion Temporal",
                                                       fechaEntrada = Date.valueOf(LocalDate.now()),
                                                       fechaSalida = Date.valueOf(LocalDate.now()),
                                                       fechaProximoMantenimiento = Date.valueOf(LocalDate.now()))
                    equipoMantenimientoRepository.save(equipo)
                }
                val avanceCurricular = avanceCurricularRepository.findByIDPractica_IDPracticasAndClaveGrupo_ClaveGrupo(asignacion.IDPractica!!.IDPracticas, asignacion.claveGrupo!!.claveGrupo)
                avanceCurricular!!.estado="Completada"
                avanceCurricular.fecha=asignacion.fecha
                avanceCurricularRepository.save(avanceCurricular)
                insumosRepository.deleteAll(insumos)
                asignacionRepository.delete(asignacion)
            }
        }
    }

    fun findByPracticaAndClaveGrupo(idPractica: Int, claveGrupo: String) = asignacionRepository.findByIDPractica_IDPracticasAndClaveGrupo_ClaveGrupo(idPractica, claveGrupo)
}