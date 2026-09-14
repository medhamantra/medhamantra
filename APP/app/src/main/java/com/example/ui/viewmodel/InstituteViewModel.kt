package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.BannerItem
import com.example.data.model.Course
import com.example.data.model.Faculty
import com.example.data.model.GalleryItem
import com.example.data.model.InstituteSettings
import com.example.data.model.Notice
import com.example.data.model.ResultItem
import com.example.data.repository.InstituteRepository
import com.example.data.repository.RepoState
import com.example.data.repository.SearchResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class InstituteViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = InstituteRepository(application.applicationContext)

    val baseUrl: StateFlow<String> = repository.baseUrl
    val coursesState: StateFlow<RepoState<List<Course>>> = repository.coursesState
    val noticesState: StateFlow<RepoState<List<Notice>>> = repository.noticesState
    val facultyState: StateFlow<RepoState<List<Faculty>>> = repository.facultyState
    val resultsState: StateFlow<RepoState<List<ResultItem>>> = repository.resultsState
    val bannersState: StateFlow<RepoState<List<BannerItem>>> = repository.bannersState
    val galleryState: StateFlow<RepoState<List<GalleryItem>>> = repository.galleryState
    val settingsState: StateFlow<RepoState<InstituteSettings>> = repository.settingsState

    val isRefreshing: StateFlow<Boolean> = repository.isRefreshing
    val globalError: StateFlow<String?> = repository.globalError

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Combined search results flow re-computed whenever query or repository data updates
    val searchResults: StateFlow<SearchResult> = combine(
        _searchQuery,
        coursesState,
        noticesState,
        facultyState,
        resultsState
    ) { query, _, _, _, _ ->
        repository.search(query)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SearchResult()
    )

    init {
        viewModelScope.launch {
            repository.initialize()
        }
    }

    fun refresh() {
        viewModelScope.launch {
            repository.refreshAll()
        }
    }

    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun clearSearch() {
        _searchQuery.value = ""
    }

    fun updateBaseUrl(newUrl: String) {
        viewModelScope.launch {
            repository.setBaseUrl(newUrl)
        }
    }

    fun resetBaseUrl() {
        viewModelScope.launch {
            repository.resetBaseUrl()
        }
    }

    fun clearGlobalError() {
        repository.clearGlobalError()
    }

    companion object {
        fun formatTimestamp(timestamp: Long): String {
            if (timestamp <= 0L) return "Loaded locally"
            val diff = System.currentTimeMillis() - timestamp
            return when {
                diff < 60_000L -> "Just now"
                diff < 3600_000L -> "${diff / 60_000L}m ago"
                diff < 86400_000L -> "${diff / 3600_000L}h ago"
                else -> {
                    val sdf = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
                    sdf.format(Date(timestamp))
                }
            }
        }
    }
}
