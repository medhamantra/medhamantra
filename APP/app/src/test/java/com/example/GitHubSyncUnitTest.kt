package com.example

import com.example.config.AppConfig
import com.example.data.remote.JsonParser
import com.example.util.UrlHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class GitHubSyncUnitTest {

    private val baseUrl = "https://raw.githubusercontent.com/kingkingng2222-prog/margepdflove/main/"

    @Test
    fun testResolveRelativeAssetUrl() {
        val rawPath = "./assets/courses/ssc-cgl.jpg"
        val expected = "https://raw.githubusercontent.com/kingkingng2222-prog/margepdflove/main/assets/courses/ssc-cgl.jpg"
        val actual = UrlHelper.resolveUrl(baseUrl, rawPath)
        assertEquals(expected, actual)
    }

    @Test
    fun testResolveRelativeAssetUrlWithoutDot() {
        val rawPath = "assets/faculty/amitava-sarkar.jpg"
        val expected = "https://raw.githubusercontent.com/kingkingng2222-prog/margepdflove/main/assets/faculty/amitava-sarkar.jpg"
        val actual = UrlHelper.resolveUrl(baseUrl, rawPath)
        assertEquals(expected, actual)
    }

    @Test
    fun testResolveAbsoluteUrlPreserved() {
        val rawPath = "https://images.unsplash.com/photo-123"
        val actual = UrlHelper.resolveUrl(baseUrl, rawPath)
        assertEquals(rawPath, actual)
    }

    @Test
    fun testBuildEndpointUrlNoDoubleSlash() {
        val actual = UrlHelper.buildEndpointUrl(baseUrl, "data/courses.json")
        assertEquals("https://raw.githubusercontent.com/kingkingng2222-prog/margepdflove/main/data/courses.json", actual)

        val actualWithLeadingSlash = UrlHelper.buildEndpointUrl(baseUrl, "/data/courses.json")
        assertEquals("https://raw.githubusercontent.com/kingkingng2222-prog/margepdflove/main/data/courses.json", actualWithLeadingSlash)
    }

    @Test
    fun testParseCoursesWithRelativePosterAndIntId() {
        val json = """
        {
          "courses": [
            {
              "id": 1,
              "title": "SSC CGL",
              "category": "SSC",
              "description": "Complete SSC CGL preparation course.",
              "duration": "12 Months",
              "mode": "Offline + Online",
              "fee": "₹15,000",
              "poster": "./assets/courses/ssc-cgl.jpg",
              "subjects": [
                "Mathematics",
                "Reasoning",
                "English"
              ],
              "batch": "Morning & Evening",
              "admission": "Admission Open"
            }
          ]
        }
        """.trimIndent()

        val courses = JsonParser.parseCourses(json, baseUrl)
        assertNotNull(courses)
        assertEquals(1, courses!!.size)
        val course = courses[0]
        assertEquals("1", course.id)
        assertEquals("SSC CGL", course.title)
        assertEquals("https://raw.githubusercontent.com/kingkingng2222-prog/margepdflove/main/assets/courses/ssc-cgl.jpg", course.poster)
        assertEquals("Admission Open", course.admissionInfo)
        assertEquals(3, course.highlights.size)
    }

    @Test
    fun testParseNoticesWithRelativeFile() {
        val json = """
        {
          "notices": [
            {
              "id": 1,
              "title": "SSC CGL 2025 New Batch Admission Notice",
              "date": "2025-07-10",
              "file": "./assets/notices/ssc-cgl-new-batch.pdf",
              "type": "PDF"
            }
          ]
        }
        """.trimIndent()

        val notices = JsonParser.parseNotices(json, baseUrl)
        assertNotNull(notices)
        assertEquals(1, notices!!.size)
        val notice = notices[0]
        assertEquals("https://raw.githubusercontent.com/kingkingng2222-prog/margepdflove/main/assets/notices/ssc-cgl-new-batch.pdf", notice.documentUrl)
        assertTrue(notice.isImportant)
    }

    @Test
    fun testParseFacultyWithRelativePhoto() {
        val json = """
        {
          "faculty": [
            {
              "name": "Amitava Sarkar",
              "subject": "Reasoning & Aptitude",
              "qualification": "M.Tech, MBA",
              "experience": "9 Years",
              "photo": "./assets/faculty/amitava-sarkar.jpg"
            }
          ]
        }
        """.trimIndent()

        val faculty = JsonParser.parseFaculty(json, baseUrl)
        assertNotNull(faculty)
        assertEquals(1, faculty!!.size)
        val member = faculty[0]
        assertEquals("Amitava Sarkar", member.name)
        assertEquals("https://raw.githubusercontent.com/kingkingng2222-prog/margepdflove/main/assets/faculty/amitava-sarkar.jpg", member.photo)
    }
}
