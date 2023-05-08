package com.khannan.controller

import com.khannan.model.CinemaUser
import com.khannan.model.EmailPass

interface UserControllerInterface {

    suspend fun registerUser(user: CinemaUser): Boolean

    suspend fun loginUser(emailPass: EmailPass): Pair<Int, Boolean>
}