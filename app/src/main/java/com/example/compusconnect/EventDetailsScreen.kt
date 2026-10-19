package com.example.compusconnect

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.compusconnect.ui.theme.CompusConnectTheme

@Composable
fun EventDetailsScreen(
    event: Event,
    onBackClick: () -> Unit = {},
    onToggleSave: () -> Unit = {},
    onShareClick: () -> Unit = {},
    onBuyTicketClick: () -> Unit = {}
) {
    val isFree = !event.isPaid

    Scaffold(
        containerColor = CampusCream,
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 16.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isFree) "Admission" else "Student Price",
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 12.sp,
                            color = CampusGrey
                        )
                        Text(
                            text = if (isFree) "Free" else event.price,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            color = if (isFree) CampusSageText else CampusNavy
                        )
                    }

                    if (!isFree) {
                        Button(
                            onClick = onBuyTicketClick,
                            colors = ButtonDefaults.buttonColors(containerColor = CampusAmber),
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier
                                .height(50.dp)
                                .width(180.dp)
                        ) {
                            Text(
                                text = "Buy Ticket",
                                fontFamily = FontFamily.SansSerif,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = CampusInk
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // Image header banner with poster artwork & action buttons
            EventPosterVisual(
                posterUrl = event.posterUrl,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
            ) {
                // Top Action Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.3f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Bookmark / Heart Toggle Button
                        IconButton(
                            onClick = onToggleSave,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.3f))
                        ) {
                            Icon(
                                imageVector = if (event.isSaved) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Save event",
                                tint = if (event.isSaved) Color(0xFF6650a4) else Color.White
                            )
                        }

                        IconButton(
                            onClick = onShareClick,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.3f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share",
                                tint = Color.White
                            )
                        }
                    }
                }

                // Category Tag inside header banner bottom
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(20.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = CampusAmber
                    ) {
                        Text(
                            text = event.category.label,
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = CampusInk,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            letterSpacing = 1.sp
                        )
                    }
                }
            }

            // Content Body
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Title
                Text(
                    text = event.title,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp,
                    color = CampusNavy
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Info Rows
                InfoRow(icon = Icons.Default.CalendarToday, title = "${event.month} ${event.day} · ${event.time}")
                Spacer(modifier = Modifier.height(12.dp))
                InfoRow(icon = Icons.Default.LocationOn, title = event.location)
                Spacer(modifier = Modifier.height(12.dp))
                InfoRow(icon = Icons.Default.Person, title = "Hosted by Campus Organization")

                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider(color = CampusLightBeige, thickness = 1.dp)
                Spacer(modifier = Modifier.height(24.dp))

                // About this event
                Text(
                    text = "About this event",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = CampusNavy
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = event.description,
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 14.sp,
                    color = CampusGrey,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Tags Row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("All ages", "Student ID required", "Accessible").forEach { tag ->
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = CampusLightBeige
                        ) {
                            Text(
                                text = tag,
                                fontFamily = FontFamily.SansSerif,
                                fontSize = 12.sp,
                                color = CampusNavy,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun InfoRow(icon: ImageVector, title: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(CampusLightBeige),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = CampusNavy,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = title,
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            color = CampusInk
        )
    }
}

@Preview(showBackground = true, widthDp = 392, heightDp = 800)
@Composable
fun EventDetailsScreenPreview() {
    CompusConnectTheme {
        EventDetailsScreen(
            event = Event(
                id = "1",
                title = "Tech Careers Fair",
                day = "16",
                month = "OCT",
                time = "10:00 AM",
                location = "Student Center Hall B",
                description = "Connect with top tech employers.",
                category = EventCategory.CAREER,
                isPaid = false,
                price = "",
                isSaved = false,
                isPurchased = false
            )
        )
    }
}
