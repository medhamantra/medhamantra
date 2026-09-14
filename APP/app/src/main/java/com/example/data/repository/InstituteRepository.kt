package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.config.AppConfig
import com.example.data.local.AppDatabase
import com.example.data.local.CachedDataEntity
import com.example.data.model.BannerItem
import com.example.data.model.Course
import com.example.data.model.Faculty
import com.example.data.model.GalleryItem
import com.example.data.model.InstituteSettings
import com.example.data.model.Notice
import com.example.data.model.ResultItem
import com.example.data.remote.JsonParser
import com.example.data.remote.NetworkClient
import com.example.util.UrlHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

data class SearchResult(
    val courses: List<Course> = emptyList(),
    val notices: List<Notice> = emptyList(),
    val faculty: List<Faculty> = emptyList(),
    val results: List<ResultItem> = emptyList()
) {
    val totalCount: Int get() = courses.size + notices.size + faculty.size + results.size
    val isEmpty: Boolean get() = totalCount == 0
}

data class RepoState<T>(
    val data: T,
    val isLoading: Boolean = false,
    val isFromCache: Boolean = true,
    val lastUpdated: Long = 0L,
    val errorMessage: String? = null
)

class InstituteRepository(private val context: Context) {

    private val db = AppDatabase.getInstance(context)
    private val cacheDao = db.cacheDao()

    private val prefs = context.getSharedPreferences("medha_mantra_prefs", Context.MODE_PRIVATE)
    private val KEY_BASE_URL = "custom_github_base_url"

    private val _baseUrl = MutableStateFlow(
        prefs.getString(KEY_BASE_URL, null)?.let { saved ->
            if (saved.isBlank() || saved.contains("medhamantra/institute-content")) {
                prefs.edit().putString(KEY_BASE_URL, AppConfig.DEFAULT_GITHUB_BASE_URL).apply()
                AppConfig.DEFAULT_GITHUB_BASE_URL
            } else {
                saved
            }
        } ?: AppConfig.DEFAULT_GITHUB_BASE_URL
    )
    val baseUrl: StateFlow<String> = _baseUrl.asStateFlow()

    // State flows for each resource (empty by default, loaded from Room cache then GitHub)
    private val _coursesState = MutableStateFlow(
        RepoState<List<Course>>(data = emptyList(), isLoading = true, isFromCache = true)
    )
    val coursesState: StateFlow<RepoState<List<Course>>> = _coursesState.asStateFlow()

    private val _noticesState = MutableStateFlow(
        RepoState<List<Notice>>(data = emptyList(), isLoading = true, isFromCache = true)
    )
    val noticesState: StateFlow<RepoState<List<Notice>>> = _noticesState.asStateFlow()

    private val _facultyState = MutableStateFlow(
        RepoState<List<Faculty>>(data = emptyList(), isLoading = true, isFromCache = true)
    )
    val facultyState: StateFlow<RepoState<List<Faculty>>> = _facultyState.asStateFlow()

    private val _resultsState = MutableStateFlow(
        RepoState<List<ResultItem>>(data = emptyList(), isLoading = true, isFromCache = true)
    )
    val resultsState: StateFlow<RepoState<List<ResultItem>>> = _resultsState.asStateFlow()

    private val _bannersState = MutableStateFlow(
        RepoState<List<BannerItem>>(data = emptyList(), isLoading = true, isFromCache = true)
    )
    val bannersState: StateFlow<RepoState<List<BannerItem>>> = _bannersState.asStateFlow()

    private val _galleryState = MutableStateFlow(
        RepoState<List<GalleryItem>>(data = emptyList(), isLoading = true, isFromCache = true)
    )
    val galleryState: StateFlow<RepoState<List<GalleryItem>>> = _galleryState.asStateFlow()

    private val _settingsState = MutableStateFlow(
        RepoState(data = InstituteSettings(), isLoading = true, isFromCache = true)
    )
    val settingsState: StateFlow<RepoState<InstituteSettings>> = _settingsState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _globalError = MutableStateFlow<String?>(null)
    val globalError: StateFlow<String?> = _globalError.asStateFlow()

    fun clearGlobalError() {
        _globalError.value = null
    }

    suspend fun setBaseUrl(newUrl: String) {
        val sanitized = newUrl.trim().let { if (it.endsWith("/")) it else "$it/" }
        prefs.edit().putString(KEY_BASE_URL, sanitized).apply()
        _baseUrl.value = sanitized
        refreshAll()
    }

    suspend fun resetBaseUrl() {
        prefs.edit().putString(KEY_BASE_URL, AppConfig.DEFAULT_GITHUB_BASE_URL).apply()
        _baseUrl.value = AppConfig.DEFAULT_GITHUB_BASE_URL
        refreshAll()
    }

    /**
     * Loads local Room cache immediately for fast launch, then initiates live network sync.
     */
    suspend fun initialize() = withContext(Dispatchers.IO) {
        val currentBase = _baseUrl.value
        loadFromCache("courses") { json, ts ->
            JsonParser.parseCourses(json, currentBase)?.let {
                _coursesState.value = RepoState(it, isLoading = false, isFromCache = true, lastUpdated = ts)
            }
        }
        loadFromCache("notices") { json, ts ->
            JsonParser.parseNotices(json, currentBase)?.let {
                _noticesState.value = RepoState(it, isLoading = false, isFromCache = true, lastUpdated = ts)
            }
        }
        loadFromCache("faculty") { json, ts ->
            JsonParser.parseFaculty(json, currentBase)?.let {
                _facultyState.value = RepoState(it, isLoading = false, isFromCache = true, lastUpdated = ts)
            }
        }
        loadFromCache("results") { json, ts ->
            JsonParser.parseResults(json, currentBase)?.let {
                _resultsState.value = RepoState(it, isLoading = false, isFromCache = true, lastUpdated = ts)
            }
        }
        loadFromCache("banners") { json, ts ->
            JsonParser.parseBanners(json, currentBase)?.let {
                _bannersState.value = RepoState(it, isLoading = false, isFromCache = true, lastUpdated = ts)
            }
        }
        loadFromCache("gallery") { json, ts ->
            JsonParser.parseGallery(json, currentBase)?.let {
                _galleryState.value = RepoState(it, isLoading = false, isFromCache = true, lastUpdated = ts)
            }
        }
        loadFromCache("settings") { json, ts ->
            JsonParser.parseSettings(json)?.let {
                _settingsState.value = RepoState(it, isLoading = false, isFromCache = true, lastUpdated = ts)
            }
        }

        // Trigger remote sync from GitHub
        refreshAll()
    }

    private suspend fun loadFromCache(key: String, onLoaded: (String, Long) -> Unit) {
        try {
            val cached = cacheDao.getCached(key)
            if (cached != null && cached.jsonData.isNotBlank()) {
                onLoaded(cached.jsonData, cached.lastUpdated)
            }
        } catch (e: Exception) {
            Log.e("InstituteRepository", "Error reading Room cache for $key: ${e.message}", e)
        }
    }

    /**
     * Fetches all 7 JSON files from GitHub concurrently.
     * Updates state flows and caches response in Room.
     */
    suspend fun refreshAll(): Boolean = withContext(Dispatchers.IO) {
        _isRefreshing.value = true
        _globalError.value = null

        val currentBase = _baseUrl.value
        val syncErrors = mutableListOf<String>()

        coroutineScope {
            val coursesJob = async {
                fetchAndCache(
                    key = "courses",
                    primaryPath = AppConfig.PATH_COURSES,
                    fallbackPath = null,
                    baseUrl = currentBase,
                    parser = { json -> JsonParser.parseCourses(json, currentBase) },
                    updateState = { list, ts ->
                        _coursesState.value = RepoState(list, isLoading = false, isFromCache = false, lastUpdated = ts)
                    },
                    currentState = _coursesState,
                    errorCollector = syncErrors
                )
            }

            val noticesJob = async {
                fetchAndCache(
                    key = "notices",
                    primaryPath = AppConfig.PATH_NOTICES,
                    fallbackPath = null,
                    baseUrl = currentBase,
                    parser = { json -> JsonParser.parseNotices(json, currentBase) },
                    updateState = { list, ts ->
                        _noticesState.value = RepoState(list, isLoading = false, isFromCache = false, lastUpdated = ts)
                    },
                    currentState = _noticesState,
                    errorCollector = syncErrors
                )
            }

            val facultyJob = async {
                fetchAndCache(
                    key = "faculty",
                    primaryPath = AppConfig.PATH_FACULTY,
                    fallbackPath = null,
                    baseUrl = currentBase,
                    parser = { json -> JsonParser.parseFaculty(json, currentBase) },
                    updateState = { list, ts ->
                        _facultyState.value = RepoState(list, isLoading = false, isFromCache = false, lastUpdated = ts)
                    },
                    currentState = _facultyState,
                    errorCollector = syncErrors
                )
            }

            val resultsJob = async {
                fetchAndCache(
                    key = "results",
                    primaryPath = AppConfig.PATH_RESULTS,
                    fallbackPath = null,
                    baseUrl = currentBase,
                    parser = { json -> JsonParser.parseResults(json, currentBase) },
                    updateState = { list, ts ->
                        _resultsState.value = RepoState(list, isLoading = false, isFromCache = false, lastUpdated = ts)
                    },
                    currentState = _resultsState,
                    errorCollector = syncErrors
                )
            }

            val bannersJob = async {
                fetchAndCache(
                    key = "banners",
                    primaryPath = AppConfig.PATH_BANNERS,
                    fallbackPath = AppConfig.FALLBACK_PATH_BANNERS,
                    baseUrl = currentBase,
                    parser = { json -> JsonParser.parseBanners(json, currentBase) },
                    updateState = { list, ts ->
                        _bannersState.value = RepoState(list, isLoading = false, isFromCache = false, lastUpdated = ts)
                    },
                    currentState = _bannersState,
                    errorCollector = syncErrors
                )
            }

            val galleryJob = async {
                fetchAndCache(
                    key = "gallery",
                    primaryPath = AppConfig.PATH_GALLERY,
                    fallbackPath = AppConfig.FALLBACK_PATH_GALLERY,
                    baseUrl = currentBase,
                    parser = { json -> JsonParser.parseGallery(json, currentBase) },
                    updateState = { list, ts ->
                        _galleryState.value = RepoState(list, isLoading = false, isFromCache = false, lastUpdated = ts)
                    },
                    currentState = _galleryState,
                    errorCollector = syncErrors
                )
            }

            val settingsJob = async {
                fetchAndCache(
                    key = "settings",
                    primaryPath = AppConfig.PATH_SETTINGS,
                    fallbackPath = AppConfig.FALLBACK_PATH_SETTINGS,
                    baseUrl = currentBase,
                    parser = { json -> JsonParser.parseSettings(json) },
                    updateState = { item, ts ->
                        _settingsState.value = RepoState(item, isLoading = false, isFromCache = false, lastUpdated = ts)
                    },
                    currentState = _settingsState,
                    errorCollector = syncErrors
                )
            }

            val results = listOf(
                coursesJob.await(),
                noticesJob.await(),
                facultyJob.await(),
                resultsJob.await(),
                bannersJob.await(),
                galleryJob.await(),
                settingsJob.await()
            )

            val anySuccess = results.any { it }
            val anyFailure = results.any { !it }

            _isRefreshing.value = false

            if (!anySuccess && syncErrors.isNotEmpty()) {
                _globalError.value = "Unable to connect to GitHub (${syncErrors.first()}). Showing cached content."
            } else if (anyFailure && syncErrors.isNotEmpty()) {
                Log.w("InstituteRepository", "Partial sync note: ${syncErrors.joinToString("; ")}")
            }

            anySuccess
        }
    }

    private suspend fun <T> fetchAndCache(
        key: String,
        primaryPath: String,
        fallbackPath: String?,
        baseUrl: String,
        parser: (String) -> T?,
        updateState: (T, Long) -> Unit,
        currentState: MutableStateFlow<RepoState<T>>,
        errorCollector: MutableList<String>
    ): Boolean {
        val primaryUrl = UrlHelper.buildEndpointUrl(baseUrl, primaryPath)
        var networkResult = NetworkClient.fetch(primaryUrl)

        // Fallback check if primary returns 404
        if (networkResult.statusCode == 404 && !fallbackPath.isNullOrBlank()) {
            val fallbackUrl = UrlHelper.buildEndpointUrl(baseUrl, fallbackPath)
            Log.d("InstituteRepository", "$primaryPath returned 404, attempting fallback path: $fallbackUrl")
            val fallbackResult = NetworkClient.fetch(fallbackUrl)
            if (fallbackResult.isSuccessful) {
                networkResult = fallbackResult
            }
        }

        if (networkResult.isSuccessful && !networkResult.body.isNullOrBlank()) {
            val json = networkResult.body
            val parsed = parser(json)
            if (parsed != null) {
                val now = System.currentTimeMillis()
                try {
                    cacheDao.insertOrUpdate(CachedDataEntity(key, json, now))
                } catch (e: Exception) {
                    Log.e("InstituteRepository", "Failed saving cache for $key: ${e.message}")
                }
                updateState(parsed, now)
                return true
            } else {
                val parseErr = "Failed to parse $key JSON"
                Log.e("InstituteRepository", parseErr)
                errorCollector.add(parseErr)
                currentState.value = currentState.value.copy(
                    isLoading = false,
                    errorMessage = parseErr
                )
                return false
            }
        } else {
            val errMsg = if (networkResult.statusCode > 0) {
                "HTTP ${networkResult.statusCode}: ${networkResult.errorMessage ?: "Failed"}"
            } else {
                networkResult.errorMessage ?: "Network error"
            }
            Log.e("InstituteRepository", "Error syncing $key: $errMsg")
            errorCollector.add("$key ($errMsg)")
            currentState.value = currentState.value.copy(
                isLoading = false,
                errorMessage = errMsg
            )
            return false
        }
    }

    /**
     * Performs instant global search across Courses, Notices, Faculty, and Results.
     */
    fun search(query: String): SearchResult {
        val q = query.trim().lowercase()
        if (q.isBlank()) return SearchResult()

        val courses = _coursesState.value.data.filter {
            it.title.lowercase().contains(q) ||
            it.category.lowercase().contains(q) ||
            it.description.lowercase().contains(q)
        }

        val notices = _noticesState.value.data.filter {
            it.title.lowercase().contains(q) ||
            it.description.lowercase().contains(q) ||
            it.category.lowercase().contains(q)
        }

        val faculty = _facultyState.value.data.filter {
            it.name.lowercase().contains(q) ||
            it.subject.lowercase().contains(q) ||
            it.qualification.lowercase().contains(q) ||
            it.bio.lowercase().contains(q)
        }

        val results = _resultsState.value.data.filter {
            it.studentName.lowercase().contains(q) ||
            it.exam.lowercase().contains(q) ||
            it.rankResult.lowercase().contains(q) ||
            it.year.lowercase().contains(q)
        }

        return SearchResult(courses, notices, faculty, results)
    }
}
