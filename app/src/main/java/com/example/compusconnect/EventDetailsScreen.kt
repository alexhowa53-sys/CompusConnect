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
    eventTitle: String = "Indie Night on the Quad",
    category: EventCategory = EventCategory.MUSIC,
    dateText: String = "Thursday, Oct 14 · 7:00 PM",
    locationText: String = "Great Hall Lawn",
    hostText: String = "Hosted by Campus Activities Board",
    priceText: String = "K6000",
    descriptionText: String = "Join us for an unforgettable evening under the stars featuring student indie bands, food trucks, and art installations. Bring your student ID and a blanket to sit on the lawn!",
    tags: List<String> = listOf("All ages", "Outdoors", "Accessible"),
    onBackClick: () -> Unit = {},
    onShareClick: () -> Unit = {},
    onBuyTicketClick: () -> Unit = {}
) {
    val isFree = priceText.equals("Free", ignoreCase = true)

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
                    horizontalArrangement = if (isFree) Arrangement.Center else Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        horizontalAlignment = if (isFree) Alignment.CenterHorizontally else Alignment.Start
                    ) {
                        Text(
                            text = if (isFree) "Admission" else "Student Price",
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 12.sp,
                            color = CampusGrey
                        )
                        Text(
                            text = priceText,
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
            // Image header banner with gradient & action buttons
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(category.color, CampusNavy)
                        )
                    )
            ) {
                // Top Action Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
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
                            text = category.label,
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
                    text = eventTitle,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp,
                    color = CampusNavy
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Info Rows
                InfoRow(icon = Icons.Default.CalendarToday, title = dateText)
                Spacer(modifier = Modifier.height(12.dp))
                InfoRow(icon = Icons.Default.LocationOn, title = locationText)
                Spacer(modifier = Modifier.height(12.dp))
                InfoRow(icon = Icons.Default.Person, title = hostText)

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
                    text = descriptionText,
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
                    tags.forEach { tag ->
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
        EventDetailsScreen()
    }
}
