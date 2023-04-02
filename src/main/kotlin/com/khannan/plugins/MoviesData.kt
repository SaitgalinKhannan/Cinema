package com.khannan.plugins

import com.khannan.model.FullMovie
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
            val fullMovie = call.receive<FullMovie>()
            val id = movieService.create(fullMovie.movie, fullMovie.movieFile)
            call.respond(HttpStatusCode.Created, id)
        }

        // Read movie
        get("/movie/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
            try {
                val movie = movieService.readMovie(id)
                call.respond(HttpStatusCode.OK, movie)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        // Read all movies
        get("/movie/all") {
            try {
                val movies = movieService.readAllMovie()
                call.respond(HttpStatusCode.OK, movies)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        // Update movie
        put("/movie/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
            val fullMovie = call.receive<FullMovie>()
            movieService.update(id, fullMovie.movie, fullMovie.movieFile)
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