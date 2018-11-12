package com.quendeltechfirm.paytmintegration.network

import android.os.RemoteException
import com.google.gson.GsonBuilder
import com.quendeltechfirm.paytmintegration.constants.PaytmConstant.Companion.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.io.InterruptedIOException
import java.net.SocketTimeoutException

class RemoteConnectionService {

    private val retrofit: Retrofit

    companion object {
        private var instance: RemoteConnectionService? = null
        @Synchronized
        fun getInstance(): RemoteConnectionService {
            if (instance == null) {
                instance = RemoteConnectionService()
            }
            return instance!!
        }
    }

    init {

        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val httpClient = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()

        val gson = GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .create()

        retrofit = Retrofit.Builder()
                .client(httpClient)
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
    }

    @Throws(IOException::class, RemoteException::class, SocketTimeoutException::class, InterruptedIOException::class)
    fun generateCheckSum(param: RequestBody): Response<ResponseBody> {
        val service = retrofit.create(RemoteEndpoints::class.java)
        val response = service.generateChecksum(param).execute()
        if (response == null || !response.isSuccessful || response.errorBody() != null) {
            throw RemoteException(response.message())
        }
        return response
    }
}