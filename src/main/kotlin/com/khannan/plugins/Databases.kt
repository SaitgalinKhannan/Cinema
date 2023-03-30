package com.khannan.plugins

import com.khannan.entity.Movie
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.*
import java.sql.*

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

/**
 * Makes a connection to a Postgres database.
 *
 * In order to connect to your running Postgres process,
 * please specify the following parameters in your configuration file:
 * - postgres.url -- Url of your running database process.
 * - postgres.user -- Username for database connection
 * - postgres.password -- Password for database connection
 *
 * If you don't have a database process running yet, you may need to [download]((https://www.postgresql.org/download/))
 * and install Postgres and follow the instructions [here](https://postgresapp.com/).
 * Then, you would be edit your url,  which is usually "jdbc:postgresql://host:port/database", as well as
 * user and password values.
 *
 *
 * @param embedded -- if [true] defaults to an embedded database for tests that runs locally in the same process.
 * In this case you don't have to provide any parameters in configuration file, and you don't have to run a process.
 *
 * @return [Connection] that represent connection to the database. Please, don't forget to close this connection when
 * your application shuts down by calling [Connection.close]
 * */
fun Application.connectToPostgres(embedded: Boolean): Connection {
    Class.forName("org.postgresql.Driver")

    return if (embedded) {
        DriverManager.getConnection("jdbc:postgresql://localhost:5432/movies", "postgres", "lisunsin")
    } else {
        val url = environment.config.property("postgres.url").getString()
        val user = environment.config.property("postgres.user").getString()
        val password = environment.config.property("postgres.password").getString()

        DriverManager.getConnection(url, user, password)
    }
}

class MovieService(private val connection: Connection) {
    companion object {
        private const val SELECT_MOVIE_BY_ID =
            "SELECT mov_title, mov_year, mov_time, mov_lang, mov_rel_country, mov_path, mov_preview_path FROM movie WHERE mov_id = ?"
        private const val SELECT_ALL_MOVIES =
            "SELECT mov_id, mov_title, mov_year, mov_time, mov_lang, mov_rel_country, mov_path, mov_preview_path FROM movie"
        private const val INSERT_MOVIE =
            "INSERT INTO movie (mov_title, mov_year, mov_time, mov_lang, mov_rel_country, mov_path, mov_preview_path) VALUES (?, ?, ?, ?, ?, ?, ?)"
        private const val UPDATE_MOVIE =
            "UPDATE movie SET mov_title = ?, mov_year = ?, mov_time = ?, mov_lang = ?, mov_rel_country = ?, mov_path = ?, mov_preview_path = ? WHERE mov_id = ?"
        private const val DELETE_MOVIE = "DELETE FROM movie WHERE mov_id = ?"

    }

    /*init {
        val statement = connection.createStatement()
        try {
            statement.executeUpdate(CREATE_TABLE_MOVIES)
        } catch(e: Exception) {
            println(e.message)
        }
    }*/

    // Create new movie
    suspend fun create(movie: Movie): Int = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(INSERT_MOVIE, Statement.RETURN_GENERATED_KEYS)
        statement.setString(1, movie.movTitle)
        statement.setInt(2, movie.movYear)
        statement.setInt(3, movie.movTime)
        statement.setString(4, movie.movLang)
        statement.setString(5, movie.movRelCountry)
        statement.setString(6, movie.movPath)
        statement.setString(7, movie.movPreviewPath)
        statement.executeUpdate()

        val generatedKeys = statement.generatedKeys
        if (generatedKeys.next()) {
            return@withContext generatedKeys.getInt(1)
        } else {
            throw Exception("Unable to retrieve the id of the newly inserted movie")
        }
    }

    // Read a movie
    suspend fun read(id: Int): Movie = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(SELECT_MOVIE_BY_ID)
        statement.setInt(1, id)
        val resultSet = statement.executeQuery()

        if (resultSet.next()) {
            val movTitle = resultSet.getString("mov_title")
            val movYear = resultSet.getInt("mov_year")
            val movTime = resultSet.getInt("mov_time")
            val movLang = resultSet.getString("mov_lang")
            val movRelCountry = resultSet.getString("mov_rel_country")
            val movPath = resultSet.getString("mov_path")
            val movPreviewPath = resultSet.getString("mov_preview_path")
            return@withContext Movie(
                id,
                movTitle,
                movYear,
                movTime,
                movLang,
                movRelCountry,
                movPath,
                movPreviewPath
            )
        } else {
            throw Exception("Record not found")
        }
    }

    // Read all movies
    suspend fun readAll(): List<Movie> = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(SELECT_ALL_MOVIES)
        val resultSet = statement.executeQuery()
        val moviesList = mutableListOf<Movie>()

        while (resultSet.next()) {
            val movId = resultSet.getInt("mov_id")
            val movTitle = resultSet.getString("mov_title")
            val movYear = resultSet.getInt("mov_year")
            val movTime = resultSet.getInt("mov_time")
            val movLang = resultSet.getString("mov_lang")
            val movRelCountry = resultSet.getString("mov_rel_country")
            val movPath = resultSet.getString("mov_path")
            val movPreviewPath = resultSet.getString("mov_preview_path")
            moviesList.add(
                Movie(
                    movId,
                    movTitle,
                    movYear,
                    movTime,
                    movLang,
                    movRelCountry,
                    movPath,
                    movPreviewPath
                )
            )
        }

        if (moviesList.isNotEmpty()) {
            return@withContext moviesList
        } else {
            throw Exception("Films not found")
        }
    }

    // Update a movie
    suspend fun update(id: Int, movie: Movie) = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(UPDATE_MOVIE)
        statement.setString(1, movie.movTitle)
        statement.setInt(2, movie.movYear)
        statement.setInt(3, movie.movTime)
        statement.setString(4, movie.movLang)
        statement.setString(5, movie.movRelCountry)
        statement.setString(6, movie.movLang)
        statement.setString(7, movie.movPreviewPath)
        statement.setInt(8, id)
        statement.executeUpdate()
    }

    // Delete a movie
    suspend fun delete(id: Int) = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(DELETE_MOVIE)
        statement.setInt(1, id)
        statement.executeUpdate()
    }
}
