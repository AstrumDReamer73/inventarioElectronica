package com.example.inventarioElectronica.Service

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service
import java.io.File

@Service class backupService(
    @Qualifier("jdbcTemplateMaster")
    private val jdbcTemplateMaster: JdbcTemplate,
    private val jdbcTemplate: JdbcTemplate
) {
    fun backupDatabase(dbName: String, path: String) {
        val sql = "BACKUP DATABASE [$dbName] TO DISK = '$path' WITH INIT"
        jdbcTemplate.execute(sql)
        val file = File(path)
        if (!file.exists()) { throw RuntimeException("No se pudo generar el backup") }
    }

    fun restoreDatabase(dbName: String, path: String) {
        val sqlSingleUser = "ALTER DATABASE [$dbName] SET SINGLE_USER WITH ROLLBACK IMMEDIATE"
        val sqlRestore = "RESTORE DATABASE [$dbName] FROM DISK = '$path' WITH REPLACE"
        val sqlMultiUser = "ALTER DATABASE [$dbName] SET MULTI_USER"
        try {
            jdbcTemplateMaster.execute(sqlSingleUser)
            jdbcTemplateMaster.execute(sqlRestore)
            jdbcTemplateMaster.execute(sqlMultiUser)
        } catch (e: Exception) {
            jdbcTemplateMaster.execute(sqlMultiUser)
            throw e
        }
    }

    fun validarBackup(path: String): Boolean {
        return try {
            val result = jdbcTemplateMaster.queryForObject("RESTORE HEADERONLY FROM DISK = '$path'",Map::class.java)
            result != null
        } catch (e: Exception) { false }
    }
}