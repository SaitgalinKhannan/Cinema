package com.khannan.controller

import com.khannan.model.CinemaUser
import com.khannan.model.EmailPass
import com.khannan.repository.UserRepository

class UserController(private val userRepository: UserRepository) {

    suspend fun registerUser(user: CinemaUser): Boolean {
        return userRepository.registerUser(user)
    }

    suspend fun loginUser(emailPass: EmailPass): Pair<Int, Boolean> {
        return userRepository.loginUser(emailPass.email, emailPass.password)
    }
}