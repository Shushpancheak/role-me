package com.example.roleme.ui.login

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import com.example.roleme.LatestMessagesActivity

import com.example.roleme.R
import com.example.roleme.data.model.LoggedInUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.io.IOException

class RegisterActivity : AppCompatActivity() {

    private lateinit var registerViewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val username = findViewById<EditText>(R.id.name_reg)
        val email = findViewById<EditText>(R.id.email_reg)
        val password = findViewById<EditText>(R.id.password_reg)
        val register = findViewById<Button>(R.id.register)
        val loading = findViewById<ProgressBar>(R.id.loading)

        val alreadyAcc = findViewById<TextView>(R.id.already_have_account)

        alreadyAcc.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        registerViewModel = ViewModelProvider(this, RegisterViewModelFactory())
            .get(RegisterViewModel::class.java)

        registerViewModel.registerFormState.observe(this@RegisterActivity, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            register.isEnabled = loginState.isDataValid

            if (loginState.usernameError != null) {
                username.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
               password.error = getString(loginState.passwordError)
            }
        })

        username.afterTextChanged {
            registerViewModel.registerDataChanged(
                email.text.toString(),
                username.text.toString(),
                password.text.toString()
            )
        }

        register.setOnClickListener {
            loading.visibility = View.VISIBLE

            val error_uid = "ERROR"
            var uid = error_uid
            var user: LoggedInUser
            val database_str = getString(R.string.database_url)
            try {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email.text.toString(), password.text.toString())
                        .addOnCompleteListener {
                            if (!it.isSuccessful) throw IOException("Error registering")
                            uid = FirebaseAuth.getInstance().uid.toString()
                            val ref = FirebaseDatabase.getInstance(database_str).getReference("/users/$uid")
                            user = LoggedInUser(uid, username.text.toString())
                            ref.setValue(user)
                        }
                        .addOnCompleteListener {
                            loading.visibility = View.GONE

                            updateUiWithUser(LoggedInUserView(username.text.toString()))
                            setResult(Activity.RESULT_OK)
                            val intent = Intent(this, LatestMessagesActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
            }
            catch(e: Throwable) {
                loading.visibility = View.GONE
                showLoginFailed(R.string.error_registering)
            }
        }

        password.apply {
            afterTextChanged {
                registerViewModel.registerDataChanged(
                    email.text.toString(),
                    username.text.toString(),
                    password.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        registerViewModel.register(
                            email.text.toString(),
                            username.text.toString(),
                            password.text.toString()
                        )
                }
                false
            }
        }
    }

    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome: String = getString(R.string.welcome)
        val displayName = model.displayName
        // TODO : initiate successful logged in experience
        Toast.makeText(
            applicationContext,
            "$welcome $displayName",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }
}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}