package com.example.util

import android.util.Log

object UrlHelper {
    private const val TAG = "UrlHelper"

    /**
     * Resolves any relative or absolute asset/document path against a given base URL.
     * Prevents double slashes, strips leading "./" or "/", and preserves full URLs.
     *
     * Example:
     * Base: "https://raw.githubusercontent.com/kingkingng2222-prog/margepdflove/main/"
     * Raw: "./assets/courses/ssc-cgl.jpg"
     * -> "https://raw.githubusercontent.com/kingkingng2222-prog/margepdflove/main/assets/courses/ssc-cgl.jpg"
     */
    fun resolveUrl(baseUrl: String, rawPath: String?): String {
        if (rawPath.isNullOrBlank()) return ""
        val trimmed = rawPath.trim()

        // If it's already an absolute web URL or special scheme, return directly
        if (trimmed.startsWith("http://", ignoreCase = true) ||
            trimmed.startsWith("https://", ignoreCase = true) ||
            trimmed.startsWith("data:", ignoreCase = true) ||
            trimmed.startsWith("file:", ignoreCase = true)
        ) {
            return trimmed
        }

        return try {
            val base = if (baseUrl.trim().endsWith("/")) baseUrl.trim() else "${baseUrl.trim()}/"
            var cleanPath = trimmed
            while (cleanPath.startsWith("./")) {
                cleanPath = cleanPath.removePrefix("./")
            }
            while (cleanPath.startsWith("/")) {
                cleanPath = cleanPath.removePrefix("/")
            }
            val resolved = base + cleanPath
            Log.d(TAG, "Converted asset URL: '$rawPath' -> '$resolved'")
            resolved
        } catch (e: Exception) {
            Log.e(TAG, "Image/file URL conversion error for '$rawPath': ${e.message}", e)
            rawPath
        }
    }

    /**
     * Joins base URL and endpoint path ensuring exactly one slash between them.
     */
    fun buildEndpointUrl(baseUrl: String, endpointPath: String): String {
        val base = if (baseUrl.trim().endsWith("/")) baseUrl.trim() else "${baseUrl.trim()}/"
        var path = endpointPath.trim()
        while (path.startsWith("/")) {
            path = path.removePrefix("/")
        }
        return base + path
    }
}
