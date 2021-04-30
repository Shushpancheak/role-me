package com.example.roleme.data

import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import com.example.roleme.LatestMessagesActivity
import com.example.roleme.data.model.LoggedInUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.awaitAll
import java.io.IOException
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks

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

            var user = LoggedInUser("ERROR", "ERROR")

            Tasks.await(
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    Log.d("Main", "***Create user done")
                    if (!it.isSuccessful) throw IOException("Error registering")
                    Log.d("Main", "***Create user success")
                    uid = it.result?.user?.uid ?: error
                }
                .addOnCompleteListener {
                    if (uid == error) throw IOException("Error registering")
                    Log.d("Main", "***Success getting uid=$uid")
                    uid = FirebaseAuth.getInstance().uid.toString()
                    val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
                    user = LoggedInUser(uid, username)
                    ref.setValue(user)
                    Log.d("Main", "***Success setting ref value")
                })

            Log.d("Main", "***Register end")
            Result.Success(user)
        } catch (e: Throwable) {
            Result.Error(IOException("Error registering", e))
        }
    }

    fun logout() {
        // TODO: revoke authentication
    }
}