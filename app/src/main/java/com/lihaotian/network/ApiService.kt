package com.lihaotian.network

import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("m2/4094597-0-default/303330344")
    suspend fun login(@Body loginRequest: LoginRequest): LoginResponse
}

data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val token: String? = null,
    val user: UserInfo? = null
)

data class UserInfo(
    val username: String,
    val email: String,
    val gender: String
) 