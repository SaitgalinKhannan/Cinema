package com.khannan.controller

import com.khannan.model.Movie
import com.khannan.model.MovieFile
import com.khannan.model.MovieFullInfo

interface MovieControllerInterface {
    suspend fun movieFullInfoById(id: Int): MovieFullInfo

    suspend fun movieById(id: Int): Movie

    suspend fun allMovies(): List<Movie>

    suspend fun createMovie(movie: Movie, movieFile: MovieFile)

    suspend fun updateMovie(id: Int, movie: Movie, movieFile: MovieFile)

    suspend fun deleteMovie(id: Int)

    suspend fun movieFile(id: Int): MovieFile
}