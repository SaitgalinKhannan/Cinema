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
data class Movie(
    val id: Int,
    val title: String,
    val releaseYear: Int,
    val runtimeMinutes: Int,
    val language: String,
    val releaseCountry: String,
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

/*
@Serializable
data class Actor(
    val actId: Int,
    val actFname: String,
    val catLname: String,
    val actGender: String
)

@Serializable
data class Director(
    val dirId: Int,
    val dirFname: String,
    val dirLname: String
)

@Serializable
data class Genre(
    val genId: Int,
    val genTitle: String
)

@Serializable
data class Movie(
    val movId: Int,
    val movTitle: String,
    val movYear: Int,
    val movTime: Int,
    val movLang: String,
    val movRelCountry: String,
    val movPath: String,
    val movPreviewPath: String
)

@Serializable
data class MovieCast(
    val actId: Int,
    val movId: Int,
    val role: String
)

@Serializable
data class MovieDirection(
    val dirId: Int,
    val movId: Int
)

@Serializable
data class MovieGenre(
    val movId: Int,
    val genId: Int
)

@Serializable
data class Rating(
    val movId: Int,
    val revId: Int,
    val revStars: Int,
    val numORatings: Int
)

@Serializable
data class Reviewer(
    val revId: Int,
    val revName: String
)*/
