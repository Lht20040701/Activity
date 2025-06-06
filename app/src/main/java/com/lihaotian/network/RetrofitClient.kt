package com.lihaotian.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

// 使用RetrofitClient请求，封装加强了okhttp的请求工具
// 详细参考https://square.github.io/retrofit/declarations/
object RetrofitClient {
    // 使用10.0.2.2来访问主机的localhost（适用于模拟器）
    private const val BASE_URL = "http://10.0.2.2:4523/"

    // 自定义一下okhttp，增加日志，方便debug
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient) // 为了方便调试，把原本的okhttp换掉
        .addConverterFactory(GsonConverterFactory.create()) // 神秘转换器https://github.com/google/gson javaObject -> JSON and JSON -> javaObject, 同样支持java
        .build()

    val apiService: ApiService = retrofit.create(ApiService::class.java)
} 