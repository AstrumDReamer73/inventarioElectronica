package com.example.inventarioElectronica.Service

import com.example.inventarioElectronica.DTO.usuarioDTO
import com.example.inventarioElectronica.Model.usuario
import com.example.inventarioElectronica.Repository.gruposRepository
import com.example.inventarioElectronica.Repository.usuarioRepository
import jakarta.transaction.Transactional
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service @Transactional class usuarioService(
    private val usuariosRepository: usuarioRepository,
    private val passwordEncoder: PasswordEncoder,
    private val gruposService: gruposService
): UserDetailsService {
    override fun loadUserByUsername(numeroControl: String): UserDetails = usuariosRepository.findById(numeroControl)
                                                                                            .orElseThrow { UsernameNotFoundException("El usuario no existe") }

    fun saveUsuario(usuario: usuarioDTO) {
        if(usuariosRepository.existsById(usuario.numeroControl))
        { throw IllegalArgumentException("ya existe un usuario con numero de control ${usuario.numeroControl}") }
        usuariosRepository.save(
            usuario(numeroControl = usuario.numeroControl,
                            nombre = usuario.nombre,
                            telefono = usuario.telefono.replace(Regex("\\D"),""),
                            correo = usuario.correo.trim(),
                            passwordHash = passwordEncoder.encode(usuario.passwordHash)!!.trim(),
                            rol = usuario.rol)
        )
    }

    fun findAllUsuarios() = usuariosRepository.findAllUsuarios()

    fun findByRol(rol:List<String>) = usuariosRepository.findByRolIn(rol)

    fun findByNumeroControl(numeroControl:String) = usuariosRepository.findByNumeroControl(numeroControl)

    fun findByCorreo(correo: String) = usuariosRepository.findByCorreo(correo)

    fun findByResetToken(token: String) = usuariosRepository.findByResetToken(token)

    fun deleteByNumeroControl(numeroControl: String) {
        val usuarioExistente = usuariosRepository.findById(numeroControl)
            .orElseThrow { throw IllegalArgumentException("el usuario con numero de control ${numeroControl}") }
        if(usuarioExistente.rol == "Personal" && usuariosRepository.findByRolIn(listOf("Personal")).size <= 1)
        { throw IllegalArgumentException("no se puede eliminar al usuario con numero de control ${numeroControl} porque es el unico usuario con rol Personal") }
        if(usuarioExistente.rol == "Maestro" && gruposService.findByMaestro(numeroControl).isEmpty())
        { throw IllegalArgumentException("el maestro tiene grupos asignados, debe eliminarlos primero para poder eliminar al maestro") }
        usuariosRepository.deleteById(numeroControl)
    }

    fun updateUsuario(usuario: usuarioDTO){
        val usuarioExistente = usuariosRepository.findById(usuario.numeroControl)
            .orElseThrow { throw IllegalArgumentException("el usuario con el numero de control ${usuario.numeroControl} no existe") }
        val password = if(usuario.passwordHash.isBlank()){ usuarioExistente.passwordHash }
        else { passwordEncoder.encode(usuario.passwordHash) }

        usuarioExistente.apply {
            nombre = usuario.nombre
            correo = usuario.correo
            telefono = usuario.telefono.replace(Regex("\\D"),"")
            passwordHash = password!!
            rol = usuario.rol
        }
        usuariosRepository.save(usuarioExistente)
    }

    fun updateResetToken(email: String, token: String) {
        val user = usuariosRepository.findByCorreo(email) ?: throw IllegalArgumentException("Correo no encontrado")
        user.resetToken = token
        user.tokenIssuedAt= LocalDateTime.now()
        usuariosRepository.save(user)
    }

    fun updatePassword(token: String, newPassword: String){
        val user= usuariosRepository.findByResetToken(token) ?: throw IllegalArgumentException("Usuario no encontrado")
        val limit = user.tokenIssuedAt!!.plusHours(1)
        if(limit != null && LocalDateTime.now().isBefore(limit)){
            user.passwordHash= passwordEncoder.encode(newPassword)!!.trim()
            user.resetToken=null
            usuariosRepository.save(user)
        }else{
            user.resetToken = null
            usuariosRepository.save(user)
            throw IllegalArgumentException("token expirado")
        }
    }

    fun increaseAttempts(user:usuario){
        user.attempts +=1
        if(user.attempts >=10) { user.lockTime = LocalDateTime.now() }
        usuariosRepository.save(user)
    }

    fun resetAttempts(numeroControl: String){
        val user = usuariosRepository.findById(numeroControl).get()
        user.attempts = 0
        user.lockTime = null
        usuariosRepository.save(user)
    }
}