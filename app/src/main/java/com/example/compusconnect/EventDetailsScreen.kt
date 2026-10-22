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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.compusconnect.ui.theme.CompusConnectTheme
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailsScreen(
    event: Event,
    userSession: UserSession = UserSession(),
    onBackClick: () -> Unit = {},
    onToggleSave: () -> Unit = {},
    onShareClick: () -> Unit = {},
    onRequireLogin: () -> Unit = {},
    onTicketPurchased: (List<String>, Int) -> Unit = { _, _ -> }
) {
    val isFree = !event.isPaid
    val repository = remember { CampusRepository() }
    val coroutineScope = rememberCoroutineScope()

    var showDialog by remember { mutableStateOf(false) }
    var quantity by remember { mutableStateOf(1) }
    var userAlreadyHeld by remember { mutableStateOf(0) }
    var isPurchasing by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(userSession.uid, event.id) {
        if (userSession.isLoggedIn && userSession.uid.isNotBlank()) {
            userAlreadyHeld = repository.fetchUserTicketCount(userSession.uid, event.id)
        }
    }

    val maxAllowed = (MAX_TICKETS_PER_EVENT - userAlreadyHeld).coerceAtLeast(0)

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { if (!isPurchasing) showDialog = false },
            containerColor = CampusCream,
            shape = RoundedCornerShape(24.dp),
            title = { Text("Confirm Ticket Purchase", fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, color = CampusNavy) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(text = event.title, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = CampusInk)
                    Text(text = "${event.time} · ${event.location}", fontFamily = FontFamily.SansSerif, fontSize = 13.sp, color = CampusGrey)

                    HorizontalDivider(color = CampusLightBeige)

                    if (maxAllowed == 0) {
                        Text(
                            text = "You have reached the limit of $MAX_TICKETS_PER_EVENT tickets for this event.",
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 13.sp,
                            color = Color(0xFFD32F2F),
                            fontWeight = FontWeight.SemiBold
                        )
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Quantity:", fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Medium, fontSize = 14.sp, color = CampusInk)
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                IconButton(
                                    onClick = { if (quantity > 1) quantity -= 1 },
                                    enabled = quantity > 1 && !isPurchasing
                                ) {
                                    Icon(Icons.Default.Remove, contentDescription = "Decrease")
                                }
                                Text(quantity.toString(), fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = CampusNavy)
                                IconButton(
                                    onClick = { if (quantity < maxAllowed) quantity += 1 },
                                    enabled = quantity < maxAllowed && !isPurchasing
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "Increase")
                                }
                            }
                        }

                        val unitPriceNum = event.price.filter { it.isDigit() || it == '.' }.toDoubleOrNull() ?: 0.0
                        val totalNum = unitPriceNum * quantity
                        val formattedTotal = "K${String.format(Locale.getDefault(), "%.2f", totalNum)}"

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total Price:", fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Medium, fontSize = 14.sp, color = CampusInk)
                            Text(formattedTotal, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = CampusNavy)
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Demo payment: no real money is charged",
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 11.sp,
                        fontStyle = FontStyle.Italic,
                        color = CampusGrey
                    )

                    if (errorMessage != null) {
                        Text(text = errorMessage!!, fontFamily = FontFamily.SansSerif, fontSize = 12.sp, color = Color(0xFFD32F2F))
                    }
                }
            },
            confirmButton = {
                if (maxAllowed > 0) {
                    Button(
                        onClick = {
                            isPurchasing = true
                            errorMessage = null
                            coroutineScope.launch {
                                val result = repository.purchaseTickets(
                                    uid = userSession.uid,
                                    event = event,
                                    quantity = quantity,
                                    buyerName = userSession.name,
                                    buyerEmail = userSession.email
                                )
                                isPurchasing = false
                                result.fold(
                                    onSuccess = { refCodes ->
                                        showDialog = false
                                        onTicketPurchased(refCodes, quantity)
                                    },
                                    onFailure = { err ->
                                        errorMessage = err.localizedMessage ?: "Purchase failed. Please try again."
                                    }
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CampusAmber),
                        shape = RoundedCornerShape(20.dp),
                        enabled = !isPurchasing
                    ) {
                        if (isPurchasing) {
                            CircularProgressIndicator(color = CampusInk, modifier = Modifier.size(18.dp))
                        } else {
                            Text("Confirm purchase", fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, color = CampusInk)
                        }
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { if (!isPurchasing) showDialog = false }, enabled = !isPurchasing) {
                    Text("Cancel", color = CampusGrey)
                }
            }
        )
    }

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
                            onClick = {
                                if (!userSession.isLoggedIn) {
                                    onRequireLogin()
                                } else {
                                    showDialog = true
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CampusAmber),
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier
                                .height(50.dp)
                                .width(180.dp)
                        ) {
                            Text(
                                text = if (maxAllowed == 0) "Limit Reached" else "Buy Ticket",
                                fontFamily = FontFamily.SansSerif,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
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
                        IconButton(
                            onClick = onToggleSave,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.3f))
                        ) {
                            Icon(
                                imageVector = if (event.isSaved) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Save",
                                tint = if (event.isSaved) CampusAmber else Color.White
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
            }

            // Event Details Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Category & Title
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = event.category.color
                    ) {
                        Text(
                            text = event.category.label,
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    Text(
                        text = event.title,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 26.sp,
                        color = CampusNavy
                    )
                }

                // Date & Time Box
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    shadowElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(CampusLightBeige),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = null,
                                tint = CampusNavy
                            )
                        }

                        Column {
                            Text(
                                text = "${event.month} ${event.day}",
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = CampusNavy
                            )
                            Text(
                                text = event.time,
                                fontFamily = FontFamily.SansSerif,
                                fontSize = 13.sp,
                                color = CampusGrey
                            )
                        }
                    }
                }

                // Location Box
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    shadowElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(CampusLightBeige),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Place,
                                contentDescription = null,
                                tint = CampusNavy
                            )
                        }

                        Column {
                            Text(
                                text = "Location / Venue",
                                fontFamily = FontFamily.SansSerif,
                                fontSize = 12.sp,
                                color = CampusGrey
                            )
                            Text(
                                text = event.location,
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = CampusNavy
                            )
                        }
                    }
                }

                // About this event
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "About this event",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = CampusNavy
                    )
                    Text(
                        text = event.description,
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 14.sp,
                        color = CampusInk,
                        lineHeight = 22.sp
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}
