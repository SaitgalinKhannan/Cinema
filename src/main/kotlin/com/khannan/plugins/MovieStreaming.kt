package com.khannan.plugins

import com.khannan.service.MovieService
import com.khannan.service.connectToPostgres
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File
import java.sql.Connection

fun Application.configureFilmSending() {

    val dbConnection: Connection = connectToPostgres(embedded = true)
    val movieService = MovieService(dbConnection)

    routing {
        get("/movie/watch/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
            try {
                val movieFile = movieService.readMovieFile(id)
                val file = File(movieFile.filePath)
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

        get("/movie/preview/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
            try {
                val movieFile = movieService.readMovieFile(id)
                val file = File(movieFile.previewFilePath)
                call.respondFile(file)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.NotFound)
            }
        }
    }
}