package com.adopshun.creator.retrofit

import android.content.Context
import com.adopshun.creator.models.QRModel
import com.adopshun.creator.utils.AppConstants
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

interface RetrofitService {

    companion object {

        fun getInstance(context: Context): RetrofitService {
            var retrofitService: RetrofitService? = null

            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY


            if (retrofitService == null) {
                val retrofit = Retrofit.Builder()
                    .baseUrl(AppConstants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(
                        OkHttpClient.Builder()
                            .addInterceptor(NetworkConnectionInterceptor(context))
                            .addInterceptor(loggingInterceptor)
                            .connectTimeout(30, TimeUnit.SECONDS)
                            .readTimeout(30, TimeUnit.SECONDS)
                            .build()
                    )
                    .build()
                retrofitService = retrofit.create(RetrofitService::class.java)
            }
            return retrofitService!!
        }

        fun getAuthInstance(context: Context): RetrofitService {
            var retrofitService: RetrofitService? = null

            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

            if (retrofitService == null) {
                val retrofit = Retrofit.Builder()
                    .baseUrl(AppConstants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(
                        OkHttpClient.Builder()
                            .addInterceptor(AuthInterceptor(context))
                            .addInterceptor(NetworkConnectionInterceptor(context))
                            .addInterceptor(loggingInterceptor)
                            .connectTimeout(30, TimeUnit.SECONDS)
                            .readTimeout(30, TimeUnit.SECONDS)
                            .build()
                    )
                    .build()
                retrofitService = retrofit.create(RetrofitService::class.java)
            }
            return retrofitService!!
        }



    }
    /***------------1--------------------**/
    @Multipart
    @POST("scan-qr")
     fun sendScreenshot(
        @Part("user_id") user_id: RequestBody?,
        @Part("unique_id") unique_id: RequestBody?,
        @Part("meta_data") meta_data: RequestBody?,
        @Part("session_id") session_id: RequestBody?,
        @Part("project_name") project_name: RequestBody?,
        @Part("screen_id") screen_id: RequestBody?,
        @Part("unique_project_id") unique_project_id: RequestBody?,
        @Part image: MultipartBody.Part?
    ): Call<QRModel>






}