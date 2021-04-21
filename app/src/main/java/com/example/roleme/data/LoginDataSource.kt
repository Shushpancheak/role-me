package com.example.roleme.data

import android.util.Log
import com.example.roleme.data.model.LoggedInUser
import com.google.firebase.auth.FirebaseAuth
import java.io.IOException

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource {

    fun login(username: String, password: String): Result<LoggedInUser> {
        return try {
            // TODO: handle loggedInUser authentication
            val fakeUser = LoggedInUser(java.util.UUID.randomUUID().toString(), "Jane Doe")
            Result.Success(fakeUser)
        } catch (e: Throwable) {
            Result.Error(IOException("Error logging in", e))
        }
    }

    fun register(email: String, username: String, password: String): Result<LoggedInUser> {
        return try {
            // TODO: handle loggedInUser authentication
            val error = "ERROR"
            var uid = error
            Log.d("Main", "***uid == ${uid}")

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (!it.isSuccessful) return@addOnCompleteListener
                    Log.d("Main", "***it == ${it.result?.user?.uid}")
                    uid = it.result?.user?.uid ?: error
                }

            Log.d("Main", "***uid pre error == ${uid}")
            if (uid == error) throw error("Error registering")


            Log.d("Main", "***uid post error == ${uid}")
            val user = LoggedInUser(uid, username)

            Result.Success(user)
        } catch (e: Throwable) {
            Result.Error(IOException("Error registering", e))
        }
    }

    fun logout() {
        // TODO: revoke authentication
    }
}