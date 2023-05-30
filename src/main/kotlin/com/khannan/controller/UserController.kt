package com.khannan.controller

import com.khannan.model.CinemaUser
import com.khannan.model.EmailPass
import com.khannan.repository.UserRepositoryInterface

class UserController(private val userRepository: UserRepositoryInterface): UserControllerInterface {
    override suspend fun userData(id: Int): CinemaUser {
        return userRepository.userData(id)
    }

    override suspend fun updateUserData(cinemaUser: CinemaUser): Boolean {
        return userRepository.updateUserData(cinemaUser)
    }

    override suspend fun registerUser(cinemaUser: CinemaUser): Boolean {
        return userRepository.registerUser(cinemaUser)
    }

    override suspend fun loginUser(emailPass: EmailPass): Pair<Int, Boolean> {
        return userRepository.loginUser(emailPass.email, emailPass.password)
    }
}