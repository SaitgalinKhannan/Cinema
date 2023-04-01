package com.khannan.service

import com.khannan.model.Movie
import io.ktor.server.application.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.Connection
import java.sql.DriverManager
import java.sql.Statement

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
        private const val CREATE_TABLE_MOVIES =
            "CREATE TABLE IF NOT EXISTS Movie (movId INTEGER PRIMARY KEY,  movTitle VARCHAR(255) NOT NULL,  movYear INTEGER NOT NULL,  movTime INTEGER NOT NULL,  movLang VARCHAR(255) NOT NULL,  movRelCountry VARCHAR(255) NOT NULL,  movPath VARCHAR(255) NOT NULL,  movPreviewPath VARCHAR(255) NOT NULL)"
        private const val CREATE_TABLE_ACTOR =
            "CREATE TABLE IF NOT EXISTS Actor (actId INTEGER PRIMARY KEY, actFname VARCHAR(255) NOT NULL, catLname VARCHAR(255) NOT NULL, actGender VARCHAR(10) NOT NULL)"
        private const val CREATE_TABLE_DIRECTOR =
            "CREATE TABLE IF NOT EXISTS Director (dirId INTEGER PRIMARY KEY, dirFname VARCHAR(255) NOT NULL, dirLname VARCHAR(255) NOT NULL)"
        private const val CREATE_TABLE_GENRE =
            "CREATE TABLE IF NOT EXISTS Genre (genId INTEGER PRIMARY KEY, genTitle VARCHAR(255) NOT NULL)"
        private const val CREATE_TABLE_MOVIE_CAST =
            "CREATE TABLE MovieCast (actId INTEGER NOT NULL, movId INTEGER NOT NULL, role VARCHAR(255) NOT NULL, PRIMARY KEY (actId, movId), FOREIGN KEY (actId) REFERENCES Actor(actId), FOREIGN KEY (movId) REFERENCES Movie(movId))"
        private const val CREATE_TABLE_MOVIE_DIRECTION =
            "CREATE TABLE MovieDirection (dirId INTEGER NOT NULL, movId INTEGER NOT NULL, PRIMARY KEY (dirId, movId), FOREIGN KEY (dirId) REFERENCES Director(dirId), FOREIGN KEY (movId) REFERENCES Movie(movId))"
        private const val CREATE_TABLE_MOVIE_GENRE =
            "CREATE TABLE MovieGenre (movId INTEGER NOT NULL, genId INTEGER NOT NULL, PRIMARY KEY (movId, genId), FOREIGN KEY (movId) REFERENCES Movie(movId), FOREIGN KEY (genId) REFERENCES Genre(genId))"
        private const val CREATE_TABLE_RATING =
            "CREATE TABLE Rating (movId INTEGER NOT NULL, revId INTEGER NOT NULL, revStars INTEGER NOT NULL, numORatings INTEGER NOT NULL, PRIMARY KEY (movId, revId), FOREIGN KEY (movId) REFERENCES Movie(movId), FOREIGN KEY (revId) REFERENCES Reviewer(revId))"
        private const val CREATE_TABLE_REVIEWER =
            "CREATE TABLE Reviewer (revId INTEGER PRIMARY KEY, revName VARCHAR(255) NOT NULL)"
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

    init {
        val statement = connection.createStatement()
        try {
            statement.executeUpdate(CREATE_TABLE_MOVIES)
            statement.executeUpdate(CREATE_TABLE_ACTOR)
            statement.executeUpdate(CREATE_TABLE_DIRECTOR)
            statement.executeUpdate(CREATE_TABLE_GENRE)
            statement.executeUpdate(CREATE_TABLE_MOVIE_CAST)
            statement.executeUpdate(CREATE_TABLE_MOVIE_DIRECTION)
            statement.executeUpdate(CREATE_TABLE_MOVIE_GENRE)
            statement.executeUpdate(CREATE_TABLE_RATING)
            statement.executeUpdate(CREATE_TABLE_REVIEWER)
        } catch(e: Exception) {
            println(e.message)
        }
    }

    // Create new movie
    suspend fun create(movie: Movie): Int = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(INSERT_MOVIE, Statement.RETURN_GENERATED_KEYS)
        statement.setString(1, movie.title)
        statement.setInt(2, movie.releaseYear)
        statement.setInt(3, movie.runtimeMinutes)
        statement.setString(4, movie.language)
        statement.setString(5, movie.releaseCountry)
        statement.setString(6, movie.filePath)
        statement.setString(7, movie.previewFilePath)
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
        statement.setString(1, movie.title)
        statement.setInt(2, movie.releaseYear)
        statement.setInt(3, movie.runtimeMinutes)
        statement.setString(4, movie.language)
        statement.setString(5, movie.releaseCountry)
        statement.setString(6, movie.language)
        statement.setString(7, movie.previewFilePath)
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
