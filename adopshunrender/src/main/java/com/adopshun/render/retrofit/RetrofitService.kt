package com.adopshun.render.retrofit

import android.content.Context
import com.adopshun.render.maintask.AppConstants
import com.adopshun.render.model.SegmentModel
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

interface RetrofitService {

    companion object {

        @JvmStatic
        fun getInstance(context: Context): RetrofitService {
            var retrofitService: RetrofitService? = null

            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

            val gson = GsonBuilder().setLenient().create()
            if (retrofitService == null) {
                val retrofit = Retrofit.Builder()
                    .baseUrl(AppConstants.BASE_URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(gson))
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

        @JvmStatic
        fun getAuthInstance(context: Context, baseUrl: String): RetrofitService {
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


    /***------------2--------------------**/
    @GET("project-details/{project_id}")
    fun getJson(@Path("project_id") project_id: String): Call<JsonObject>


    @GET("project-details/{unique_project_id}/{user_id}")
    fun getJsonWithUserId(@Path("unique_project_id") unique_project_id:String, @Path("user_id") user_id:String ): Call<JsonObject>

    @POST("store-segment-data")
    fun requestCatcher(
        @Header("Authorization") authToken: String,
        @Body segmentModel: SegmentModel
    ): Call<String>
}