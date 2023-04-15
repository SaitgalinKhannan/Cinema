package com.khannan.view

import com.khannan.controller.MovieController
import com.khannan.repository.MovieRepository
import com.khannan.repository.connectToPostgres
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File
import java.sql.Connection

fun Application.configureFilmSending() {

    val dbConnection: Connection = connectToPostgres(embedded = true)
    val movieController = MovieController(MovieRepository(dbConnection))

    routing {
        get("/movie/watch/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
            try {
                val movieFile = movieController.movieFile(id)
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
                val movieFile = movieController.movieFile(id)
                val file = File(movieFile.previewFilePath)
                call.response.header(
                    HttpHeaders.ContentDisposition,
                    ContentDisposition.Attachment.withParameter(ContentDisposition.Parameters.FileName, file.name)
                        .toString()
                )
                call.respondFile(file)
                call.respondOutputStream {
                    file.inputStream().copyTo(this)
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.NotFound)
            }
        }
    }
}