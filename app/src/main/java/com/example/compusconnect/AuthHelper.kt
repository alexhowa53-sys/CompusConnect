package com.example.compusconnect

enum class AuthStep {
    FORM,
    OTP
}

/**
 * Pure input validation function for login credentials format and length.
 * Validates email format using regex and ensures password is at least 6 characters.
 */
fun validateLoginInput(email: String, password: String): Boolean {
    val trimmedEmail = email.trim()
    if (trimmedEmail.isBlank() || password.isBlank()) return false
    val emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$".toRegex()
    val isEmailValid = emailRegex.matches(trimmedEmail)
    val isPasswordValid = password.length >= 6
    return isEmailValid && isPasswordValid
}
