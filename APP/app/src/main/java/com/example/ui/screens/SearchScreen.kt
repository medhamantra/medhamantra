package com.example.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CourseCard
import com.example.ui.components.FacultyCard
import com.example.ui.components.NoticeCard
import com.example.ui.components.ResultCard
import com.example.ui.util.IntentHelpers
import com.example.ui.viewmodel.InstituteViewModel

@Composable
fun SearchScreen(
    viewModel: InstituteViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val query by viewModel.searchQuery.collectAsState()
    val searchResult by viewModel.searchResults.collectAsState()
    val settingsState by viewModel.settingsState.collectAsState()
    val settings = settingsState.data

    Column(modifier = modifier.fillMaxSize()) {
        // Search Header Bar
        Surface(
            tonalElevation = 2.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }

                OutlinedTextField(
                    value = query,
                    onValueChange = { viewModel.onSearchQueryChanged(it) },
                    placeholder = { Text("Search courses, notices, faculty, results...") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    trailingIcon = {
                        if (query.isNotBlank()) {
                            IconButton(onClick = { viewModel.clearSearch() }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear search"
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                )
            }
        }

        if (query.isBlank()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        modifier = Modifier.size(56.dp),
                        tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Global Institute Search",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Search across courses (WBCS, SSC, Rail), notices, faculty members, and student exam results.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        } else if (searchResult.isEmpty) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No matches found for \"$query\".",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 12.dp, horizontal = 0.dp)
            ) {
                item {
                    Text(
                        text = "Found ${searchResult.totalCount} results for \"$query\"",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }

                // Courses matches
                if (searchResult.courses.isNotEmpty()) {
                    item {
                        SearchSectionHeader(title = "Courses (${searchResult.courses.size})")
                    }
                    items(searchResult.courses, key = { "c_${it.id}" }) { course ->
                        CourseCard(
                            course = course,
                            onInquireClick = {
                                IntentHelpers.openWhatsApp(
                                    context,
                                    settings.whatsapp,
                                    "Hello, I found this course in search: ${course.title}."
                                )
                            }
                        )
                    }
                }

                // Notices matches
                if (searchResult.notices.isNotEmpty()) {
                    item {
                        SearchSectionHeader(title = "Notices & Circulars (${searchResult.notices.size})")
                    }
                    items(searchResult.notices, key = { "n_${it.id}" }) { notice ->
                        NoticeCard(
                            notice = notice,
                            onViewDocument = { IntentHelpers.viewPdfDocument(context, it.documentUrl, it.title) },
                            onDownloadDocument = { IntentHelpers.downloadPdfDocument(context, it.documentUrl, it.title) }
                        )
                    }
                }

                // Faculty matches
                if (searchResult.faculty.isNotEmpty()) {
                    item {
                        SearchSectionHeader(title = "Faculty & Mentors (${searchResult.faculty.size})")
                    }
                    items(searchResult.faculty, key = { "f_${it.id}" }) { faculty ->
                        FacultyCard(faculty = faculty)
                    }
                }

                // Results matches
                if (searchResult.results.isNotEmpty()) {
                    item {
                        SearchSectionHeader(title = "Results & Rankers (${searchResult.results.size})")
                    }
                    items(searchResult.results, key = { "r_${it.id}" }) { res ->
                        ResultCard(result = res)
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        ),
        color = MaterialTheme.colorScheme.secondary,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}
