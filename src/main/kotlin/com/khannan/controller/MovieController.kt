package com.khannan.controller

import com.khannan.model.Movie
import com.khannan.model.MovieFile
import com.khannan.repository.MovieRepository

class MovieController(private val movieRepository: MovieRepository) {

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
        movieRepository.update(id, movie, movieFile)
    }

    suspend fun deleteMovie(id: Int) {
        movieRepository.delete(id)
    }

    suspend fun movieFile(id: Int): MovieFile {
        return movieRepository.movieFile(id)
    }
}