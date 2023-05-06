package com.khannan.repository

import com.khannan.model.CinemaUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.Connection
import java.sql.Statement

class UserRepository(private val connection: Connection) {
    companion object {
        private const val CREATE_TABLE_USER =
            "CREATE TABLE IF NOT EXISTS CinemaUser (userId INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY, last_name VARCHAR(50), first_name VARCHAR(50), middle_name VARCHAR(50) NULL, email VARCHAR(100) UNIQUE, password VARCHAR(100))"
        private const val INSERT_USER =
            "INSERT INTO cinemauser (last_name, first_name, middle_name, email, password) VALUES (?, ?, ?, ?, ?)"
        private const val LOGIN_USER =
            "SELECT * FROM cinemauser WHERE email = ? AND password = ?"
    }

    init {
        val statement = connection.createStatement()
        try {
            statement.executeUpdate(CREATE_TABLE_USER)
        } catch (e: Exception) {
            println(e.message)
        }
    }

    suspend fun registerUser(user: CinemaUser): Boolean = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(INSERT_USER, Statement.RETURN_GENERATED_KEYS)
        statement.setString(1, user.lastName)
        statement.setString(2, user.firstName)
        statement.setString(3, user.middleName)
        statement.setString(4, user.email)
        statement.setString(5, user.password)
        statement.executeUpdate()

        val generatedKeys = statement.generatedKeys
        if (generatedKeys.next()) {
            return@withContext true
        } else {
            throw Exception("Unable to retrieve the id of the newly inserted user")
        }
    }

    suspend fun loginUser(email: String, password: String): Pair<Int, Boolean> = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(LOGIN_USER, Statement.RETURN_GENERATED_KEYS)
        statement.setString(1, email)
        statement.setString(2, password)
        val resultSet = statement.executeQuery()

        if (resultSet.next()) {
            return@withContext Pair<Int, Boolean>(resultSet.getInt("userid"), true)
        } else {
            throw Exception("Unable to retrieve the id of the newly inserted user")
        }
    }
}