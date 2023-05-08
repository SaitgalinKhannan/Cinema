package com.khannan.repository

import com.khannan.model.*

interface MovieRepositoryInterface {

    suspend fun movieFullInfo(id: Int): MovieFullInfo

    suspend fun movieRating(id: Int): Int

    suspend fun movieReviews(id: Int): List<Review>

    suspend fun movieDirectors(id: Int): List<Director>

    suspend fun movieCast(id: Int): List<Actor>

    suspend fun movieGenre(id: Int): List<Genre>

    suspend fun create(movie: Movie, movieFile: MovieFile): Int

    suspend fun movie(id: Int): Movie

    suspend fun deleteMovieFile(id: Int): Int

    suspend fun movieFile(id: Int): MovieFile

    suspend fun allMovies(): List<Movie>

    suspend fun allMoviesFiles(): List<MovieFile>

    suspend fun updateMovie(id: Int, movie: Movie, movieFile: MovieFile)

    suspend fun deleteMovie(id: Int)
}