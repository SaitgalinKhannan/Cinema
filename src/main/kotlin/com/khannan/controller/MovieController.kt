package com.khannan.controller

import com.khannan.model.Movie
import com.khannan.model.MovieFile
import com.khannan.model.MovieFullInfo
import com.khannan.repository.MovieRepository

class MovieController(private val movieRepository: MovieRepository) {

    suspend fun movieFullInfoById(id: Int): MovieFullInfo {
        return movieRepository.movieFullInfo(id)
    }

    suspend fun movieById(id: Int): Movie {
        return movieRepository.movie(id)
    }

    suspend fun allMovies(): List<Movie> {
        return movieRepository.allMovies()
    }

    suspend fun createMovie(movie: Movie, movieFile: MovieFile) {
        movieRepository.create(movie, movieFile)
    }

    suspend fun updateMovie(id: Int, movie: Movie, movieFile: MovieFile) {
        movieRepository.updateMovie(id, movie, movieFile)
    }

    suspend fun deleteMovie(id: Int) {
        movieRepository.deleteMovie(id)
    }

    suspend fun movieFile(id: Int): MovieFile {
        return movieRepository.movieFile(id)
    }
}