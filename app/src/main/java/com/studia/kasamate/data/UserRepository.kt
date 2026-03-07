package com.studia.kasamate.data

import kotlinx.coroutines.flow.Flow

class UserRepository(private val userDao: UserDao, private val transactionDao: TransactionDao) {

    suspend fun addUser(username: String, passwordHash: String): Boolean {
        val user = User(username = username, passwordHash = passwordHash)
        return userDao.insertUser(user) != -1L
    }

    suspend fun getUser(username: String): String? {
        return userDao.getUserByUsername(username)?.passwordHash
    }

    fun getAllUsers(): Flow<List<User>> {
        return userDao.getAllUsers()
    }

    suspend fun deleteUser(username: String) {
        userDao.getUserByUsername(username)?.let {
            // Transaction deletion is handled by SQLite CASCADE
            userDao.deleteUser(it)
        }
    }

    suspend fun updatePassword(username: String, newPasswordHash: String) {
        userDao.getUserByUsername(username)?.let {
            val updatedUser = it.copy(passwordHash = newPasswordHash)
            userDao.updateUser(updatedUser)
        }
    }
}
