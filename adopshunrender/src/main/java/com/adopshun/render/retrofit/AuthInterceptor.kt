package com.adopshun.render.retrofit

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val authToken: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val requestBuilder = original.newBuilder()
            .header("Authorization", "Bearer $authToken") // Add the Bearer token
        val request = requestBuilder.build()
        return chain.proceed(request)
    }
}