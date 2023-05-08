package com.khannan.repository

import io.ktor.server.application.*
import java.sql.Connection
import java.sql.DriverManager

fun Application.connectToDataBase(embedded: Boolean): Connection {
    Class.forName("org.postgresql.Driver")

    return if (embedded) {
        DriverManager.getConnection("jdbc:postgresql://localhost:5432/movies", "postgres", "roza")
    } else {
        val url = environment.config.property("postgres.url").getString()
        val user = environment.config.property("postgres.user").getString()
        val password = environment.config.property("postgres.password").getString()

        DriverManager.getConnection(url, user, password)
    }
}