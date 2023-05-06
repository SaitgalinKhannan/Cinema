/*
package com.khannan

import com.khannan.model.CinemaUser
import com.khannan.model.EmailPass
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

suspend fun main() {
    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
            })
        }
    }

    suspend fun registerUser(emailPass: EmailPass): Boolean = withContext(Dispatchers.IO) {
        val text = Json.encodeToString(
            CinemaUser(
                1,
                "Test",
                "Test",
                " Test",
                emailPass.email,
                emailPass.password
            )
        )

        val data = CinemaUser(
            1,
            "Test",
            "Test",
            " Test",
            emailPass.email,
            emailPass.password
        )

        val response = client.post("http://192.168.0.105:9090/register") {
            contentType(ContentType.Application.Json)
            setBody(data)
        }

        return@withContext response.body<Boolean>()
    }

    println(registerUser(EmailPass("examples1@gmail.com", "qqq")))
}*/
