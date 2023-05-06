package com.khannan

import com.khannan.view.cinemaUsers
import com.khannan.view.filmSending
import com.khannan.view.movies
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.autohead.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.partialcontent.*
import kotlinx.serialization.json.Json

fun main() {
    embeddedServer(Netty, port = 9090, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
        })
    }
    install(PartialContent)
    install(AutoHeadResponse)
    cinemaUsers()
    movies()
    filmSending()
}
