package com.khannan.view

import com.khannan.controller.MovieController
import com.khannan.controller.MovieControllerInterface
import com.khannan.model.FullMovie
import com.khannan.repository.MovieRepository
import com.khannan.repository.connectToDataBase
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File
import java.sql.Connection

fun Application.movies() {
    val dbConnection: Connection = connectToDataBase(embedded = true)
    val movieController: MovieControllerInterface = MovieController(MovieRepository(dbConnection))

    routing {
        post("/movie/title") {
            try {
                val title = call.receive<String>()
                val file = File("output1.txt")
                file.writeText(title)
                val movies = movieController.searchMovieByTitle(title)
                call.respond(HttpStatusCode.OK, movies)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        post("/movie") {
            try {
                val fullMovie = call.receive<FullMovie>()
                val id = movieController.createMovie(fullMovie.movie, fullMovie.movieFile)
                call.respond(HttpStatusCode.Created, id)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest)
            }
        }

        post("/movie/usermovie/insert") {
            try {
                val userMovie = call.receive<Pair<Int, Int>>()
                movieController.insertUserMovie(userMovie.first, userMovie.second)
                call.respond(HttpStatusCode.OK)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest)
            }
        }

        post("/movie/usermovie/delete") {
            try {
                val userMovie = call.receive<Pair<Int, Int>>()
                movieController.deleteUserMovie(userMovie.first, userMovie.second)
                call.respond(HttpStatusCode.OK)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest)
            }
        }

        get("/movie/cast/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
            try {
                val movie = movieController.movieCastById(id)
                call.respond(HttpStatusCode.OK, movie)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        get("/movie/usermovie/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
            try {
                val movie = movieController.movieByUser(id)
                call.respond(HttpStatusCode.OK, movie)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        get("/movie/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
            try {
                val movie = movieController.movieById(id)
                call.respond(HttpStatusCode.OK, movie)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        get("/movie/full/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
            try {
                val movieFullInfo = movieController.movieFullInfoById(id)
                call.respond(HttpStatusCode.OK, movieFullInfo)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        get("/movie/all") {
            try {
                val movies = movieController.allMovies()
                call.respond(HttpStatusCode.OK, movies)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        put("/movie/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
            try {
                val fullMovie = call.receive<FullMovie>()
                movieController.updateMovie(id, fullMovie.movie, fullMovie.movieFile)
                call.respond(HttpStatusCode.OK)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        delete("/movie/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
            try {
                movieController.deleteMovie(id)
                call.respond(HttpStatusCode.OK)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.NotFound)
            }
        }
    }
}