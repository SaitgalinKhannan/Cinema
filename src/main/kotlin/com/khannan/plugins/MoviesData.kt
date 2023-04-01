package com.khannan.plugins

import com.khannan.model.Movie
import com.khannan.service.MovieService
import com.khannan.service.connectToPostgres
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.sql.Connection

fun Application.configureDatabases() {

    val dbConnection: Connection = connectToPostgres(embedded = true)
    val movieService = MovieService(dbConnection)

    routing {
        // Create movie
        post("/movie") {
            val movie = call.receive<Movie>()
            val id = movieService.create(movie)
            call.respond(HttpStatusCode.Created, id)
        }

        // Read movie
        get("/movie/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
            try {
                val movie = movieService.read(id)
                call.respond(HttpStatusCode.OK, movie)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        // Read all movies
        get("/movie/all") {
            try {
                val movies = movieService.readAll()
                call.respond(HttpStatusCode.OK, movies)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        // Update movie
        put("/movie/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
            val movie = call.receive<Movie>()
            movieService.update(id, movie)
            call.respond(HttpStatusCode.OK)
        }

        // Delete movie
        delete("/movie/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
            movieService.delete(id)
            call.respond(HttpStatusCode.OK)
        }
    }
}