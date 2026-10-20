package com.example.compusconnect

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.compusconnect.ui.theme.CompusConnectTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEventScreen(
    onBackClick: () -> Unit = {},
    onPublishEvent: (Event) -> Unit = {}
) {
    var title by remember { mutableStateOf("") }
    var day by remember { mutableStateOf("25") }
    var month by remember { mutableStateOf("OCT") }
    var time by remember { mutableStateOf("7:00 PM") }
    var location by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(EventCategory.MUSIC) }
    var isPaid by remember { mutableStateOf(false) }
    var price by remember { mutableStateOf("K5000.00") }
    var posterUrlInput by remember { mutableStateOf("https://images.unsplash.com/photo-1514525253161-7a46d19cd819?auto=format&fit=crop&w=800&q=80") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Launcher to pick an image from phone gallery
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            posterUrlInput = uri.toString()
        }
    }

    Scaffold(
        containerColor = CampusCream,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Create Event",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        color = CampusCream
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = CampusCream
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CampusNavy)
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 16.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Button(
                        onClick = {
                            if (title.isBlank()) {
                                errorMessage = "Please enter an event title"
                                return@Button
                            }
                            if (location.isBlank()) {
                                errorMessage = "Please enter an event location"
                                return@Button
                            }
                            val newEvent = Event(
                                id = System.currentTimeMillis().toString(),
                                title = title.trim(),
                                day = day.trim().ifEmpty { "25" },
                                month = month.trim().uppercase().ifEmpty { "OCT" },
                                time = time.trim().ifEmpty { "7:00 PM" },
                                location = location.trim(),
                                description = description.ifBlank { "Describe what attendees can expect, schedule, special guests..." },
                                category = selectedCategory,
                                isPaid = isPaid,
                                price = if (isPaid) price.trim().ifEmpty { "K5000.00" } else "",
                                isSaved = false,
                                isPurchased = false,
                                posterUrl = posterUrlInput.trim().ifEmpty { "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?auto=format&fit=crop&w=800&q=80" }
                            )
                            onPublishEvent(newEvent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CampusAmber),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = CampusInk)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Publish Event",
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = CampusInk
                        )
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
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            if (errorMessage != null) {
                Surface(
                    color = Color(0xFFF8D7DA),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Error, contentDescription = null, tint = Color(0xFF721C24))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = errorMessage!!,
                            color = Color(0xFF721C24),
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            // 1. Poster Image Upload / URL Section
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Event Poster / Artwork",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = CampusNavy
                )
                Text(
                    text = "Upload from your phone gallery or provide an image URL",
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 13.sp,
                    color = CampusNavy.copy(alpha = 0.75f)
                )
                Spacer(modifier = Modifier.height(4.dp))

                // Preview Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(16.dp))
                ) {
                    EventPosterVisual(
                        posterUrl = posterUrlInput,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(16.dp)
                        ) {
                            Column {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = CampusAmber
                                ) {
                                    Text(
                                        text = selectedCategory.label,
                                        fontFamily = FontFamily.SansSerif,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        color = CampusInk,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (title.isBlank()) "Event Title Preview" else title,
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Upload from Phone Gallery Button
                Button(
                    onClick = { galleryLauncher.launch("image/*") },
                    colors = ButtonDefaults.buttonColors(containerColor = CampusNavy),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Icon(imageVector = Icons.Default.PhotoLibrary, contentDescription = null, tint = CampusCream)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Upload Poster from Phone Gallery",
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = CampusCream
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // URL Input Field
                OutlinedTextField(
                    value = posterUrlInput,
                    onValueChange = { posterUrlInput = it },
                    label = { Text("Or Paste Poster Image URL") },
                    placeholder = { Text("https://images.unsplash.com/...", color = CampusNavy.copy(alpha = 0.6f)) },
                    textStyle = LocalTextStyle.current.copy(color = CampusInk, fontWeight = FontWeight.Medium),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CampusNavy,
                        unfocusedBorderColor = CampusGrey.copy(alpha = 0.5f),
                        focusedLabelColor = CampusNavy,
                        focusedTextColor = CampusInk,
                        unfocusedTextColor = CampusInk,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Or choose professional preset artwork:",
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 12.sp,
                    color = CampusNavy.copy(alpha = 0.75f)
                )

                // Quick Presets
                val presetPosters = listOf(
                    "Concert" to "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?auto=format&fit=crop&w=800&q=80",
                    "Tech Fair" to "https://images.unsplash.com/photo-1540575467063-178a50c2df87?auto=format&fit=crop&w=800&q=80",
                    "Stadium" to "https://images.unsplash.com/photo-1508098682722-e99c43a406b2?auto=format&fit=crop&w=800&q=80",
                    "Arts" to "https://images.unsplash.com/photo-1460723237483-7a6dc9d0b212?auto=format&fit=crop&w=800&q=80"
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    presetPosters.forEach { (name, url) ->
                        val isSelected = posterUrlInput == url
                        Box(modifier = Modifier.weight(1f)) {
                            OutlinedButton(
                                onClick = { posterUrlInput = url },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(38.dp),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(2.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (isSelected) CampusNavy else Color.White,
                                    contentColor = if (isSelected) CampusCream else CampusNavy
                                )
                            ) {
                                Text(
                                    text = name,
                                    fontFamily = FontFamily.SansSerif,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }

            HorizontalDivider(color = CampusLightBeige)

            // 2. Event Title
            OutlinedTextField(
                value = title,
                onValueChange = { title = it; errorMessage = null },
                label = { Text("Event Title") },
                placeholder = { Text("e.g. Spring Music Fest", color = CampusNavy.copy(alpha = 0.6f)) },
                textStyle = LocalTextStyle.current.copy(color = CampusInk, fontWeight = FontWeight.Medium),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CampusNavy,
                    unfocusedBorderColor = CampusGrey.copy(alpha = 0.5f),
                    focusedLabelColor = CampusNavy,
                    focusedTextColor = CampusInk,
                    unfocusedTextColor = CampusInk,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            )

            // 3. Date & Time Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = day,
                    onValueChange = { day = it },
                    label = { Text("Day") },
                    placeholder = { Text("14", color = CampusNavy.copy(alpha = 0.6f)) },
                    textStyle = LocalTextStyle.current.copy(color = CampusInk, fontWeight = FontWeight.Medium),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CampusNavy,
                        unfocusedBorderColor = CampusGrey.copy(alpha = 0.5f),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedTextColor = CampusInk,
                        unfocusedTextColor = CampusInk
                    ),
                    modifier = Modifier.width(80.dp)
                )
                OutlinedTextField(
                    value = month,
                    onValueChange = { month = it },
                    label = { Text("Month") },
                    placeholder = { Text("OCT", color = CampusNavy.copy(alpha = 0.6f)) },
                    textStyle = LocalTextStyle.current.copy(color = CampusInk, fontWeight = FontWeight.Medium),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CampusNavy,
                        unfocusedBorderColor = CampusGrey.copy(alpha = 0.5f),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedTextColor = CampusInk,
                        unfocusedTextColor = CampusInk
                    ),
                    modifier = Modifier.width(96.dp)
                )
                OutlinedTextField(
                    value = time,
                    onValueChange = { time = it },
                    label = { Text("Time") },
                    placeholder = { Text("7:00 PM", color = CampusNavy.copy(alpha = 0.6f)) },
                    textStyle = LocalTextStyle.current.copy(color = CampusInk, fontWeight = FontWeight.Medium),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CampusNavy,
                        unfocusedBorderColor = CampusGrey.copy(alpha = 0.5f),
                        focusedLabelColor = CampusNavy,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedTextColor = CampusInk,
                        unfocusedTextColor = CampusInk
                    ),
                    modifier = Modifier.weight(1f)
                )
            }

            // 4. Location
            OutlinedTextField(
                value = location,
                onValueChange = { location = it; errorMessage = null },
                label = { Text("Location / Venue") },
                placeholder = { Text("e.g. Great Hall, Sports Complex", color = CampusNavy.copy(alpha = 0.6f)) },
                textStyle = LocalTextStyle.current.copy(color = CampusInk, fontWeight = FontWeight.Medium),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CampusNavy,
                    unfocusedBorderColor = CampusGrey.copy(alpha = 0.5f),
                    focusedLabelColor = CampusNavy,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedTextColor = CampusInk,
                    unfocusedTextColor = CampusInk
                ),
                modifier = Modifier.fillMaxWidth()
            )

            // 5. Category Selection
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Category",
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = CampusNavy
                )
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    EventCategory.entries.forEach { cat ->
                        val isSelected = selectedCategory == cat
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat.label) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CampusNavy,
                                selectedLabelColor = CampusCream,
                                containerColor = Color.White,
                                labelColor = CampusNavy
                            )
                        )
                    }
                }
            }

            // 6. Price Type & Amount
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Pricing",
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = CampusNavy
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { isPaid = false }
                    ) {
                        RadioButton(
                            selected = !isPaid,
                            onClick = { isPaid = false }
                        )
                        Text(
                            text = "Free Event",
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 14.sp,
                            color = CampusInk
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { isPaid = true }
                    ) {
                        RadioButton(
                            selected = isPaid,
                            onClick = { isPaid = true }
                        )
                        Text(
                            text = "Paid Ticket",
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 14.sp,
                            color = CampusInk
                        )
                    }
                }

                if (isPaid) {
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = price,
                        onValueChange = { price = it },
                        label = { Text("Ticket Price") },
                        placeholder = { Text("K5000.00", color = CampusNavy.copy(alpha = 0.6f)) },
                        textStyle = LocalTextStyle.current.copy(color = CampusInk, fontWeight = FontWeight.Medium),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CampusNavy,
                            unfocusedBorderColor = CampusGrey.copy(alpha = 0.5f),
                            focusedLabelColor = CampusNavy,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedTextColor = CampusInk,
                            unfocusedTextColor = CampusInk
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // 7. Description with clear placeholder
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description & Details") },
                placeholder = { Text("Describe what attendees can expect, schedule, special guests...", color = CampusNavy.copy(alpha = 0.6f)) },
                textStyle = LocalTextStyle.current.copy(color = CampusInk, fontWeight = FontWeight.Medium),
                minLines = 4,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CampusNavy,
                    unfocusedBorderColor = CampusGrey.copy(alpha = 0.5f),
                    focusedLabelColor = CampusNavy,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedTextColor = CampusInk,
                    unfocusedTextColor = CampusInk
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
