package com.example.zadaniemobv.webservice

import android.content.Context
import com.example.zadaniemobv.others.PreferenceData
import okhttp3.Interceptor
import okhttp3.Response

class Interceptor(val context: Context) : Interceptor {
    private val api_key="c95332ee022df8c953ce470261efc695ecf3e784"
    override fun intercept(chain: Interceptor.Chain): Response {
        synchronized(this) {
            val request = chain.request()
                .newBuilder()
                .addHeader("User-Agent", "Mobv-Android/1.0.0")
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
            if (chain.request().header("needAuth")?.compareTo("yes") == 0) {
                request.addHeader(
                    "Authorization",
                    "Bearer ${PreferenceData.getInstance().getUserItem(context)?.access}"
                )
            }
            PreferenceData.getInstance().getUserItem(context)?.id?.let {
                request.addHeader(
                    "x-user",
                    it
                )
            }
            request.addHeader("x-apikey", api_key)
            val response = chain.proceed(request.build())
            return response
        }
    }

}