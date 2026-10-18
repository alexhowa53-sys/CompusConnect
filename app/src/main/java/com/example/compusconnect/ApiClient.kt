
package com.example.compusconnect

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

object ApiClient {

    suspend fun getEvents(): JSONObject = withContext(Dispatchers.IO) {

        val url = URL("${BASE_URL}events.php")
        val connection = url.openConnection() as HttpURLConnection

        try {
            connection.requestMethod = "GET"
            connection.connectTimeout = 10000
            connection.readTimeout = 10000

            val responseStream =
                if (connection.responseCode in 200..299) {
                    connection.inputStream
                } else {
                    connection.errorStream
                }

            val response = responseStream.bufferedReader().use {
                it.readText()
            }

            JSONObject(response)

        } finally {
            connection.disconnect()
        }
    }


    private const val BASE_URL =
        "http://127.0.0.1:8080/campusconnect_api/"

    // LOGIN
    suspend fun login(
        email: String,
        password: String
    ): JSONObject = withContext(Dispatchers.IO) {

        val url = URL("${BASE_URL}login.php")
        val connection = url.openConnection() as HttpURLConnection

        try {
            connection.requestMethod = "POST"
            connection.setRequestProperty(
                "Content-Type",
                "application/json; charset=UTF-8"
            )
            connection.connectTimeout = 10000
            connection.readTimeout = 10000
            connection.doOutput = true

            val request = JSONObject()
                .put("email", email)
                .put("password", password)

            connection.outputStream.use { output ->
                output.write(
                    request.toString().toByteArray(Charsets.UTF_8)
                )
            }

            val responseStream =
                if (connection.responseCode in 200..299) {
                    connection.inputStream
                } else {
                    connection.errorStream
                }

            val response = responseStream.bufferedReader().use {
                it.readText()
            }

            JSONObject(response)

        } finally {
            connection.disconnect()
        }
    }

    // REGISTER
    suspend fun register(
        name: String,
        email: String,
        password: String
    ): JSONObject = withContext(Dispatchers.IO) {

        val url = URL("${BASE_URL}register.php")
        val connection = url.openConnection() as HttpURLConnection

        try {
            connection.requestMethod = "POST"
            connection.setRequestProperty(
                "Content-Type",
                "application/json; charset=UTF-8"
            )
            connection.connectTimeout = 10000
            connection.readTimeout = 10000
            connection.doOutput = true

            val request = JSONObject()
                .put("name", name)
                .put("email", email)
                .put("password", password)

            connection.outputStream.use { output ->
                output.write(
                    request.toString().toByteArray(Charsets.UTF_8)
                )
            }

            val responseStream =
                if (connection.responseCode in 200..299) {
                    connection.inputStream
                } else {
                    connection.errorStream
                }

            val response = responseStream.bufferedReader().use {
                it.readText()
            }

            JSONObject(response)

        } finally {
            connection.disconnect()
        }
    }

    suspend fun addEvent(
        adminId: Int,
        title: String,
        date: String,
        time: String,
        location: String,
        description: String,
        category: String,
        isPaid: Boolean,
        price: Double
    ): JSONObject = withContext(Dispatchers.IO) {

        val url = URL("${BASE_URL}add_event.php")
        val connection = url.openConnection() as HttpURLConnection

        try {
            connection.requestMethod = "POST"
            connection.setRequestProperty(
                "Content-Type",
                "application/json; charset=UTF-8"
            )
            connection.connectTimeout = 10000
            connection.readTimeout = 10000
            connection.doOutput = true

            val request = JSONObject()
                .put("admin_id", adminId)
                .put("title", title)
                .put("date", date)
                .put("time", time)
                .put("location", location)
                .put("description", description)
                .put("category", category)
                .put("is_paid", if (isPaid) 1 else 0)
                .put("price", if (isPaid) price else 0.0)

            connection.outputStream.use { output ->
                output.write(request.toString().toByteArray(Charsets.UTF_8))
            }

            val responseStream =
                if (connection.responseCode in 200..299) {
                    connection.inputStream
                } else {
                    connection.errorStream
                }

            val response = responseStream
                .bufferedReader()
                .use { it.readText() }

            JSONObject(response)
        } finally {
            connection.disconnect()
        }
    }

}
