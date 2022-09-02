package com.example.recognigtion

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.Toast

class ProcessingSingUpActivity : AppCompatActivity() {
    private lateinit var outputFile : String
    private lateinit var type : String
    private lateinit var pbProgress : ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_processing_sing_up)
        try {
            val bundle = intent.extras
            type = bundle?.getString("type").toString()
            outputFile = bundle?.getString("outputFile").toString()

            pbProgress = findViewById(R.id.pbProgress)
            pbProgress.progress = 0
        }catch (e: Exception){
            Toast.makeText(applicationContext, "Error:"+e.message, Toast.LENGTH_LONG).show()
        }
    }
}