package com.lihaotian.network

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET

interface ApiService {
    @POST("m2/4094597-0-default/303330344")
    suspend fun login(@Body loginRequest: LoginRequest): LoginResponse

    @GET("m2/4094597-0-default/303847221")
    suspend fun getMusicList(): MusicResponse
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

data class MusicResponse(
    val success: Boolean,
    val message: String,
    val data: List<MusicItem>
)

data class MusicItem(
    val id: Int,
    val name: String,
    val author: String,
    val musicUrl: String,
    val coverUrl: String,
    val dominantColor: String // 主色调，格式为十六进制颜色代码
) 