package com.example.inventarioElectronica

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.datasource.DriverManagerDataSource
import javax.sql.DataSource

@Configuration class databaseConfig {
    @Bean fun dataSource(): DataSource {
        val ds = DriverManagerDataSource()
        ds.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver")
        ds.url = "jdbc:sqlserver://localhost:1433;databaseName=ProyectoResidencias;encrypt=true;trustServerCertificate=true"
        ds.username = "sa"
        ds.password = "AstrumDReamer73"
        return ds
    }

    @Bean fun jdbcTemplate(dataSource: DataSource): JdbcTemplate = JdbcTemplate(dataSource)

    @Bean(name = ["jdbcTemplateMaster"])
    fun jdbcTemplateMaster(): JdbcTemplate {
        val ds = DriverManagerDataSource()
        ds.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver")
        ds.url = "jdbc:sqlserver://localhost:1433;databaseName=master;encrypt=true;trustServerCertificate=true"
        ds.username = "sa"
        ds.password = "AstrumDReamer73"
        return JdbcTemplate(ds)
    }
}