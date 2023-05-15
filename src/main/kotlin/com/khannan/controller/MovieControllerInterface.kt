package com.khannan.controller

import com.khannan.model.Actor
import com.khannan.model.Movie
import com.khannan.model.MovieFile
import com.khannan.model.MovieFullInfo

interface MovieControllerInterface {
    suspend fun searchMovieByTitle(title: String): List<Movie>
    suspend fun movieFullInfoById(id: Int): MovieFullInfo
    suspend fun movieCastById(id: Int): List<Actor>
    suspend fun insertUserMovie(userId: Int, movId: Int)
    suspend fun deleteUserMovie(userId: Int, movId: Int)
    suspend fun movieByUser(id: Int): List<Movie>
    suspend fun movieById(id: Int): Movie
    suspend fun allMovies(): List<Movie>
    suspend fun createMovie(movie: Movie, movieFile: MovieFile)
    suspend fun updateMovie(id: Int, movie: Movie, movieFile: MovieFile)
    suspend fun deleteMovie(id: Int)
    suspend fun movieFile(id: Int): MovieFile
}