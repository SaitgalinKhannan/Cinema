package com.khannan.view

import com.khannan.controller.UserController
import com.khannan.model.CinemaUser
import com.khannan.model.EmailPass
import com.khannan.repository.UserRepository
import com.khannan.repository.connectToPostgres
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.sql.Connection

fun Application.cinemaUsers() {
    val dbConnection: Connection = connectToPostgres(embedded = true)
    val userController = UserController(UserRepository(dbConnection))

    routing {

        post("/register") {
            val user = call.receive<CinemaUser>()
            val result = userController.registerUser(user)
            call.respond(HttpStatusCode.Created, result)
        }

        post("/login") {
            val user = call.receive<EmailPass>()
            val id = userController.loginUser(user)
            call.respond(HttpStatusCode.OK, id)
        }
    }
}