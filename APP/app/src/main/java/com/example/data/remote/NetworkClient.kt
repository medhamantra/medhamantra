package com.example.data.remote

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.util.concurrent.TimeUnit

data class NetworkResult(
    val statusCode: Int,
    val body: String?,
    val isSuccessful: Boolean,
    val errorMessage: String? = null
)

/**
 * Resilient, unauthenticated network client for fetching public dynamic JSON files
 * from raw.githubusercontent.com or GitHub Pages.
 */
object NetworkClient {
    private const val TAG = "NetworkClient"

    val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .followRedirects(true)
            .followSslRedirects(true)
            .retryOnConnectionFailure(true)
            .build()
    }

    /**
     * Executes HTTP GET request asynchronously and returns detailed HTTP response status and body.
     */
    suspend fun fetch(url: String): NetworkResult = withContext(Dispatchers.IO) {
        Log.d(TAG, "GitHub URL being requested: $url")
        try {
            val request = Request.Builder()
                .url(url)
                .header("Cache-Control", "no-cache")
                .header("Accept", "application/json, text/plain, */*")
                .header("User-Agent", "MedhaMantraApp/1.0 (Android; GitHub Dynamic Content)")
                .build()

            val response = okHttpClient.newCall(request).execute()
            val code = response.code
            Log.d(TAG, "HTTP response code for $url: $code")

            if (response.isSuccessful) {
                val body = response.body?.string()
                if (!body.isNullOrBlank()) {
                    NetworkResult(statusCode = code, body = body, isSuccessful = true)
                } else {
                    val errMsg = "HTTP $code: Empty response body from $url"
                    Log.e(TAG, errMsg)
                    NetworkResult(statusCode = code, body = null, isSuccessful = false, errorMessage = errMsg)
                }
            } else {
                val errMsg = "HTTP $code: ${response.message.ifBlank { "Request failed" }}"
                Log.e(TAG, "HTTP error for $url: code $code, message: ${response.message}")
                NetworkResult(statusCode = code, body = null, isSuccessful = false, errorMessage = errMsg)
            }
        } catch (e: Exception) {
            val errMsg = e.localizedMessage ?: "Connection error (${e.javaClass.simpleName})"
            Log.e(TAG, "Network exception requesting $url: $errMsg", e)
            NetworkResult(statusCode = -1, body = null, isSuccessful = false, errorMessage = errMsg)
        }
    }

    /**
     * Legacy wrapper returning Result<String>
     */
    suspend fun fetchString(url: String): Result<String> {
        val res = fetch(url)
        return if (res.isSuccessful && res.body != null) {
            Result.success(res.body)
        } else {
            Result.failure(IOException(res.errorMessage ?: "HTTP ${res.statusCode}"))
        }
    }
}
