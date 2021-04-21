package com.example.roleme.ui.login

/**
 * Authentication result : success (user details) or error message.
 */
data class RegisterResult (
     val success:LoggedInUserView? = null,
     val error:Int? = null
)