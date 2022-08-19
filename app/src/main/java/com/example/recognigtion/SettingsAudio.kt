package com.example.recognigtion

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.widget.Toast

class SettingsAudio (Cont : Context) {
    var firstRecog : MediaRecorder = MediaRecorder()
    var player : MediaPlayer = MediaPlayer()
    var timerecord : String = "0:00"
    var context : Context
    fun recordaudio(outputFile : String){
        try {
            counttime()
            firstRecog.setAudioSource(MediaRecorder.AudioSource.MIC)
            firstRecog.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            firstRecog.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            firstRecog.setOutputFile(outputFile)
            firstRecog.prepare()
            firstRecog.start()
        }catch (ex: Exception){
            Toast.makeText(context, "Error Record:"+ex.message, Toast.LENGTH_SHORT).show()
        }
    }

    fun counttime(){
        try{
        }catch (ex: Exception){
            Toast.makeText(context, "Error Count:"+ex.message, Toast.LENGTH_SHORT).show()
        }
    }

    fun stoprecord(){
        try {
            firstRecog.stop()
            firstRecog.release()
        }catch (ex: Exception){
            Toast.makeText(context, "Error Stop:"+ex.message, Toast.LENGTH_SHORT).show()
        }
    }

    fun playaudio(outputFile : String){
        try{
            player.setDataSource(outputFile)
            player.prepare()
            player.duration

            Toast.makeText(context, ""+player.duration +" - "+ timerecord, Toast.LENGTH_SHORT).show()
            player.start()
            verifytimerplayer()

        }catch (ex: Exception){
            Toast.makeText(context, "Error Play Audio:"+ex.message, Toast.LENGTH_SHORT).show()
        }

    }
    fun verifytimerplayer() {

    }
    fun stopaudio(){
        try{
            player.stop()
            player.reset()
        }catch (ex: Exception){
            Toast.makeText(context, "Error Stop Audio:"+ex.message, Toast.LENGTH_SHORT).show()
        }
    }
    init{
        context = Cont
    }
}