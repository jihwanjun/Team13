package edu.skku.cs.team13

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val idInput = findViewById<EditText>(R.id.idEdit)
        val passwordInput = findViewById<EditText>(R.id.passwordEdit)

        val loginBtn = findViewById<Button>(R.id.loginButton)

        loginBtn.setOnClickListener {
            val id = idInput.text.toString()
            val password = passwordInput.text.toString()

            if (id.equals("Team13") && password.equals("1234")) {
                startActivity(Intent(applicationContext, SearchActivity::class.java))
            } else {
                Toast.makeText(
                    applicationContext,
                    "Login failed! Check your id and password. (Team13/1234)",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}