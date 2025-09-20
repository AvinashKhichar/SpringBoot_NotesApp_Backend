package com.mynotes.notes.security

import com.mynotes.notes.database.model.RefreshToken
import com.mynotes.notes.database.model.User
import com.mynotes.notes.database.repository.RefreshTokenRepository
import com.mynotes.notes.database.repository.UserRepository
import org.bson.types.ObjectId
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.security.MessageDigest
import java.time.Instant
import java.util.Base64

@Service
class AuthService(
    private val jwtServie: JwtService,
    private val userRepository: UserRepository,
    private val hashEncoder: HashEncoder,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val jwtService: JwtService
) {
    data class TokenPair(
        val accessToken: String,
        val refreshToken: String
    )
    fun register(email: String, password: String): User {
        val user = userRepository.findByEmail(email.trim())
        if(user != null) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "A user with that email already exists.")
        }
        return userRepository.save(
            User(
                email = email,
                hashedpassword = hashEncoder.encode(password),
            )
        )
    }


    fun login(email: String, password: String): TokenPair{
        val user = userRepository.findByEmail(email)?: throw BadCredentialsException("This email does not exist.")

        if(!hashEncoder.matches(password, user.hashedpassword)){
            throw BadCredentialsException("Passwords do not match.")
        }

        val newAccessToken = jwtServie.generateAccessToken(user.id.toHexString())
        val newRefreshToken = jwtServie.generateRefreshToken(user.id.toHexString())

        storeRefreshToken(user.id, newRefreshToken)


        return TokenPair(
            accessToken = newAccessToken,
            refreshToken = newRefreshToken
        )
    }

    @Transactional
    fun refresh(refresToken: String): TokenPair{
        if(!jwtService.validateRefreshToken(refresToken)){
            throw ResponseStatusException(HttpStatusCode.valueOf(401),"Invalid refresh token.")
        }

        val userId = jwtService.getUserIdFromToken(refresToken)
        val user = userRepository.findById(ObjectId(userId)).orElseThrow{
            ResponseStatusException(HttpStatusCode.valueOf(401),"Invalid Refresh token.")
        }

        val hashed = hashToken(refresToken)
        refreshTokenRepository.findByUserIdAndHashedToken(user.id, hashed)
            ?: throw ResponseStatusException(HttpStatusCode.valueOf(401),"Refresh token not recognised")

        refreshTokenRepository.deleteByUserIdAndHashedToken(user.id, hashed)

        val newAccessToken = jwtService.generateAccessToken(userId)
        val newRefreshToken = jwtServie.generateRefreshToken(userId)

        storeRefreshToken(user.id, newRefreshToken)

        return TokenPair(
            accessToken = newAccessToken,
            refreshToken = newRefreshToken
        )

    }

    private fun storeRefreshToken(userId: ObjectId, rawRefreshToken: String){
        val hashed = hashToken(rawRefreshToken)
        val expiryMs = jwtService.refreshTokenValidityMs
        val expiresAt = Instant.now().plusMillis(expiryMs)

        refreshTokenRepository.save(
            RefreshToken(
                userId = userId,
                expiresAt = expiresAt,
                hashedToken = hashed
            )
        )
    }

    private fun hashToken(token: String): String{
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(token.encodeToByteArray())
        return Base64.getEncoder().encodeToString(hashBytes)
    }

}