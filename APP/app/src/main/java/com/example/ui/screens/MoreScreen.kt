package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.navigation.Screen
import com.example.ui.util.IntentHelpers
import com.example.ui.viewmodel.InstituteViewModel

@Composable
fun MoreScreen(
    viewModel: InstituteViewModel,
    onNavigate: (Screen) -> Unit,
    onOpenConfig: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val settingsState by viewModel.settingsState.collectAsState()
    val settings = settingsState.data

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        // Institute Identity Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_medha_logo),
                        contentDescription = "Institute Logo",
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color.Black)
                            .border(1.dp, Color(0xFFD4AF37).copy(alpha = 0.6f), CircleShape)
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = settings.instituteName,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 16.sp
                            )
                        )
                        Text(
                            text = settings.tagline,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.secondary,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        // Section Menu List
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    MoreMenuItem(
                        icon = Icons.Default.Groups,
                        title = "Faculty & Mentors",
                        subtitle = "Meet our experienced exam coaches",
                        iconColor = Color(0xFF16A34A),
                        onClick = { onNavigate(Screen.Faculty) }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)

                    MoreMenuItem(
                        icon = Icons.Default.Collections,
                        title = "Campus Gallery",
                        subtitle = "Classrooms, test halls, events & library",
                        iconColor = Color(0xFF9333EA),
                        onClick = { onNavigate(Screen.Gallery) }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)

                    MoreMenuItem(
                        icon = Icons.Default.Info,
                        title = "About Institute",
                        subtitle = "Mission, vision, pedagogy & facilities",
                        iconColor = Color(0xFF2563EB),
                        onClick = { onNavigate(Screen.About) }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)

                    MoreMenuItem(
                        icon = Icons.Default.ContactPhone,
                        title = "Contact & Campus Location",
                        subtitle = "Phone, WhatsApp, email, Memari address",
                        iconColor = Color(0xFFEA580C),
                        onClick = { onNavigate(Screen.Contact) }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)

                    MoreMenuItem(
                        icon = Icons.Default.Campaign,
                        title = "Direct Admission Inquiry",
                        subtitle = "Connect instantly on WhatsApp",
                        iconColor = Color(0xFF0D9488),
                        onClick = {
                            IntentHelpers.openWhatsApp(
                                context,
                                settings.whatsapp,
                                "Hello MEDHA MANTRA, I want admission enquiry."
                            )
                        }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)

                    MoreMenuItem(
                        icon = Icons.Default.Language,
                        title = "Official Website Portal",
                        subtitle = settings.website,
                        iconColor = Color(0xFF4F46E5),
                        onClick = { IntentHelpers.openBrowser(context, settings.website) }
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        // GitHub Data Source Settings Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    MoreMenuItem(
                        icon = Icons.Default.CloudSync,
                        title = "GitHub Remote Data Source",
                        subtitle = "Inspect or change dynamic JSON repository URL",
                        iconColor = MaterialTheme.colorScheme.primary,
                        onClick = onOpenConfig
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }

        // App Footer with version and credits
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "MEDHA MANTRA Mobile App v1.0.0",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.outline
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Powered by Jetpack Compose & GitHub Dynamic Content Architecture",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun MoreMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    iconColor: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(iconColor.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
            modifier = Modifier.size(14.dp),
            tint = MaterialTheme.colorScheme.outline
        )
    }
}
