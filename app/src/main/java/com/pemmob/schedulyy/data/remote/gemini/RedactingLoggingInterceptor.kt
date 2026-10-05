package com.pemmob.schedulyy.data.remote.gemini

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

class RedactingLoggingInterceptor(
    private val isDebug: Boolean = true
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        if (!isDebug) {
            // Logging HTTP mati total di mode release sesuai §8
            return chain.proceed(originalRequest)
        }

        val url = originalRequest.url.toString()
        val method = originalRequest.method
        val headers = originalRequest.headers

        // Meredaksi header x-goog-api-key agar tidak pernah tercatat di Logcat
        val safeHeaders = headers.names().associateWith { name ->
            if (name.equals("x-goog-api-key", ignoreCase = true)) {
                "[REDACTED]"
            } else {
                headers.values(name).joinToString(", ")
            }
        }

        Log.d(TAG, "--> $method $url")
        Log.d(TAG, "Headers: $safeHeaders")

        val response = chain.proceed(originalRequest)

        Log.d(TAG, "<-- ${response.code} ${response.message} ($url)")
        return response
    }

    companion object {
        private const val TAG = "GeminiHttp"
    }
}
