package com.pr_asistencia.asistencia_auto.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.pr_asistencia.asistencia_auto.R
import com.pr_asistencia.asistencia_auto.App
import com.pr_asistencia.asistencia_auto.models.LoginRequest
import com.pr_asistencia.asistencia_auto.network.RetrofitClient
import kotlinx.coroutines.launch
import androidx.core.content.edit

class LoginActivity : AppCompatActivity() {

    private lateinit var etTenant: EditText
    private lateinit var etUser: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etTenant = findViewById(R.id.etTenant)
        etUser = findViewById(R.id.etUser)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)

        animarLogo()

        verificarSesion()

        btnLogin.setOnClickListener {
            login()
        }
    }

    private fun animarLogo()
    {
        val logo = findViewById<android.widget.ImageView>(R.id.ivLogo)
        logo.alpha = 0f
        logo.scaleX = 0.7f
        logo.scaleY = 0.7f
        logo.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(400)
            .start()
    }

    private fun verificarSesion()
    {

        val prefs = App.instance.securePrefs()
        val token = prefs.getString("token", null)

        if (token != null) {
            startActivity(
                Intent(this, MainActivity::class.java)
            )
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }
    }

    private fun login()
    {

        lifecycleScope.launch {

            try {

                if (etTenant.text.isEmpty() || etUser.text.isEmpty() || etPassword.text.isEmpty()) {
                    Toast.makeText(
                        this@LoginActivity,
                        "Por favor complete todos los campos",
                        Toast.LENGTH_LONG
                    ).show()
                    return@launch
                }
                val response = RetrofitClient
                    .api
                    .login(
                        LoginRequest( tenantName = etTenant.text.toString(),
                            userNameOrEmailAddress = etUser.text.toString(),
                            password = etPassword.text.toString(),
                            rememberClient = false
                        )
                    )

                if (response.isSuccessful) {

                    val token = response.body()?.result?.accessToken
                    if (token.isNullOrBlank()) {
                        Toast.makeText(this@LoginActivity, "Respuesta inválida del servidor", Toast.LENGTH_LONG).show()
                        return@launch
                    }
                    guardarSesion(token)

                    Toast.makeText(this@LoginActivity, "Login correcto", Toast.LENGTH_LONG).show()

                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))

                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)

                    finish()

                } else {
                    Toast.makeText(this@LoginActivity, "Credenciales incorrectas", Toast.LENGTH_LONG).show()
                }

            } catch (e: Exception) {
                Toast.makeText(this@LoginActivity, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun guardarSesion(token: String)
    {

        val prefs = App.instance.securePrefs()

        prefs.edit {
            putString(
                "tenant",
                etTenant.text.toString().ifBlank { "inlearning" }
            ).
            putString(
                "token",
                token
            ).
            putString(
                "user",
                etUser.text.toString()
            ).
            putString(
                "password",
                etPassword.text.toString()
            )
        }
    }
}