package com.example.inventarioElectronica.Service

import com.example.inventarioElectronica.Repository.avanceCurricularRepository
import org.springframework.stereotype.Service

@Service class avanceCurricularService(private val avanceCurricularRepository: avanceCurricularRepository) {
    fun findAll() = avanceCurricularRepository.findAll()

    fun findByGrupo(claveGrupo: String) = avanceCurricularRepository.findByclaveGrupo_claveGrupo(claveGrupo)
}