package com.khannan

import com.khannan.view.configureDatabases
import com.khannan.view.configureFilmSending
import com.khannan.view.configureSSE
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.autohead.*
import io.ktor.server.plugins.partialcontent.*

fun main() {
    embeddedServer(Netty, port = 9090, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    install(PartialContent)
    install(AutoHeadResponse)
    configureDatabases()
    configureSSE()
    configureFilmSending()
}
