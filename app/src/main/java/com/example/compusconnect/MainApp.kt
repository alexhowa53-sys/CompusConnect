package com.example.compusconnect

import androidx.compose.runtime.*

sealed class Screen {
    object Splash : Screen()
    object Login : Screen()
    object MainStudent : Screen()
    object EventDetails : Screen()
    object TicketConfirmation : Screen()
    object AdminDashboard : Screen()
}

@Composable
fun MainApp() {
    var currentRoute by remember { mutableStateOf<Screen>(Screen.Splash) }
    var studentTab by remember { mutableStateOf(0) } // 0: Home, 1: Search, 2: Tickets, 3: Profile
    var selectedEventTitle by remember { mutableStateOf("Indie Night on the Quad") }
    var selectedEventCategory by remember { mutableStateOf(EventCategory.MUSIC) }
    var selectedEventPrice by remember { mutableStateOf("$12") }

    when (currentRoute) {
        is Screen.Splash -> {
            SplashScreen(
                onSplashFinished = {
                    currentRoute = Screen.MainStudent
                }
            )
        }
        is Screen.Login -> {
            LoginAuthScreen(
                onLoginSuccess = { isAdmin ->
                    if (isAdmin) {
                        currentRoute = Screen.AdminDashboard
                    } else {
                        currentRoute = Screen.MainStudent
                        studentTab = 0
                    }
                }
            )
        }
        is Screen.MainStudent -> {
            when (studentTab) {
                0 -> HomeScreen(
                    currentScreen = studentTab,
                    onNavigate = { studentTab = it },
                    onEventClick = { event ->
                        selectedEventTitle = event.title
                        selectedEventCategory = event.category
                        selectedEventPrice = if (event.isPaid) event.price else "Free"
                        currentRoute = Screen.EventDetails
                    }
                )
                1 -> SearchScreen(
                    currentScreen = studentTab,
                    onNavigate = { studentTab = it },
                    onEventClick = { event ->
                        selectedEventTitle = event.title
                        selectedEventCategory = event.category
                        selectedEventPrice = if (event.isPaid) event.price else "Free"
                        currentRoute = Screen.EventDetails
                    }
                )
                2 -> TicketsScreen(
                    currentScreen = studentTab,
                    onNavigate = { studentTab = it }
                )
                3 -> ProfileScreen(
                    currentScreen = studentTab,
                    onNavigate = { studentTab = it },
                    onSwitchToAdminView = {
                        currentRoute = Screen.AdminDashboard
                    },
                    onLogout = {
                        currentRoute = Screen.Login
                    }
                )
            }
        }
        is Screen.EventDetails -> {
            EventDetailsScreen(
                eventTitle = selectedEventTitle,
                category = selectedEventCategory,
                priceText = selectedEventPrice,
                onBackClick = { currentRoute = Screen.MainStudent },
                onBuyTicketClick = {
                    currentRoute = Screen.TicketConfirmation
                }
            )
        }
        is Screen.TicketConfirmation -> {
            TicketConfirmationScreen(
                eventTitle = selectedEventTitle,
                onViewMyTicketsClick = {
                    currentRoute = Screen.MainStudent
                    studentTab = 2 // Tickets tab
                },
                onBackToHomeClick = {
                    currentRoute = Screen.MainStudent
                    studentTab = 0 // Home tab
                }
            )
        }
        is Screen.AdminDashboard -> {
            AdminDashboardScreen(
                onCreateEventClick = {
                    // Create event action
                },
                onSwitchToStudentView = {
                    currentRoute = Screen.MainStudent
                    studentTab = 0
                }
            )
        }
    }
}
