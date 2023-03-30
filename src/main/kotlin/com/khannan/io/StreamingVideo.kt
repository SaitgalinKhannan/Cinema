package com.khannan.io

import com.khannan.plugins.MovieService
import com.khannan.plugins.connectToPostgres
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File
import java.sql.Connection

fun Application.sendFilm() {

    val dbConnection: Connection = connectToPostgres(embedded = true)
    val movieService = MovieService(dbConnection)

    routing {
        get("/movie/watch/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
            try {
                val movie = movieService.read(id)
                val file = File(movie.movPath)
                call.response.header(
                    HttpHeaders.ContentDisposition,
                    ContentDisposition.Attachment.withParameter(ContentDisposition.Parameters.FileName, file.name)
                        .toString()
                )
                call.respondFile(file)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.NotFound)
            }
        }
    }
}