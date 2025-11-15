
package com.studia.kasamate.data

class UserRepository {
    private val users = mutableMapOf<String, String>()

    fun addUser(username: String, password: String): Boolean {
        if (users.containsKey(username)) {
            return false
        }
        users[username] = password
        return true
    }

    fun getUser(username: String): String? {
        return users[username]
    }
}
