package com.example.recognigtion

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Button

class RecognizeActivity : AppCompatActivity() {
    val SettingsAudio = SettingsAudio(this)
    private var staterecognize = 0
    private lateinit var btnplayaudio: Button
    private var outputFile : String = Environment.getExternalStorageDirectory().absolutePath
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recognize)
        try {
            btnplayaudio = findViewById(R.id.btnPlayAudio)
        }catch (e : Exception){

        }
    }
    fun RecordAudio(v: View?){

    }

    fun Recognize(v: View?) {
        try{
            val intent = Intent(this, ResponseActivity::class.java)
            startActivity(intent)
        }catch (e : Exception){

        }

    }
    fun PlayAudio(v: View?){
        try {
            when(staterecognize){
                1 ->{
                    SettingsAudio.playaudio(outputFile)
                }
                2 ->{
                    SettingsAudio.stopaudio()

                }

            }
        }catch (e : Exception){

        }
    }
}