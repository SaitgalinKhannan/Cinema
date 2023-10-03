package com.khannan.repository

import com.khannan.model.CinemaUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.Connection
import java.sql.SQLException
import java.sql.Statement


class UserRepository(private val connection: Connection) : UserRepositoryInterface {
    companion object {
        private const val CREATE_TABLE_USER =
            "CREATE TABLE IF NOT EXISTS " +
                    "CinemaUser (userId INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY, last_name VARCHAR(50), " +
                    "first_name VARCHAR(50), middle_name VARCHAR(50) NULL, email VARCHAR(100) UNIQUE, password VARCHAR(100))"
        private const val INSERT_USER =
            "INSERT INTO cinemauser (last_name, first_name, middle_name, email, password) VALUES (?, ?, ?, ?, ?)"
        private const val LOGIN_USER =
            "SELECT * FROM cinemauser WHERE email = ? AND password = ?"
        private const val USER_DATA_BY_ID =
            "SELECT * FROM CinemaUser WHERE userId = ?"
        private const val UPDATE_USER_DATA_BY_ID =
            "UPDATE CinemaUser SET last_name = ?, first_name = ?, middle_name = ?, email = ?, password = ? WHERE userId = ?"
    }

    init {
        try {
            connection.createStatement().use { statement ->
                statement.executeUpdate(CREATE_TABLE_USER)
            }
        } catch (e: SQLException) {
            println(e.message)
        }
    }

    override suspend fun updateUserData(cinemaUser: CinemaUser): Boolean = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(UPDATE_USER_DATA_BY_ID)
        statement.setString(1, cinemaUser.lastName)
        statement.setString(2, cinemaUser.firstName)
        statement.setString(3, cinemaUser.middleName)
        statement.setString(4, cinemaUser.email)
        statement.setString(5, cinemaUser.password)
        statement.setInt(6, cinemaUser.id)
        val result = statement.executeUpdate()

        if (result == 1) {
            return@withContext true
        } else {
            throw Exception("Unable to update")
        }
    }

    override suspend fun registerUser(cinemaUser: CinemaUser): Boolean = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(INSERT_USER, Statement.RETURN_GENERATED_KEYS)
        statement.setString(1, cinemaUser.lastName)
        statement.setString(2, cinemaUser.firstName)
        statement.setString(3, cinemaUser.middleName)
        statement.setString(4, cinemaUser.email)
        statement.setString(5, cinemaUser.password)
        statement.executeUpdate()

        val generatedKeys = statement.generatedKeys
        if (generatedKeys.next()) {
            return@withContext true
        } else {
            throw Exception("Unable to retrieve the id of the newly inserted user")
        }
    }

    override suspend fun loginUser(email: String, password: String): Pair<Int, Boolean> = withContext(Dispatchers.IO) {
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

    override suspend fun userData(id: Int): CinemaUser = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(USER_DATA_BY_ID)
        statement.setInt(1, id)
        val result = statement.executeQuery()

        if (result.next()) {
            val lastName = result.getString("last_name")
            val firstName = result.getString("first_name")
            val middleName = result.getString("middle_name")
            val email = result.getString("email")
            val password = result.getString("password")
            return@withContext CinemaUser(id, lastName, firstName, middleName, email, password)
        } else {
            throw Exception("User not found")
        }
    }
}