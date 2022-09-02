package com.example.recognigtion

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView

class ResponseActivity : AppCompatActivity(), View.OnClickListener {
    var Nickname: String = ""
    private lateinit var txtresponse: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_response)
        val bun = intent.extras
        Nickname = bun?.getString("nickname").toString()
        txtresponse = findViewById(R.id.txtresponse)

    }

    override fun onClick(v: View?) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        txtresponse.text = "${txtresponse.text},$Nickname"
    }
}