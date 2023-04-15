package com.khannan.repository

import com.khannan.model.Movie
import com.khannan.model.MovieFile
import io.ktor.server.application.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.Connection
import java.sql.DriverManager
import java.sql.Statement

fun Application.connectToPostgres(embedded: Boolean): Connection {
    Class.forName("org.postgresql.Driver")

    return if (embedded) {
        DriverManager.getConnection("jdbc:postgresql://localhost:5432/movies", "postgres", "roza")
    } else {
        val url = environment.config.property("postgres.url").getString()
        val user = environment.config.property("postgres.user").getString()
        val password = environment.config.property("postgres.password").getString()

        DriverManager.getConnection(url, user, password)
    }
}

class MovieRepository(private val connection: Connection) {
    companion object {
        private const val CREATE_TABLE_MOVIES =
            "CREATE TABLE IF NOT EXISTS Movie (movId INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY, movTitle VARCHAR(255) NOT NULL, movYear INTEGER NOT NULL, movTime INTEGER NOT NULL, movLang VARCHAR(255) NOT NULL, movRelCountry VARCHAR(255) NOT NULL)"
        private const val CREATE_TABLE_ACTOR =
            "CREATE TABLE IF NOT EXISTS Actor (actId INTEGER PRIMARY KEY, actFname VARCHAR(255) NOT NULL, catLname VARCHAR(255) NOT NULL, actGender VARCHAR(10) NOT NULL)"
        private const val CREATE_TABLE_DIRECTOR =
            "CREATE TABLE IF NOT EXISTS Director (dirId INTEGER PRIMARY KEY, dirFname VARCHAR(255) NOT NULL, dirLname VARCHAR(255) NOT NULL)"
        private const val CREATE_TABLE_GENRE =
            "CREATE TABLE IF NOT EXISTS Genre (genId INTEGER PRIMARY KEY, genTitle VARCHAR(255) NOT NULL)"
        private const val CREATE_TABLE_MOVIE_CAST =
            "CREATE TABLE IF NOT EXISTS MovieCast (actId INTEGER NOT NULL, movId INTEGER NOT NULL, role VARCHAR(255) NOT NULL, PRIMARY KEY (actId, movId), FOREIGN KEY (actId) REFERENCES Actor(actId), FOREIGN KEY (movId) REFERENCES Movie(movId))"
        private const val CREATE_TABLE_MOVIE_DIRECTION =
            "CREATE TABLE IF NOT EXISTS MovieDirection (dirId INTEGER NOT NULL, movId INTEGER NOT NULL, PRIMARY KEY (dirId, movId), FOREIGN KEY (dirId) REFERENCES Director(dirId), FOREIGN KEY (movId) REFERENCES Movie(movId))"
        private const val CREATE_TABLE_MOVIE_GENRE =
            "CREATE TABLE IF NOT EXISTS MovieGenre (movId INTEGER NOT NULL, genId INTEGER NOT NULL, PRIMARY KEY (movId, genId), FOREIGN KEY (movId) REFERENCES Movie(movId), FOREIGN KEY (genId) REFERENCES Genre(genId))"
        private const val CREATE_TABLE_REVIEWER =
            "CREATE TABLE IF NOT EXISTS Reviewer (revId INTEGER PRIMARY KEY, revName VARCHAR(255) NOT NULL)"
        private const val CREATE_TABLE_RATING =
            "CREATE TABLE IF NOT EXISTS Rating (movId INTEGER NOT NULL, revId INTEGER NOT NULL, revStars INTEGER NOT NULL, numORatings INTEGER NOT NULL, PRIMARY KEY (movId, revId), FOREIGN KEY (movId) REFERENCES Movie(movId), FOREIGN KEY (revId) REFERENCES Reviewer(revId))"
        private const val CREATE_TABLE_MOVIEFILE =
            "CREATE TABLE IF NOT EXISTS MovieFile (movId INTEGER NOT NULL, movPath VARCHAR(255) NOT NULL, movPreviewPath VARCHAR(255) NOT NULL, CONSTRAINT id_unique UNIQUE (movId), FOREIGN KEY (movId) REFERENCES Movie (movId))"
        private const val SELECT_MOVIE_BY_ID =
            "SELECT movtitle, movyear, movtime, movlang, movrelcountry FROM movie WHERE movid = ?"
        private const val SELECT_MOVIEFILE_BY_ID =
            "SELECT movpath, movpreviewpath FROM moviefile WHERE movid = ?"
        private const val SELECT_ALL_MOVIEFILE =
            "SELECT movid, movpath, movpreviewpath FROM moviefile ORDER BY movid"
        private const val SELECT_ALL_MOVIES =
            "SELECT movid, movtitle, movyear, movtime, movlang, movrelcountry FROM movie ORDER BY movid"
        private const val INSERT_MOVIE =
            "INSERT INTO movie (movtitle, movyear, movtime, movlang, movrelcountry) VALUES (?, ?, ?, ?, ?)"
        private const val INSERT_MOVIEFILE =
            "INSERT INTO moviefile (movid, movpath, movpreviewpath) VALUES (?, ?, ?)"
        private const val UPDATE_MOVIE =
            "UPDATE movie SET movtitle = ?, movyear = ?, movtime = ?, movlang = ?, movrelcountry = ? WHERE movid = ?"
        private const val UPDATE_MOVIEFILE =
            "UPDATE moviefile SET movpath = ?, movpreviewpath = ? WHERE movid = ?"
        private const val DELETE_MOVIE = "DELETE FROM movie WHERE movid = ?"

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
            statement.executeUpdate(CREATE_TABLE_REVIEWER)
            statement.executeUpdate(CREATE_TABLE_RATING)
            statement.executeUpdate(CREATE_TABLE_MOVIEFILE)
        } catch (e: Exception) {
            println(e.message)
        }
    }

    // Create new movie
    suspend fun create(movie: Movie, movieFile: MovieFile): Int = withContext(Dispatchers.IO) {
        val statementMovie = connection.prepareStatement(INSERT_MOVIE, Statement.RETURN_GENERATED_KEYS)
        statementMovie.setString(1, movie.title)
        statementMovie.setInt(2, movie.releaseYear)
        statementMovie.setInt(3, movie.runtimeMinutes)
        statementMovie.setString(4, movie.language)
        statementMovie.setString(5, movie.releaseCountry)
        statementMovie.executeUpdate()

        val statementMovieFile = connection.prepareStatement(INSERT_MOVIEFILE)
        statementMovieFile.setInt(1, movieFile.id)
        statementMovieFile.setString(2, movieFile.filePath)
        statementMovieFile.setString(3, movieFile.previewFilePath)

        statementMovieFile.executeUpdate()

        val generatedKeys = statementMovie.generatedKeys
        if (generatedKeys.next()) {
            return@withContext generatedKeys.getInt(1)
        } else {
            throw Exception("Unable to retrieve the id of the newly inserted movie")
        }
    }

    // Read a movie
    suspend fun movie(id: Int): Movie = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(SELECT_MOVIE_BY_ID)
        statement.setInt(1, id)
        val resultSet = statement.executeQuery()

        if (resultSet.next()) {
            val movTitle = resultSet.getString("movtitle")
            val movYear = resultSet.getInt("movyear")
            val movTime = resultSet.getInt("movtime")
            val movLang = resultSet.getString("movlang")
            val movRelCountry = resultSet.getString("movrelcountry")

            return@withContext Movie(
                id,
                movTitle,
                movYear,
                movTime,
                movLang,
                movRelCountry
            )
        } else {
            throw Exception("Record not found")
        }
    }

    suspend fun movieFile(id: Int): MovieFile = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(SELECT_MOVIEFILE_BY_ID)
        statement.setInt(1, id)
        val resultSet = statement.executeQuery()

        if (resultSet.next()) {
            val filePath = resultSet.getString("movpath")
            val previewFilePath = resultSet.getString("movpreviewpath")

            return@withContext MovieFile(
                id,
                filePath,
                previewFilePath
            )
        } else {
            throw Exception("Record not found")
        }
    }

    // Read all movies
    suspend fun allMovies(): List<Movie> = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(SELECT_ALL_MOVIES)
        val resultSet = statement.executeQuery()
        val moviesList = mutableListOf<Movie>()

        while (resultSet.next()) {
            val movId = resultSet.getInt("movid")
            val movTitle = resultSet.getString("movtitle")
            val movYear = resultSet.getInt("movyear")
            val movTime = resultSet.getInt("movtime")
            val movLang = resultSet.getString("movlang")
            val movRelCountry = resultSet.getString("movrelcountry")

            moviesList.add(
                Movie(
                    movId,
                    movTitle,
                    movYear,
                    movTime,
                    movLang,
                    movRelCountry
                )
            )
        }

        if (moviesList.isNotEmpty()) {
            return@withContext moviesList
        } else {
            throw Exception("Films not found")
        }
    }

    @Suppress("Unused")
    suspend fun allMoviesFiles(): List<MovieFile> = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(SELECT_ALL_MOVIEFILE)
        val resultSet = statement.executeQuery()
        val moviesList = mutableListOf<MovieFile>()

        while (resultSet.next()) {
            val movId = resultSet.getInt("movid")
            val movPath = resultSet.getString("movpath")
            val movPreviewPath = resultSet.getString("movpreviewpath")

            moviesList.add(
                MovieFile(
                    movId,
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
    suspend fun update(id: Int, movie: Movie, movieFile: MovieFile) = withContext(Dispatchers.IO) {
        val statementMovie = connection.prepareStatement(UPDATE_MOVIE)
        statementMovie.setString(1, movie.title)
        statementMovie.setInt(2, movie.releaseYear)
        statementMovie.setInt(3, movie.runtimeMinutes)
        statementMovie.setString(4, movie.language)
        statementMovie.setString(5, movie.releaseCountry)
        statementMovie.setInt(6, id)
        statementMovie.executeUpdate()

        val statementMovieFile = connection.prepareStatement(UPDATE_MOVIEFILE)
        statementMovieFile.setString(1, movieFile.filePath)
        statementMovieFile.setString(2, movieFile.previewFilePath)
        statementMovieFile.setInt(3, id)
        statementMovieFile.executeUpdate()
    }

    // Delete a movie
    suspend fun delete(id: Int) = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(DELETE_MOVIE)
        statement.setInt(1, id)
        statement.executeUpdate()
    }
}
