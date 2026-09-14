package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.util.IntentHelpers
import com.example.ui.viewmodel.InstituteViewModel

@Composable
fun ContactScreen(
    viewModel: InstituteViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val settingsState by viewModel.settingsState.collectAsState()
    val settings = settingsState.data

    var studentName by remember { mutableStateOf("") }
    var studentPhone by remember { mutableStateOf("") }
    var targetExam by remember { mutableStateOf("") }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Top Card: Address & Working Hours
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "MEDHA MANTRA CAMPUS",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    // Address
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = settings.address,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.95f),
                            lineHeight = 20.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Operating Hours
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Class & Desk Hours: Mon - Sun, 8:00 AM - 7:30 PM",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
            }
        }

        // Direct Contact Methods (Requirement 18: Call, WhatsApp, Email, Website, Google Maps)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "Direct Touchpoints",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                ContactDetailCard(
                    icon = Icons.Default.Phone,
                    title = "Call Helpline",
                    value = settings.phone,
                    buttonLabel = "Call Now",
                    iconColor = Color(0xFF2563EB),
                    onClick = { IntentHelpers.openDialer(context, settings.phone) }
                )

                Spacer(modifier = Modifier.height(8.dp))

                ContactDetailCard(
                    icon = Icons.AutoMirrored.Filled.Send,
                    title = "WhatsApp Desk",
                    value = settings.whatsapp,
                    buttonLabel = "Chat on WhatsApp",
                    iconColor = Color(0xFF16A34A),
                    onClick = {
                        IntentHelpers.openWhatsApp(
                            context,
                            settings.whatsapp,
                            "Hello MEDHA MANTRA, I am contacting from the institute app for admission inquiry."
                        )
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                ContactDetailCard(
                    icon = Icons.Default.Email,
                    title = "Email Support",
                    value = settings.email,
                    buttonLabel = "Send Email",
                    iconColor = Color(0xFFDC2626),
                    onClick = { IntentHelpers.openEmail(context, settings.email) }
                )

                Spacer(modifier = Modifier.height(8.dp))

                ContactDetailCard(
                    icon = Icons.Default.Language,
                    title = "Official Portal",
                    value = settings.website,
                    buttonLabel = "Open Website",
                    iconColor = Color(0xFF7C3AED),
                    onClick = { IntentHelpers.openBrowser(context, settings.website) }
                )

                Spacer(modifier = Modifier.height(8.dp))

                ContactDetailCard(
                    icon = Icons.Default.LocationOn,
                    title = "Directions via Google Maps",
                    value = "Malancha Complex, Chakdighi Road, Memari",
                    buttonLabel = "View on Maps",
                    iconColor = Color(0xFFEA580C),
                    onClick = { IntentHelpers.openGoogleMaps(context, settings.address) }
                )
            }
        }

        // Quick Admission Inquiry Form
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Instant Admission Inquiry",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    Text(
                        text = "Submit your details and our admission counselor will contact you immediately.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = studentName,
                        onValueChange = { studentName = it },
                        label = { Text("Your Full Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = studentPhone,
                        onValueChange = { studentPhone = it },
                        label = { Text("Mobile / WhatsApp Number") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = targetExam,
                        onValueChange = { targetExam = it },
                        label = { Text("Target Exam (e.g. WBCS, SSC, Police, Rail)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            val msg = "Hello MEDHA MANTRA,\n" +
                                    "New Admission Inquiry:\n" +
                                    "Name: ${studentName.ifBlank { "Aspirant" }}\n" +
                                    "Phone: ${studentPhone.ifBlank { "Not provided" }}\n" +
                                    "Target Exam: ${targetExam.ifBlank { "General Inquiry" }}"
                            IntentHelpers.openWhatsApp(context, settings.whatsapp, msg)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Send Inquiry via WhatsApp")
                    }
                }
            }
        }
    }
}

@Composable
private fun ContactDetailCard(
    icon: ImageVector,
    title: String,
    value: String,
    buttonLabel: String,
    iconColor: Color,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            OutlinedButton(
                onClick = onClick,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = buttonLabel,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp)
                )
            }
        }
    }
}
