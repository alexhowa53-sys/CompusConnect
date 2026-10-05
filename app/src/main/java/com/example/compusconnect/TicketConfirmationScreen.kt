package com.example.compusconnect

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.compusconnect.ui.theme.CompusConnectTheme

@Composable
fun TicketConfirmationScreen(
    event: Event,
    ticketRef: String = "CC-2025-8849",
    holderName: String = "Alex Lee",
    onViewMyTicketsClick: () -> Unit = {},
    onBackToHomeClick: () -> Unit = {}
) {
    Scaffold(
        containerColor = CampusCream
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Dark navy top header card / container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                    .background(CampusNavy)
                    .padding(vertical = 36.dp, horizontal = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(CampusAmber),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Success",
                            tint = CampusNavy,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "You're on the list!",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 26.sp,
                        color = CampusCream
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Your ticket has been successfully booked.",
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 14.sp,
                        color = CampusSubtitleBlue
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Ticket stub card details
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .shadow(6.dp, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                color = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = event.title,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = CampusNavy,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "${event.month} ${event.day}, 2025 · ${event.time}",
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 14.sp,
                        color = CampusAmber,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = event.location,
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 13.sp,
                        color = CampusGrey
                    )

                    Spacer(modifier = Modifier.height(20.dp))
                    HorizontalDivider(color = CampusLightBeige, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "TICKET HOLDER", fontSize = 10.sp, color = CampusGrey, fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(text = holderName, fontSize = 14.sp, color = CampusInk, fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "REFERENCE", fontSize = 10.sp, color = CampusGrey, fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(text = ticketRef, fontSize = 14.sp, color = CampusInk, fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // QR / Barcode stub representation
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(CampusLightBeige),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.QrCode,
                                contentDescription = "QR Stub",
                                tint = CampusNavy,
                                modifier = Modifier.size(54.dp)
                            )
                            Column {
                                Text(
                                    text = "Scan at entrance",
                                    fontFamily = FontFamily.SansSerif,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = CampusNavy
                                )
                                Text(
                                    text = "Tap for full screen pass",
                                    fontFamily = FontFamily.SansSerif,
                                    fontSize = 11.sp,
                                    color = CampusGrey
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Action Buttons
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onViewMyTicketsClick,
                    colors = ButtonDefaults.buttonColors(containerColor = CampusNavy),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(
                        text = "View My Tickets",
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = CampusCream
                    )
                }

                OutlinedButton(
                    onClick = onBackToHomeClick,
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CampusNavy)
                ) {
                    Text(
                        text = "Back to Home",
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = CampusNavy
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Preview(showBackground = true, widthDp = 392, heightDp = 800)
@Composable
fun TicketConfirmationScreenPreview() {
    CompusConnectTheme {
        TicketConfirmationScreen(
            event = Event(
                id = "1",
                title = "Welcoming Function",
                day = "14",
                month = "OCT",
                time = "7:00 PM",
                location = "Great Hall",
                description = "Join us for the biggest night.",
                category = EventCategory.MUSIC,
                isPaid = true,
                price = "K5000.00",
                isSaved = true,
                isPurchased = true
            )
        )
    }
}
