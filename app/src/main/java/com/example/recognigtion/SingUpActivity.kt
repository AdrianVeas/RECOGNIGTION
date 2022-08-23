package com.example.recognigtion

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat


class SingUpActivity : AppCompatActivity() {
    private lateinit var txtnickname: EditText
    private lateinit var btnrecord: ImageView
    private lateinit var btnplay: Button
    private var outputFile : String = Environment.getExternalStorageDirectory().absolutePath
    private var SettingsAudio = SettingsAudio(this)
    private lateinit var pbrecord : ProgressBar

    private var staterecord : Int = 0
    private var stateplay : Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sing_up)
        try {
            txtnickname = findViewById(R.id.txtnickname)
            pbrecord = findViewById(R.id.pbSingup)

            btnrecord = findViewById(R.id.btnRecordSingup)
            btnplay = findViewById(R.id.btnPlayAudioSingup)

        }catch (ex: Exception){
            Toast.makeText(applicationContext, "Error Sing Up:"+ex.message, Toast.LENGTH_SHORT).show()
        }
    }

    fun RecordAudio(v: View?){
        try {
            when(staterecord){
                0->{  //record
                    if (txtnickname.text.toString()  != ""){
                        staterecord = 1
                        chngRecordtoStop()
                        outputFile = "$outputFile/$txtnickname.mp3"
                        SettingsAudio.recordaudio(outputFile)
                    }else{
                        Toast.makeText(applicationContext, "Write your name!", Toast.LENGTH_LONG).show()
                    }
                }
                1->{ //stoprecord
                    staterecord = 2
                    chngStoptoRecord()
                    SettingsAudio.stoprecord()
                }
                2->{
                    val builder = AlertDialog.Builder(this)
                    builder.setMessage("Are you sure you want to re-record the audio?")
                        .setCancelable(false)
                        .setPositiveButton("Yes") { dialog, id ->
                            staterecord = 0
                            RecordAudio(null)
                        }
                        .setNegativeButton("No") { dialog, id ->
                            // Dismiss the dialog
                            dialog.dismiss()
                        }
                    val alert = builder.create()
                    alert.show()
                }
            }



        }catch(e:Exception){
            Toast.makeText(applicationContext, "Error:"+e.message, Toast.LENGTH_LONG).show()
        }
    }

    fun PlayAudio(v: View?) {
        try {
            when(stateplay){
                0->{ //playaudio
                    if (staterecord == 2){
                        chngPlaytoStop()
                        Toast.makeText(applicationContext, "PlayAudio", Toast.LENGTH_LONG).show()
                        stateplay = 1
                        SettingsAudio.playaudio(outputFile)
                    }else{
                        if(staterecord == 1){
                            Toast.makeText(applicationContext, "Stop record first", Toast.LENGTH_LONG).show()
                        }else{
                            Toast.makeText(applicationContext, "Record audio first", Toast.LENGTH_LONG).show()
                        }

                    }
                }
                1->{ //stopaudio
                    chngStoptoPlay()
                    stateplay = 0
                    Toast.makeText(applicationContext, "StopAudio", Toast.LENGTH_LONG).show()
                    SettingsAudio.stopaudio()

                }
            }
        }catch(e:Exception){
            Toast.makeText(applicationContext, "Error:"+e.message, Toast.LENGTH_LONG).show()
        }
    }

    fun Save(v: View?){
        try {
            val intent = Intent(this, RecognizeActivity::class.java)

            startActivity(intent)
        }catch(e:Exception){
            Toast.makeText(applicationContext, "Error:"+e.message, Toast.LENGTH_LONG).show()
        }

    }
    fun chngRecordtoStop(){
        btnrecord.setImageResource(R.drawable.iconostop)
    }
    fun chngStoptoRecord(){
        btnrecord.setImageResource(R.drawable.iconorec)
    }
    fun chngPlaytoStop(){
        try{
            btnplay.text = resources.getString(R.string.stop_audio)

        }catch(e:Exception){
            Toast.makeText(applicationContext, "Error:"+e.message, Toast.LENGTH_LONG).show()
        }

    }
    fun chngStoptoPlay(){
        try{
            btnplay.text = resources.getString(R.string.play_audio)
        }catch(e:Exception){
            Toast.makeText(applicationContext, "Error:"+e.message, Toast.LENGTH_LONG).show()
        }

    }
}

