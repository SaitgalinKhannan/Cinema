package com.khannan.repository

import com.khannan.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.sql.Connection
import java.sql.Statement

class MovieRepository(private val connection: Connection) : MovieRepositoryInterface {
    companion object {
        private const val CREATE_TABLE_MOVIES =
            "CREATE TABLE IF NOT EXISTS Movie (movId INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY, movTitle VARCHAR(255) NOT NULL, movYear INTEGER NOT NULL, movTime INTEGER NOT NULL, movLang VARCHAR(255) NOT NULL, movRelCountry VARCHAR(255) NOT NULL)"
        private const val CREATE_TABLE_ACTOR =
            "CREATE TABLE IF NOT EXISTS Actor (actId INTEGER PRIMARY KEY generated always as identity, actFirstName VARCHAR(255) NOT NULL, actLastName VARCHAR(255) NOT NULL, actGender VARCHAR(10) NOT NULL)"
        private const val CREATE_TABLE_DIRECTOR =
            "CREATE TABLE IF NOT EXISTS Director (dirId INTEGER PRIMARY KEY, dirFirstName VARCHAR(255) NOT NULL, dirLastName VARCHAR(255) NOT NULL)"
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
        private const val CREATE_TABLE_REVIEW =
            "CREATE TABLE IF NOT EXISTS Review (movId INTEGER NOT NULL, revId INTEGER NOT NULL, revStars INTEGER NOT NULL, PRIMARY KEY (movId, revId), FOREIGN KEY (movId) REFERENCES Movie(movId), FOREIGN KEY (revId) REFERENCES Reviewer(revId))"
        private const val CREATE_TABLE_MOVIE_FILE =
            "CREATE TABLE IF NOT EXISTS MovieFile (movId INTEGER NOT NULL, movPath VARCHAR(255) NOT NULL, movPreviewPath VARCHAR(255) NOT NULL, CONSTRAINT id_unique UNIQUE (movId), FOREIGN KEY (movId) REFERENCES Movie (movId))"
        private const val CREATE_TABLE_USER_MOVIE =
            "CREATE TABLE IF NOT EXISTS userMovie (userid INTEGER, movId INTEGER, FOREIGN KEY (userid) REFERENCES cinemauser(userid), FOREIGN KEY (movid) REFERENCES movie(movid), PRIMARY KEY (userid, movid))"
        private const val SELECT_MOVIE_BY_ID =
            "SELECT movtitle, movyear, movtime, movlang, movrelcountry FROM movie WHERE movid = ?"
        private const val SELECT_MOVIE_FILE_BY_ID =
            "SELECT movpath, movpreviewpath FROM moviefile WHERE movid = ?"
        private const val SELECT_ALL_MOVIE_FILE =
            "SELECT movid, movpath, movpreviewpath FROM moviefile ORDER BY movid"
        private const val SELECT_ALL_MOVIES =
            "SELECT movid, movtitle, movyear, movtime, movlang, movrelcountry FROM movie ORDER BY movid"
        private const val SELECT_MOVIES_BY_TITLE =
            "SELECT movId, movTitle, movYear, movTime, movLang, movRelCountry FROM movie WHERE LOWER(movTitle) LIKE"
        private const val SELECT_MOVIE_GENRE_BY_ID =
            "SELECT genre.genid, genre.gentitle FROM moviegenre " +
                    "JOIN genre ON moviegenre.genid = genre.genid " +
                    "WHERE moviegenre.movid = ?"
        private const val SELECT_MOVIE_CAST_BY_ID =
            "SELECT actor.actid, actfirstname, actlastname, role, actgender\n" +
                    "FROM moviecast\n" +
                    "JOIN actor ON moviecast.actid = actor.actid\n" +
                    "WHERE moviecast.movid = ?"
        private const val SELECT_MOVIE_DIRECTORS_BY_ID =
            "SELECT director.dirid, director.dirfirstname, director.dirlastname " +
                    "FROM moviedirection " +
                    "JOIN director ON moviedirection.dirid = director.dirid " +
                    "WHERE moviedirection.movid = ?"
        private const val SELECT_USER_MOVIES =
            "SELECT movie.movid, movie.movtitle, movie.movyear, movie.movtime, movie.movlang, movie.movrelcountry FROM movie INNER JOIN usermovie ON usermovie.movid = movie.movid WHERE usermovie.userid = ?"
        private const val SELECT_MOVIE_REVIEWS_BY_ID =
            "SELECT rv.revid, r.revname, rv.revstars FROM reviewer r JOIN review rv ON r.revid = rv.revid WHERE rv.movid = ?"
        private const val SELECT_MOVIE_RATING_BY_ID =
            "SELECT AVG(revstars) AS rating FROM review WHERE movid = ?"
        private const val INSERT_MOVIE =
            "INSERT INTO movie (movtitle, movyear, movtime, movlang, movrelcountry) VALUES (?, ?, ?, ?, ?)"
        private const val INSERT_MOVIE_FILE =
            "INSERT INTO moviefile (movid, movpath, movpreviewpath) VALUES (?, ?, ?)"
        private const val INSERT_USER_MOVIE =
            "INSERT INTO usermovie (userid, movid) VALUES (?, ?)"
        private const val DELETE_USER_MOVIE =
            "DELETE FROM usermovie WHERE userid = ? AND movid = ?"
        private const val UPDATE_MOVIE =
            "UPDATE movie SET movtitle = ?, movyear = ?, movtime = ?, movlang = ?, movrelcountry = ? WHERE movid = ?"
        private const val UPDATE_MOVIE_FILE =
            "UPDATE moviefile SET movpath = ?, movpreviewpath = ? WHERE movid = ?"
        private const val DELETE_MOVIE_FILE = "DELETE FROM moviefile WHERE movid = ?"
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
            statement.executeUpdate(CREATE_TABLE_REVIEW)
            statement.executeUpdate(CREATE_TABLE_MOVIE_FILE)
            statement.executeUpdate(CREATE_TABLE_USER_MOVIE)
        } catch (e: Exception) {
            println(e.message)
        }
    }

    override suspend fun searchMovieByTitle(title: String): List<Movie> = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(SELECT_MOVIES_BY_TITLE + " LOWER('%${title.trim()}%')")
        val file = File("output.txt")
        file.writeText(statement.toString())
        val resultSet = statement.executeQuery()
        val moviesList = mutableListOf<Movie>()

        while (resultSet.next()) {
            val movId = resultSet.getInt("movId")
            val movTitle = resultSet.getString("movTitle")
            val movYear = resultSet.getInt("movYear")
            val movTime = resultSet.getInt("movTime")
            val movLang = resultSet.getString("movLang")
            val movRelCountry = resultSet.getString("movRelCountry")

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

        return@withContext moviesList
    }

    override suspend fun movieFullInfo(id: Int): MovieFullInfo = withContext(Dispatchers.IO) {
        try {
            val movie = movie(id)
            val movieFile = movieFile(id)
            val movieGenre = movieGenre(id)
            val movieCast = movieCast(id)
            val movieDirectors = movieDirectors(id)
            val movieReviews = movieReviews(id)
            val movieRating: Int = movieRating(id)

            return@withContext MovieFullInfo(
                movie,
                movieFile,
                movieGenre,
                movieCast,
                movieDirectors,
                movieReviews,
                movieRating
            )
        } catch (e: Exception) {
            println(e.message)
            throw Exception("Record not found: movieFullInfo")
        }
    }

    override suspend fun insertUserMovie(userId: Int, movId: Int) {
        val userMoviesStatement = connection.prepareStatement(INSERT_USER_MOVIE)
        userMoviesStatement.setInt(1, userId)
        userMoviesStatement.setInt(2, movId)
        userMoviesStatement.execute()
    }

    override suspend fun deleteUserMovie(userId: Int, movId: Int) {
        val userMoviesStatement = connection.prepareStatement(DELETE_USER_MOVIE)
        userMoviesStatement.setInt(1, userId)
        userMoviesStatement.setInt(2, movId)
        userMoviesStatement.execute()
    }

    override suspend fun movieByUser(id: Int): List<Movie> = withContext(Dispatchers.IO) {
        val userMoviesStatement = connection.prepareStatement(SELECT_USER_MOVIES)
        userMoviesStatement.setInt(1, id)
        val userMoviesResultSet = userMoviesStatement.executeQuery()
        val moviesList = mutableListOf<Movie>()

        while (userMoviesResultSet.next()) {
            val movId = userMoviesResultSet.getInt("movId")
            val movTitle = userMoviesResultSet.getString("movTitle")
            val movYear = userMoviesResultSet.getInt("movYear")
            val movTime = userMoviesResultSet.getInt("movTime")
            val movLang = userMoviesResultSet.getString("movLang")
            val movRelCountry = userMoviesResultSet.getString("movRelCountry")

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

        return@withContext moviesList
    }

    override suspend fun movieRating(id: Int): Int = withContext(Dispatchers.IO) {
        val movieRatingStatement = connection.prepareStatement(SELECT_MOVIE_RATING_BY_ID)
        movieRatingStatement.setInt(1, id)
        val movieRatingResultSet = movieRatingStatement.executeQuery()

        if (movieRatingResultSet.next()) {
            return@withContext movieRatingResultSet.getInt("rating")
        } else {
            return@withContext 0
        }
    }

    override suspend fun movieReviews(id: Int): List<Review> = withContext(Dispatchers.IO) {
        val movieReviewsStatement = connection.prepareStatement(SELECT_MOVIE_REVIEWS_BY_ID)
        movieReviewsStatement.setInt(1, id)
        val movieReviewsResultSet = movieReviewsStatement.executeQuery()
        val movieReviews: MutableList<Review> = mutableListOf()

        while (movieReviewsResultSet.next()) {
            val reviewId = movieReviewsResultSet.getInt("revId")
            val reviewerName = movieReviewsResultSet.getString("revName")
            val reviewStars = movieReviewsResultSet.getInt("revStars")
            val review = Review(reviewId, reviewerName, reviewStars)
            movieReviews.add(review)
        }

        return@withContext movieReviews
    }

    override suspend fun movieDirectors(id: Int): List<Director> = withContext(Dispatchers.IO) {
        val movieDirectorsStatement = connection.prepareStatement(SELECT_MOVIE_DIRECTORS_BY_ID)
        movieDirectorsStatement.setInt(1, id)
        val movieDirectorsResultSet = movieDirectorsStatement.executeQuery()
        val movieDirectors: MutableList<Director> = mutableListOf()

        while (movieDirectorsResultSet.next()) {
            val directorId = movieDirectorsResultSet.getInt("dirId")
            val firstName = movieDirectorsResultSet.getString("dirFirstName")
            val lastName = movieDirectorsResultSet.getString("dirLastName")
            val director = Director(directorId, firstName, lastName)
            movieDirectors.add(director)
        }

        return@withContext movieDirectors
    }

    override suspend fun movieCast(id: Int): List<Actor> = withContext(Dispatchers.IO) {
        val movieCastStatement = connection.prepareStatement(SELECT_MOVIE_CAST_BY_ID)
        movieCastStatement.setInt(1, id)
        val movieCastResultSet = movieCastStatement.executeQuery()
        var movieCast = mutableListOf<Actor>()

        if (movieCastResultSet.next()) {
            val cast = mutableListOf<Actor>()
            while (movieCastResultSet.next()) {
                val actorId = movieCastResultSet.getInt("actId")
                val firstName = movieCastResultSet.getString("actFirstName")
                val lastName = movieCastResultSet.getString("actLastName")
                val role = movieCastResultSet.getString("role")
                val gender = movieCastResultSet.getString("actGender")
                val actor = Actor(actorId, firstName, lastName, role, gender)
                cast.add(actor)
            }
            movieCast = cast
        }

        return@withContext movieCast
    }

    override suspend fun movieGenre(id: Int): List<Genre> = withContext(Dispatchers.IO) {
        val movieGenreStatement = connection.prepareStatement(SELECT_MOVIE_GENRE_BY_ID)
        movieGenreStatement.setInt(1, id)
        val movieGenreResultSet = movieGenreStatement.executeQuery()
        val movieGenres = mutableListOf<Genre>()

        while (movieGenreResultSet.next()) {
            val genreId = movieGenreResultSet.getInt("genId")
            val genreTitle = movieGenreResultSet.getString("genTitle")
            val genre = Genre(genreId, genreTitle)
            movieGenres.add(genre)
        }

        return@withContext movieGenres
    }

    override suspend fun create(movie: Movie, movieFile: MovieFile): Int = withContext(Dispatchers.IO) {
        val statementMovie = connection.prepareStatement(INSERT_MOVIE, Statement.RETURN_GENERATED_KEYS)
        statementMovie.setString(1, movie.title)
        statementMovie.setInt(2, movie.releaseYear)
        statementMovie.setInt(3, movie.runtimeMinutes)
        statementMovie.setString(4, movie.language)
        statementMovie.setString(5, movie.releaseCountry)
        statementMovie.executeUpdate()

        val statementMovieFile = connection.prepareStatement(INSERT_MOVIE_FILE)
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

    override suspend fun movie(id: Int): Movie = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(SELECT_MOVIE_BY_ID)
        statement.setInt(1, id)
        val resultSet = statement.executeQuery()

        if (resultSet.next()) {
            val movTitle = resultSet.getString("movTitle")
            val movYear = resultSet.getInt("movYear")
            val movTime = resultSet.getInt("movTime")
            val movLang = resultSet.getString("movLang")
            val movRelCountry = resultSet.getString("movRelCountry")

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

    override suspend fun deleteMovieFile(id: Int): Int = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(DELETE_MOVIE_FILE)
        statement.setInt(1, id)
        statement.executeUpdate()
    }

    override suspend fun movieFile(id: Int): MovieFile = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(SELECT_MOVIE_FILE_BY_ID)
        statement.setInt(1, id)
        val resultSet = statement.executeQuery()

        if (resultSet.next()) {
            val filePath = resultSet.getString("movPath")
            val previewFilePath = resultSet.getString("movPreviewPath")

            return@withContext MovieFile(
                id,
                filePath,
                previewFilePath
            )
        } else {
            throw Exception("Record not found")
        }
    }

    override suspend fun allMovies(): List<Movie> = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(SELECT_ALL_MOVIES)
        val resultSet = statement.executeQuery()

        val moviesList = mutableListOf<Movie>()

        while (resultSet.next()) {
            val movId = resultSet.getInt("movId")
            val movTitle = resultSet.getString("movTitle")
            val movYear = resultSet.getInt("movYear")
            val movTime = resultSet.getInt("movTime")
            val movLang = resultSet.getString("movLang")
            val movRelCountry = resultSet.getString("movRelCountry")

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

    override suspend fun allMoviesFiles(): List<MovieFile> = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(SELECT_ALL_MOVIE_FILE)
        val resultSet = statement.executeQuery()
        val moviesList = mutableListOf<MovieFile>()

        while (resultSet.next()) {
            val movId = resultSet.getInt("movId")
            val movPath = resultSet.getString("movPath")
            val movPreviewPath = resultSet.getString("movPreviewPath")

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

    override suspend fun updateMovie(id: Int, movie: Movie, movieFile: MovieFile): Unit = withContext(Dispatchers.IO) {
        val statementMovie = connection.prepareStatement(UPDATE_MOVIE)
        statementMovie.setString(1, movie.title)
        statementMovie.setInt(2, movie.releaseYear)
        statementMovie.setInt(3, movie.runtimeMinutes)
        statementMovie.setString(4, movie.language)
        statementMovie.setString(5, movie.releaseCountry)
        statementMovie.setInt(6, id)
        statementMovie.executeUpdate()

        val statementMovieFile = connection.prepareStatement(UPDATE_MOVIE_FILE)
        statementMovieFile.setString(1, movieFile.filePath)
        statementMovieFile.setString(2, movieFile.previewFilePath)
        statementMovieFile.setInt(3, id)
        statementMovieFile.executeUpdate()
    }

    override suspend fun deleteMovie(id: Int): Unit = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(DELETE_MOVIE)
        statement.setInt(1, id)
        statement.executeUpdate()
    }
}