package com.khannan.view

import com.khannan.controller.MovieController
import com.khannan.model.FullMovie
import com.khannan.repository.MovieRepository
import com.khannan.repository.connectToPostgres
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.sql.Connection

fun Application.configureDatabases() {

    val dbConnection: Connection = connectToPostgres(embedded = true)
    val movieController = MovieController(MovieRepository(dbConnection))

    routing {
        // Create movie
        post("/movie") {
            val fullMovie = call.receive<FullMovie>()
            val id = movieController.createMovie(fullMovie.movie, fullMovie.movieFile)
            call.respond(HttpStatusCode.Created, id)
        }

        // Read movie
        get("/movie/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
            try {
                val movie = movieController.movieById(id)
                call.respond(HttpStatusCode.OK, movie)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        // Read all movies
        get("/movie/all") {
            try {
                val movies = movieController.allMovies()
                call.respond(HttpStatusCode.OK, movies)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        // Update movie
        put("/movie/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
            val fullMovie = call.receive<FullMovie>()
            movieController.updateMovie(id, fullMovie.movie, fullMovie.movieFile)
            call.respond(HttpStatusCode.OK)
        }

        // Delete movie
        delete("/movie/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
            movieController.deleteMovie(id)
            call.respond(HttpStatusCode.OK)
        }
    }
}