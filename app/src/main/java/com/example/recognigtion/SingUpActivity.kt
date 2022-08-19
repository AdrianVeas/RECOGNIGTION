package com.example.recognigtion

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class SingUpActivity : AppCompatActivity(), View.OnClickListener {
    private var Nickname: String = ""
    private lateinit var btnrecog: Button
    private var outputFile : String = Environment.getExternalStorageDirectory().absolutePath

    private lateinit var timer : ProgressBar

    private var statebtnrecog : Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sing_up)


        val settingsaudio = SettingsAudio(applicationContext)

        val bun = intent.extras
        try {
            Nickname = bun?.getString("nickname").toString()
            outputFile = "$outputFile/$Nickname.mp3"

            timer = findViewById(R.id.ProgressBar)

            btnrecog = findViewById(R.id.btnrecfirst)
            btnrecog.setOnClickListener {
                statebtnrecog = if (statebtnrecog == 0) {
                    settingsaudio.recordaudio(outputFile)
                    1
                } else {
                    if (statebtnrecog == 1) {
                        //stoprecord()
                        2
                    } else {
                        if (statebtnrecog == 2){
                            //playaudio()
                            3
                        }else{
                            //stopaudio()
                            2
                        }
                    }
                }
            }
        }catch (ex: Exception){
            Toast.makeText(applicationContext, "Error Sing Up:"+ex.message, Toast.LENGTH_SHORT).show()
        }
    }


    override fun onClick(v: View?) {

        val intent = Intent(this, RecognigtionActivity::class.java)
        val bundle = Bundle()

        bundle.putString("nickname", Nickname)
        intent.putExtras(bundle)
        startActivity(intent)
    }

    fun playaudio(v: View?) {

    }
}

