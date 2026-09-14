package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.Course
import com.example.data.model.Notice
import com.example.data.model.ResultItem
import com.example.ui.components.BannerSlider
import com.example.ui.components.ContactActionRow
import com.example.ui.components.CourseCard
import com.example.ui.components.NoticeCard
import com.example.ui.components.ResultCard
import com.example.ui.navigation.Screen
import com.example.ui.util.IntentHelpers
import com.example.ui.viewmodel.InstituteViewModel

@Composable
fun HomeScreen(
    viewModel: InstituteViewModel,
    onNavigate: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val bannersState by viewModel.bannersState.collectAsState()
    val coursesState by viewModel.coursesState.collectAsState()
    val noticesState by viewModel.noticesState.collectAsState()
    val resultsState by viewModel.resultsState.collectAsState()
    val settingsState by viewModel.settingsState.collectAsState()

    val settings = settingsState.data
    val courses = coursesState.data
    val notices = noticesState.data
    val results = resultsState.data

    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val globalError by viewModel.globalError.collectAsState()

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Progress bar when syncing from GitHub
        if (isRefreshing) {
            item {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp),
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }

        // 1. Institute Hero Header (Logo, Name, Tagline)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_medha_logo),
                        contentDescription = "Medha Mantra Institute Logo",
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Black)
                            .border(1.dp, Color(0xFFD4AF37).copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    )

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(bottom = 3.dp)
                        ) {
                            Text(
                                text = "MEMARI'S PREMIER COACHING",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                ),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Text(
                            text = settings.instituteName,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                color = Color.White
                            )
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = settings.tagline,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        )
                    }
                }
            }
        }

        // Offline / Error banner with functional Retry button (Requirements 3, 4, 5)
        if (globalError != null) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudOff,
                            contentDescription = "Sync Error",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = globalError ?: "Sync error",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.sp
                                ),
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Text(
                                text = "Showing cached content. Tap Retry to reconnect to GitHub.",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.85f)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = { viewModel.refresh() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Retry",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }
        }

        // 2. Banner Slider (Requirement 4: Auto slide every 5s, swipeable, remote URLs)
        item {
            BannerSlider(
                banners = bannersState.data,
                onBannerClick = { banner ->
                    when (banner.actionUrl.lowercase()) {
                        "courses" -> onNavigate(Screen.Courses)
                        "notices" -> onNavigate(Screen.Notices)
                        "results" -> onNavigate(Screen.Results)
                        "contact" -> onNavigate(Screen.Contact)
                        else -> {
                            if (banner.actionUrl.startsWith("http")) {
                                IntentHelpers.openBrowser(context, banner.actionUrl)
                            } else {
                                onNavigate(Screen.Courses)
                            }
                        }
                    }
                }
            )
        }

        // 3. Quick Action Grid / Links (Courses, Notices, Faculty, Results, Gallery, Contact, About, Admissions)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Quick Access",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QuickLinkCard(
                        icon = Icons.AutoMirrored.Filled.MenuBook,
                        title = "Courses",
                        subtitle = "${courses.size} Active",
                        bgColor = Color(0xFFEFF6FF),
                        iconTint = Color(0xFF1D4ED8),
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(Screen.Courses) }
                    )
                    QuickLinkCard(
                        icon = Icons.Default.Notifications,
                        title = "Notices",
                        subtitle = "${notices.size} Updates",
                        bgColor = Color(0xFFFEF2F2),
                        iconTint = Color(0xFFDC2626),
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(Screen.Notices) }
                    )
                    QuickLinkCard(
                        icon = Icons.Default.EmojiEvents,
                        title = "Results",
                        subtitle = "Toppers",
                        bgColor = Color(0xFFFFFBEB),
                        iconTint = Color(0xFFD97706),
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(Screen.Results) }
                    )
                    QuickLinkCard(
                        icon = Icons.Default.Groups,
                        title = "Faculty",
                        subtitle = "Mentors",
                        bgColor = Color(0xFFF0FDF4),
                        iconTint = Color(0xFF16A34A),
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(Screen.Faculty) }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QuickLinkCard(
                        icon = Icons.Default.Collections,
                        title = "Gallery",
                        subtitle = "Campus",
                        bgColor = Color(0xFFFAF5FF),
                        iconTint = Color(0xFF9333EA),
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(Screen.Gallery) }
                    )
                    QuickLinkCard(
                        icon = Icons.Default.Info,
                        title = "About Us",
                        subtitle = "Legacy",
                        bgColor = Color(0xFFF8FAFC),
                        iconTint = Color(0xFF475569),
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(Screen.About) }
                    )
                    QuickLinkCard(
                        icon = Icons.Default.ContactPhone,
                        title = "Contact",
                        subtitle = "Visit Us",
                        bgColor = Color(0xFFF0FDF4),
                        iconTint = Color(0xFF0D9488),
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(Screen.Contact) }
                    )
                    QuickLinkCard(
                        icon = Icons.Default.Campaign,
                        title = "Admission",
                        subtitle = "Enquire",
                        bgColor = Color(0xFFFFF7ED),
                        iconTint = Color(0xFFEA580C),
                        modifier = Modifier.weight(1f),
                        onClick = {
                            IntentHelpers.openWhatsApp(
                                context,
                                settings.whatsapp,
                                "Hello MEDHA MANTRA, I am interested in admission for competitive exam coaching."
                            )
                        }
                    )
                }
            }
        }

        // 4. Important Announcements Ticker
        val importantNotices = notices.filter { it.isImportant }.ifEmpty { notices.take(2) }
        if (importantNotices.isNotEmpty()) {
            item {
                Column(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Campaign,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Important Announcements",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                        TextButton(onClick = { onNavigate(Screen.Notices) }) {
                            Text("View All")
                        }
                    }

                    importantNotices.take(2).forEach { notice ->
                        NoticeCard(
                            notice = notice,
                            onViewDocument = { IntentHelpers.viewPdfDocument(context, it.documentUrl, it.title) },
                            onDownloadDocument = { IntentHelpers.downloadPdfDocument(context, it.documentUrl, it.title) }
                        )
                    }
                }
            }
        }

        // 5. Direct Connect Buttons (Call, WhatsApp, Email, Maps, Web)
        item {
            ContactActionRow(settings = settings)
        }

        // 6. Institute Key Strengths & Stats
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Why MEDHA MANTRA?",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StatPill("100+", "Selections", Color(0xFF1E3A8A))
                        StatPill("10+", "Mentors", Color(0xFFB45309))
                        StatPill("100%", "OMR Tests", Color(0xFF047857))
                        StatPill("Memari", "Campus", Color(0xFF6B21A8))
                    }
                }
            }
        }

        // 7. Popular Courses Preview
        if (courses.isNotEmpty()) {
            item {
                Column(modifier = Modifier.fillMaxWidth().padding(top = 10.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Featured Courses",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        TextButton(onClick = { onNavigate(Screen.Courses) }) {
                            Text("See All (${courses.size})")
                        }
                    }

                    courses.take(2).forEach { course ->
                        CourseCard(
                            course = course,
                            onInquireClick = {
                                IntentHelpers.openWhatsApp(
                                    context,
                                    settings.whatsapp,
                                    "Hello MEDHA MANTRA, I want inquiry about course: ${it.title}."
                                )
                            }
                        )
                    }
                }
            }
        }

        // 8. Top Achievers / Results Section
        if (results.isNotEmpty()) {
            item {
                Column(modifier = Modifier.fillMaxWidth().padding(top = 10.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Recent Selections & Ranks",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        TextButton(onClick = { onNavigate(Screen.Results) }) {
                            Text("View Hall of Fame")
                        }
                    }

                    results.take(2).forEach { res ->
                        ResultCard(result = res)
                    }
                }
            }
        }

        // 9. Admission Call To Action Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Take The First Step Towards Your Dream Job",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        ),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Join our upcoming foundation batches with individual mentoring at Memari campus.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.85f),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            IntentHelpers.openWhatsApp(
                                context,
                                settings.whatsapp,
                                "Hello, I want to book a free demo class at MEDHA MANTRA."
                            )
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Book Free Demo Class")
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickLinkCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    bgColor: Color,
    iconTint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.9f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                ),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.outline
                ),
                maxLines = 1
            )
        }
    }
}

@Composable
private fun StatPill(value: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp
            ),
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
