package com.khannan.controller

import com.khannan.model.Actor
import com.khannan.model.Movie
import com.khannan.model.MovieFile
import com.khannan.model.MovieFullInfo
import com.khannan.repository.MovieRepositoryInterface

class MovieController(private val movieRepository: MovieRepositoryInterface) : MovieControllerInterface {
    override suspend fun searchMovieByTitle(title: String): List<Movie> {
        return movieRepository.searchMovieByTitle(title)
    }

    override suspend fun movieFullInfoById(id: Int): MovieFullInfo {
        return movieRepository.movieFullInfo(id)
    }

    override suspend fun movieCastById(id: Int): List<Actor> {
        return movieRepository.movieCast(id)
    }

    override suspend fun insertUserMovie(userId: Int, movId: Int) {
        movieRepository.insertUserMovie(userId, movId)
    }

    override suspend fun movieByUser(id: Int): List<Movie> {
        return movieRepository.movieByUser(id)
    }

    override suspend fun movieById(id: Int): Movie {
        return movieRepository.movie(id)
    }

    override suspend fun allMovies(): List<Movie> {
        return movieRepository.allMovies()
    }

    override suspend fun createMovie(movie: Movie, movieFile: MovieFile) {
        movieRepository.create(movie, movieFile)
    }

    override suspend fun updateMovie(id: Int, movie: Movie, movieFile: MovieFile) {
        movieRepository.updateMovie(id, movie, movieFile)
    }

    override suspend fun deleteMovie(id: Int) {
        movieRepository.deleteMovie(id)
    }

    override suspend fun movieFile(id: Int): MovieFile {
        return movieRepository.movieFile(id)
    }
}