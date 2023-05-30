package com.khannan.controller

import com.khannan.model.CinemaUser
import com.khannan.model.EmailPass

interface UserControllerInterface {
    suspend fun updateUserData(cinemaUser: CinemaUser): Boolean
    suspend fun registerUser(cinemaUser: CinemaUser): Boolean
    suspend fun loginUser(emailPass: EmailPass): Pair<Int, Boolean>
    suspend fun userData(id: Int): CinemaUser
}