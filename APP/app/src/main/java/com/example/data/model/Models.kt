package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Course model matching data/courses.json
 */
@JsonClass(generateAdapter = true)
data class Course(
    @Json(name = "id") val id: String = "",
    @Json(name = "title") val title: String = "",
    @Json(name = "category") val category: String = "General",
    @Json(name = "description") val description: String = "",
    @Json(name = "duration") val duration: String = "",
    @Json(name = "mode") val mode: String = "Offline / Online",
    @Json(name = "fee") val fee: String = "",
    @Json(name = "poster") val poster: String = "",
    @Json(name = "admissionInfo") val admissionInfo: String = "",
    @Json(name = "highlights") val highlights: List<String> = emptyList()
)

/**
 * Notice model matching data/notices.json
 */
@JsonClass(generateAdapter = true)
data class Notice(
    @Json(name = "id") val id: String = "",
    @Json(name = "title") val title: String = "",
    @Json(name = "date") val date: String = "",
    @Json(name = "description") val description: String = "",
    @Json(name = "documentUrl") val documentUrl: String = "",
    @Json(name = "category") val category: String = "General",
    @Json(name = "isImportant") val isImportant: Boolean = false
)

/**
 * Faculty member model matching data/faculty.json
 */
@JsonClass(generateAdapter = true)
data class Faculty(
    @Json(name = "id") val id: String = "",
    @Json(name = "name") val name: String = "",
    @Json(name = "subject") val subject: String = "",
    @Json(name = "qualification") val qualification: String = "",
    @Json(name = "experience") val experience: String = "",
    @Json(name = "photo") val photo: String = "",
    @Json(name = "bio") val bio: String = ""
)

/**
 * Exam result achievement model matching data/results.json
 */
@JsonClass(generateAdapter = true)
data class ResultItem(
    @Json(name = "id") val id: String = "",
    @Json(name = "studentName") val studentName: String = "",
    @Json(name = "exam") val exam: String = "",
    @Json(name = "rankResult") val rankResult: String = "",
    @Json(name = "year") val year: String = "",
    @Json(name = "photo") val photo: String = ""
)

/**
 * Hero Banner item matching data/banners.json
 */
@JsonClass(generateAdapter = true)
data class BannerItem(
    @Json(name = "id") val id: String = "",
    @Json(name = "title") val title: String = "",
    @Json(name = "subtitle") val subtitle: String = "",
    @Json(name = "imageUrl") val imageUrl: String = "",
    @Json(name = "actionUrl") val actionUrl: String = ""
)

/**
 * Photo Gallery item matching data/gallery.json
 */
@JsonClass(generateAdapter = true)
data class GalleryItem(
    @Json(name = "id") val id: String = "",
    @Json(name = "title") val title: String = "",
    @Json(name = "category") val category: String = "Campus",
    @Json(name = "imageUrl") val imageUrl: String = ""
)

/**
 * Institute Contact, About & Social settings matching data/settings.json
 */
@JsonClass(generateAdapter = true)
data class InstituteSettings(
    @Json(name = "instituteName") val instituteName: String = "MEDHA MANTRA Competitive Institute",
    @Json(name = "tagline") val tagline: String = "EMPOWERING MINDS, CREATING FUTURES",
    @Json(name = "website") val website: String = "https://medhamantra.com",
    @Json(name = "phone") val phone: String = "6297034796",
    @Json(name = "whatsapp") val whatsapp: String = "6297034796",
    @Json(name = "email") val email: String = "medhamantra.memari@gmail.com",
    @Json(name = "address") val address: String = "1st Floor, Malancha Complex, Chakdighi Road, Near Abhijan Sangha Club, Memari, Purba Bardhaman 713146",
    @Json(name = "mapUrl") val mapUrl: String = "https://maps.google.com/?q=Malancha+Complex+Chakdighi+Road+Memari+Purba+Bardhaman+713146",
    @Json(name = "facebook") val facebook: String = "https://facebook.com",
    @Json(name = "youtube") val youtube: String = "https://youtube.com",
    @Json(name = "telegram") val telegram: String = "https://telegram.org",
    @Json(name = "aboutText") val aboutText: String = "MEDHA MANTRA Competitive Institute is dedicated to providing high quality coaching and mentoring for WBCS, WBPSC, SSC, Railways, Banking, Police and competitive government recruitment examinations in Memari, Purba Bardhaman.",
    @Json(name = "features") val features: List<String> = listOf(
        "Expert Subject Specialist Faculty",
        "Updated & Exam-Oriented Study Material",
        "Weekly Speed & Mock Tests with Analysis",
        "Dedicated Doubt Clearing Sessions",
        "Air Conditioned Classrooms & Study Room"
    )
)
