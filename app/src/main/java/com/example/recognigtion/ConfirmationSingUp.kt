package com.example.recognigtion

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView

class ConfirmationSingUp : AppCompatActivity() , View.OnClickListener{
    private lateinit var output:  String
    private lateinit var txtresponse: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirmation_sing_up)
        val bun = intent.extras
        txtresponse = findViewById(R.id.txtresponseconfirmation)
        output = bun?.getString("output").toString()
        txtresponse.text = "USER SAVED"
    }
    override fun onClick(v: View?) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}