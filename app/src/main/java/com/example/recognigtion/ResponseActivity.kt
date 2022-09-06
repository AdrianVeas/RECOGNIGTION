package com.example.recognigtion

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView

class ResponseActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var output:  String
    private lateinit var txtresponse: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_response)
        val bun = intent.extras
        txtresponse = findViewById(R.id.txtresponse)
        output = bun?.getString("output").toString()
        txtresponse.text = output
    }

    override fun onClick(v: View?) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}