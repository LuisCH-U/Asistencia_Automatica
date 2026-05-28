package com.pr_asistencia.asistencia_auto.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.pr_asistencia.asistencia_auto.HomeActivity
import com.pr_asistencia.asistencia_auto.R
import com.pr_asistencia.asistencia_auto.App
import com.pr_asistencia.asistencia_auto.models.LoginRequest
import com.pr_asistencia.asistencia_auto.network.RetrofitClient
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var etTenant: EditText
    private lateinit var etUsuario: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        etTenant = findViewById(R.id.etTenant)
        etUsuario = findViewById(R.id.etUsuario)
        etPassword = findViewById(R.id.etPassword)

        btnLogin = findViewById(R.id.btnLogin)

        verificarSesion()

        btnLogin.setOnClickListener {
            login()
        }
    }

    private fun verificarSesion() {

        val prefs = App.instance.securePrefs()
        val token = prefs.getString("token", null)

        if (token != null) {
            startActivity(
                Intent(this, HomeActivity::class.java)
            )
            finish()
        }
    }

    private fun login() {

        lifecycleScope.launch {

            try {

                if (etTenant.text.isEmpty() || etUsuario.text.isEmpty() || etPassword.text.isEmpty()) {
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
                            userNameOrEmailAddress = etUsuario.text.toString(),
                            password = etPassword.text.toString(),
                            rememberClient = false
                        )
                    )

                if (response.isSuccessful) {

                    val token = response.body()?.result?.accessToken

                    guardarSesion(token!!)

                    Toast.makeText(
                        this@LoginActivity,
                        "Login correcto",
                        Toast.LENGTH_LONG
                    ).show()

                    startActivity(
                        Intent(
                            this@LoginActivity,
                            HomeActivity::class.java
                        )
                    )

                    finish()

                } else {

                    Toast.makeText(
                        this@LoginActivity,
                        "Credenciales incorrectas",
                        Toast.LENGTH_LONG
                    ).show()
                }

            } catch (e: Exception) {

                Toast.makeText(
                    this@LoginActivity,
                    e.message,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun guardarSesion(token: String)
    {

        val prefs = App.instance.securePrefs()

        prefs.edit()
            .putString(
                "tenant",
                etTenant.text.toString().ifBlank { "inlearning" }
            )
            .putString(
                "token",
                token
            )
            .putString(
                "usuario",
                etUsuario.text.toString()
            )
            .putString(
                "user",
                etUsuario.text.toString()
            )
            .putString(
                "password",
                etPassword.text.toString()
            )
            .apply()
    }
}