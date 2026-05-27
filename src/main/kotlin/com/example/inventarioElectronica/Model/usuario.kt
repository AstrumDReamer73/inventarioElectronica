package com.example.inventarioElectronica.Model

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.time.LocalDateTime

@Table(name = "usuarios")
@Entity data class usuario(
    @Id var numeroControl: String="",
    var nombre:String ="",
    var telefono: String="",
    var correo:String ="",
    var passwordHash: String ="",
    var rol: String="",
    var attempts: Int = 0,
    var resetToken: String? = null,
    var tokenIssuedAt: LocalDateTime? = LocalDateTime.now(),
    var lockTime: LocalDateTime? = null
): UserDetails{
    override fun getAuthorities(): Collection<GrantedAuthority> = listOf(SimpleGrantedAuthority(rol))
    override fun getPassword(): String = passwordHash
    override fun getUsername(): String = numeroControl
    override fun isAccountNonExpired() = true
    override fun isCredentialsNonExpired() = true
    override fun isEnabled() = true

    override fun isAccountNonLocked(): Boolean{
        if(lockTime==null) return true
        return lockTime!!.plusMinutes(30).isBefore(LocalDateTime.now())
    }
}