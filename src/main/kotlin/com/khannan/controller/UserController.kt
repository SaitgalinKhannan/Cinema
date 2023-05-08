package com.khannan.controller

import com.khannan.model.CinemaUser
import com.khannan.model.EmailPass
import com.khannan.repository.UserRepositoryInterface

class UserController(private val userRepository: UserRepositoryInterface): UserControllerInterface {

    override suspend fun registerUser(user: CinemaUser): Boolean {
        return userRepository.registerUser(user)
    }

    override suspend fun loginUser(emailPass: EmailPass): Pair<Int, Boolean> {
        return userRepository.loginUser(emailPass.email, emailPass.password)
    }
}