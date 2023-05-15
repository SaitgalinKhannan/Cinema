package com.khannan.repository

import com.khannan.model.CinemaUser

interface UserRepositoryInterface {
    suspend fun registerUser(user: CinemaUser): Boolean
    suspend fun loginUser(email: String, password: String): Pair<Int, Boolean>
    suspend fun userData(id: Int): CinemaUser
}