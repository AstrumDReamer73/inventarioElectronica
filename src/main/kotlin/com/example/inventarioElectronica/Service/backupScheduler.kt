package com.example.inventarioElectronica.Service

import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@EnableScheduling
@Service class backupScheduler(private val backupService: backupService) {
    @Scheduled(cron = "0 0 0 15 * ?")
    fun fullBackup() = backupService.backupDatabase("ProyectoResidencias","C:\\Backups\\PoyectoResidenciasBackup_${System.currentTimeMillis()}.bak")
}