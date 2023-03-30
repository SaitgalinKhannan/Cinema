package com.khannan

import com.khannan.io.sendFilm
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.khannan.plugins.*
import io.ktor.server.plugins.autohead.*
import io.ktor.server.plugins.partialcontent.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    install(PartialContent)
    install(AutoHeadResponse)
    configureSerialization()
    configureDatabases()
    configureRouting()
    sendFilm()
}
