package com.example.inventarioElectronica

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.datasource.DriverManagerDataSource
import javax.sql.DataSource

@Configuration class databaseConfig(private val environment: Environment) {
    @Bean fun jdbcTemplate(dataSource: DataSource): JdbcTemplate = JdbcTemplate(dataSource)

    @Bean(name = ["jdbcTemplateMaster"])
    fun jdbcTemplateMaster(): JdbcTemplate {
        val ds = DriverManagerDataSource()
        ds.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver")
        ds.url = "jdbc:sqlserver://localhost:1433;databaseName=master;encrypt=true;trustServerCertificate=true"
        ds.username = environment.getProperty("DB_USERNAME")
        ds.password = environment.getProperty("DB_PASSWORD")
        return JdbcTemplate(ds)
    }
}