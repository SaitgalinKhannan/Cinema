package com.khannan.model

import kotlinx.serialization.Serializable

@Serializable
data class Actor(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val gender: String
)

@Serializable
data class Director(
    val id: Int,
    val firstName: String,
    val lastName: String
)

@Serializable
data class Genre(
    val id: Int,
    val title: String
)

@Serializable
data class FullMovie(
    val movie: Movie,
    val movieFile: MovieFile
)

@Serializable
data class Movie(
    val id: Int,
    val title: String,
    val releaseYear: Int,
    val runtimeMinutes: Int,
    val language: String,
    val releaseCountry: String,
)

@Serializable
data class MovieFile(
    val id: Int,
    val filePath: String,
    val previewFilePath: String
)

@Serializable
data class MovieCast(
    val actorId: Int,
    val movieId: Int,
    val role: String
)

@Serializable
data class MovieDirection(
    val directorId: Int,
    val movieId: Int
)

@Serializable
data class MovieGenre(
    val movieId: Int,
    val genreId: Int
)

@Serializable
data class Rating(
    val movieId: Int,
    val reviewerId: Int,
    val stars: Int,
    val numberOfRatings: Int
)

@Serializable
data class Reviewer(
    val id: Int,
    val name: String
)

data class SseEvent(
    val data: String,
    val event: String? = null,
    val id: String? = null
)