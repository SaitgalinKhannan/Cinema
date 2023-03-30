package com.khannan.entity

import kotlinx.serialization.Serializable

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
)