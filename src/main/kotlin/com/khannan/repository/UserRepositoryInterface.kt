package com.khannan.repository

import com.khannan.model.CinemaUser

interface UserRepositoryInterface {
    suspend fun registerUser(cinemaUser: CinemaUser): Boolean
    suspend fun loginUser(email: String, password: String): Pair<Int, Boolean>
    suspend fun userData(id: Int): CinemaUser
    suspend fun updateUserData(cinemaUser: CinemaUser): Boolean
}