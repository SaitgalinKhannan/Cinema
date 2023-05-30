package com.khannan.view

import com.khannan.controller.UserController
import com.khannan.controller.UserControllerInterface
import com.khannan.model.CinemaUser
import com.khannan.model.EmailPass
import com.khannan.repository.UserRepository
import com.khannan.repository.connectToDataBase
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.sql.Connection

fun Application.cinemaUsers() {
    val dbConnection: Connection = connectToDataBase(embedded = true)
    val userController: UserControllerInterface = UserController(UserRepository(dbConnection))

    routing {
        post("/register") {
            try {
                val user = call.receive<CinemaUser>()
                val result = userController.registerUser(user)
                call.respond(HttpStatusCode.Created, result)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        post("/login") {
            try {
                val user = call.receive<EmailPass>()
                val id = userController.loginUser(user)
                call.respond(HttpStatusCode.OK, id)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        post("/update") {
            try {
                val user = call.receive<CinemaUser>()
                val result = userController.updateUserData(user)
                call.respond(HttpStatusCode.OK, result)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        get("/user/{id}") {
            try {
                val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
                val cinemaUser = userController.userData(id)
                call.respond(HttpStatusCode.OK, cinemaUser)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.NotFound)
            }
        }
    }
}