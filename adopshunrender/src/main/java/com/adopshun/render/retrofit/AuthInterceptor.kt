package com.adopshun.render.retrofit

import android.content.Context
import com.adopshun.render.maintask.PreferencesManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(var context: Context?) : Interceptor {

    private var sessionManager = PreferencesManager(context!!)

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()
        PreferencesManager.initializeInstance(context = context!!)
        sessionManager = PreferencesManager.instance!!


        return chain.proceed(requestBuilder.build())
    }
}
