package com.example.data.remote

import android.util.Log
import com.example.config.AppConfig
import com.example.data.model.BannerItem
import com.example.data.model.Course
import com.example.data.model.Faculty
import com.example.data.model.GalleryItem
import com.example.data.model.InstituteSettings
import com.example.data.model.Notice
import com.example.data.model.ResultItem
import com.example.util.UrlHelper
import org.json.JSONArray
import org.json.JSONObject

/**
 * Resilient JSON Parser for all MEDHA MANTRA remote content models.
 * Automatically resolves relative asset URLs against the active GitHub base URL,
 * accommodates both array and wrapped object structures, and provides detailed error logging.
 */
object JsonParser {
    private const val TAG = "JsonParser"

    fun parseCourses(json: String, baseUrl: String = AppConfig.DEFAULT_GITHUB_BASE_URL): List<Course>? {
        return try {
            val array = extractArray(json, "courses") ?: return null
            val list = mutableListOf<Course>()
            for (i in 0 until array.length()) {
                val obj = array.optJSONObject(i) ?: continue
                val id = obj.opt("id")?.toString() ?: (i + 1).toString()
                val title = obj.optString("title", "")
                val category = obj.optString("category", "General")
                val description = obj.optString("description", "")
                val duration = obj.optString("duration", "")
                val mode = obj.optString("mode", "Offline / Online")
                val fee = obj.optString("fee", "")
                val rawPoster = obj.optString("poster", "")
                val poster = UrlHelper.resolveUrl(baseUrl, rawPoster)

                val admission = obj.optString("admissionInfo", "").ifBlank {
                    obj.optString("admission", "")
                }

                val highlights = mutableListOf<String>()
                val subjArray = obj.optJSONArray("subjects") ?: obj.optJSONArray("highlights")
                if (subjArray != null) {
                    for (j in 0 until subjArray.length()) {
                        subjArray.optString(j)?.let { highlights.add(it) }
                    }
                }

                list.add(
                    Course(
                        id = id,
                        title = title,
                        category = category,
                        description = description,
                        duration = duration,
                        mode = mode,
                        fee = fee,
                        poster = poster,
                        admissionInfo = admission,
                        highlights = highlights
                    )
                )
            }
            Log.d(TAG, "Successfully parsed ${list.size} courses from GitHub JSON")
            list
        } catch (e: Exception) {
            Log.e(TAG, "JSON parsing error for courses: ${e.message}", e)
            null
        }
    }

    fun parseNotices(json: String, baseUrl: String = AppConfig.DEFAULT_GITHUB_BASE_URL): List<Notice>? {
        return try {
            val array = extractArray(json, "notices") ?: return null
            val list = mutableListOf<Notice>()
            for (i in 0 until array.length()) {
                val obj = array.optJSONObject(i) ?: continue
                val id = obj.opt("id")?.toString() ?: (i + 1).toString()
                val title = obj.optString("title", "")
                val date = obj.optString("date", "")
                val desc = obj.optString("description", "").ifBlank { "Official notification from MEDHA MANTRA." }
                val rawFile = obj.optString("file", "").ifBlank { obj.optString("documentUrl", "") }
                val docUrl = UrlHelper.resolveUrl(baseUrl, rawFile)
                val cat = obj.optString("category", "").ifBlank { obj.optString("type", "General") }
                val isImp = obj.optBoolean(
                    "isImportant",
                    title.contains("Admission", ignoreCase = true) ||
                    title.contains("Scholarship", ignoreCase = true) ||
                    title.contains("Special", ignoreCase = true)
                )

                list.add(
                    Notice(
                        id = id,
                        title = title,
                        date = date,
                        description = desc,
                        documentUrl = docUrl,
                        category = cat,
                        isImportant = isImp
                    )
                )
            }
            Log.d(TAG, "Successfully parsed ${list.size} notices from GitHub JSON")
            list
        } catch (e: Exception) {
            Log.e(TAG, "JSON parsing error for notices: ${e.message}", e)
            null
        }
    }

    fun parseFaculty(json: String, baseUrl: String = AppConfig.DEFAULT_GITHUB_BASE_URL): List<Faculty>? {
        return try {
            val array = extractArray(json, "faculty") ?: return null
            val list = mutableListOf<Faculty>()
            for (i in 0 until array.length()) {
                val obj = array.optJSONObject(i) ?: continue
                val name = obj.optString("name", "")
                val id = obj.opt("id")?.toString() ?: name.replace(" ", "_").lowercase().ifBlank { (i + 1).toString() }
                val subject = obj.optString("subject", "")
                val qual = obj.optString("qualification", "")
                val exp = obj.optString("experience", "")
                val rawPhoto = obj.optString("photo", "")
                val photo = UrlHelper.resolveUrl(baseUrl, rawPhoto)
                val bio = obj.optString("bio", "").ifBlank { "$qual with $exp teaching experience in $subject." }

                list.add(
                    Faculty(
                        id = id,
                        name = name,
                        subject = subject,
                        qualification = qual,
                        experience = exp,
                        photo = photo,
                        bio = bio
                    )
                )
            }
            Log.d(TAG, "Successfully parsed ${list.size} faculty from GitHub JSON")
            list
        } catch (e: Exception) {
            Log.e(TAG, "JSON parsing error for faculty: ${e.message}", e)
            null
        }
    }

    fun parseResults(json: String, baseUrl: String = AppConfig.DEFAULT_GITHUB_BASE_URL): List<ResultItem>? {
        return try {
            val array = extractArray(json, "results") ?: return null
            val list = mutableListOf<ResultItem>()
            for (i in 0 until array.length()) {
                val obj = array.optJSONObject(i) ?: continue
                val name = obj.optString("name", "").ifBlank { obj.optString("studentName", "") }
                val exam = obj.optString("exam", "")
                val id = obj.opt("id")?.toString() ?: "${name}_${exam}_$i"
                val rank = obj.optString("rank", "").ifBlank { obj.optString("rankResult", "") }
                val year = obj.optString("year", "")
                val rawPhoto = obj.optString("photo", "")
                val photo = UrlHelper.resolveUrl(baseUrl, rawPhoto)

                list.add(
                    ResultItem(
                        id = id,
                        studentName = name,
                        exam = exam,
                        rankResult = rank,
                        year = year,
                        photo = photo
                    )
                )
            }
            Log.d(TAG, "Successfully parsed ${list.size} results from GitHub JSON")
            list
        } catch (e: Exception) {
            Log.e(TAG, "JSON parsing error for results: ${e.message}", e)
            null
        }
    }

    fun parseBanners(json: String, baseUrl: String = AppConfig.DEFAULT_GITHUB_BASE_URL): List<BannerItem>? {
        return try {
            val array = extractArray(json, "banners") ?: return null
            val list = mutableListOf<BannerItem>()
            for (i in 0 until array.length()) {
                val obj = array.optJSONObject(i) ?: continue
                val id = obj.opt("id")?.toString() ?: "banner_$i"
                val title = obj.optString("title", "")
                val subtitle = obj.optString("subtitle", "")
                val rawImage = obj.optString("imageUrl", "").ifBlank { obj.optString("image", "") }
                val image = UrlHelper.resolveUrl(baseUrl, rawImage)
                val actionUrl = obj.optString("actionUrl", "courses")

                list.add(
                    BannerItem(
                        id = id,
                        title = title,
                        subtitle = subtitle,
                        imageUrl = image,
                        actionUrl = actionUrl
                    )
                )
            }
            Log.d(TAG, "Successfully parsed ${list.size} banners from GitHub JSON")
            list
        } catch (e: Exception) {
            Log.e(TAG, "JSON parsing error for banners: ${e.message}", e)
            null
        }
    }

    fun parseGallery(json: String, baseUrl: String = AppConfig.DEFAULT_GITHUB_BASE_URL): List<GalleryItem>? {
        return try {
            val array = extractArray(json, "gallery") ?: return null
            val list = mutableListOf<GalleryItem>()
            for (i in 0 until array.length()) {
                val obj = array.optJSONObject(i) ?: continue
                val id = obj.opt("id")?.toString() ?: "gallery_$i"
                val title = obj.optString("title", "")
                val cat = obj.optString("category", "Campus")
                val rawImage = obj.optString("imageUrl", "").ifBlank { obj.optString("image", "") }
                val image = UrlHelper.resolveUrl(baseUrl, rawImage)

                list.add(
                    GalleryItem(
                        id = id,
                        title = title,
                        category = cat,
                        imageUrl = image
                    )
                )
            }
            Log.d(TAG, "Successfully parsed ${list.size} gallery items from GitHub JSON")
            list
        } catch (e: Exception) {
            Log.e(TAG, "JSON parsing error for gallery: ${e.message}", e)
            null
        }
    }

    fun parseSettings(json: String): InstituteSettings? {
        return try {
            val trimmed = json.trim()
            val obj = if (trimmed.startsWith("{")) JSONObject(trimmed) else return null
            val settings = InstituteSettings(
                instituteName = obj.optString("instituteName", AppConfig.INSTITUTE_NAME),
                tagline = obj.optString("tagline", AppConfig.INSTITUTE_TAGLINE),
                website = obj.optString("website", AppConfig.DEFAULT_WEBSITE),
                phone = obj.optString("phone", AppConfig.DEFAULT_PHONE),
                whatsapp = obj.optString("whatsapp", AppConfig.DEFAULT_WHATSAPP),
                email = obj.optString("email", AppConfig.DEFAULT_EMAIL),
                address = obj.optString("address", AppConfig.DEFAULT_ADDRESS),
                mapUrl = obj.optString("mapUrl", AppConfig.DEFAULT_MAPS_URL),
                facebook = obj.optString("facebook", "https://facebook.com"),
                youtube = obj.optString("youtube", "https://youtube.com"),
                telegram = obj.optString("telegram", "https://telegram.org"),
                aboutText = obj.optString("aboutText", ""),
                features = extractStringList(obj, "features")
            )
            Log.d(TAG, "Successfully parsed institute settings from GitHub JSON")
            settings
        } catch (e: Exception) {
            Log.e(TAG, "JSON parsing error for settings: ${e.message}", e)
            null
        }
    }

    private fun extractArray(json: String, rootKey: String): JSONArray? {
        val trimmed = json.trim()
        return if (trimmed.startsWith("[")) {
            JSONArray(trimmed)
        } else if (trimmed.startsWith("{")) {
            val obj = JSONObject(trimmed)
            obj.optJSONArray(rootKey) ?: obj.optJSONArray("data") ?: obj.optJSONArray("items")
        } else {
            null
        }
    }

    private fun extractStringList(obj: JSONObject, key: String): List<String> {
        val arr = obj.optJSONArray(key) ?: return emptyList()
        val list = mutableListOf<String>()
        for (i in 0 until arr.length()) {
            arr.optString(i)?.let { list.add(it) }
        }
        return list
    }
}
