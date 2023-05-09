package com.khannan.model

import kotlinx.serialization.Serializable

@Serializable
data class MovieFullInfo(
    val movie: Movie,
    val movieFile: MovieFile,
    val genres: List<Genre>,
    val cast: List<Actor>,
    val directors: List<Director>,
    val reviews: List<Review>,
    val rating: Int
)

@Serializable
data class Actor(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val role: String,
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

/*@Serializable
data class Review(
    val reviewerName: String,
    val stars: Int,
)*/

@Serializable
data class Review(
    val revId: Int,
    val reviewerName: String,
    val stars: Int,
)

@Suppress("Unused")
@Serializable
data class Reviewer(
    val id: Int,
    val name: String
)

@Serializable
data class SseEvent(
    val data: String,
    val event: String? = null,
    val id: String? = null
)

@Serializable
data class CinemaUser(
    val id: Int,
    val lastName: String,
    val firstName: String,
    val middleName: String,
    val email: String,
    val password: String
)

@Serializable
data class EmailPass(
    val email: String,
    val password: String
)
