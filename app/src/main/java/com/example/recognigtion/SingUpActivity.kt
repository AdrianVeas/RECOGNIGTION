package com.example.recognigtion

import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity


class SingUpActivity : AppCompatActivity() {

    private lateinit var txtnickname: EditText
    private lateinit var btnrecord: ImageView
    private lateinit var btnplay: Button
    private var outputFile : String = Environment.getExternalStorageDirectory().absolutePath
    private var settingsAudio = SettingsAudio(this)
    private lateinit var pbrecord : ProgressBar
    private var staterecord : Int = 0
    private var stateplay : Int = 0
    private var statepb : Int = 0
    private var time = 0.0
    private var timeMax = 15.0
    private lateinit var serviceIntent:Intent
    private var timerStarted: Boolean = false

    private lateinit var txtdetail : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sing_up)
        try {
            txtnickname = findViewById(R.id.txtnickname)
            pbrecord = findViewById(R.id.pbSingup)
            btnrecord = findViewById(R.id.btnRecordSingup)
            btnplay = findViewById(R.id.btnPlayAudioSingup)

            txtdetail = findViewById(R.id.txtdetail)

            pbrecord.progress = 0
            serviceIntent = Intent(applicationContext,TimerService::class.java)
            registerReceiver(updateTime, IntentFilter(TimerService.TIMER_UPDATE))
        }catch (ex: Exception){
            Toast.makeText(applicationContext, "Error Sing Up:"+ex.message, Toast.LENGTH_SHORT).show()
        }
    }
    private fun startStopTimer(){
        if(timerStarted)
        {
            stopTimer()
        }else{
            startTimer()
        }
    }
    private fun resetTimer(){
        stopTimer()
        time = 0.0
        pbrecord.progress = 0
    }
    private fun startTimer() {
        try{
            serviceIntent.putExtra(TimerService.TIME_EXTRA, time)
            startService(serviceIntent)
            timerStarted = true
        }catch (ex : Exception){
            Toast.makeText(applicationContext, "Error Sing Up:"+ex.message, Toast.LENGTH_SHORT).show()
        }
    }
    private fun stopTimer() {
        try{
            stopService(serviceIntent)
            timerStarted = false
        }catch (ex : Exception){
            Toast.makeText(applicationContext, "Error Sing Up:"+ex.message, Toast.LENGTH_SHORT).show()
        }
    }
    private val updateTime: BroadcastReceiver = object : BroadcastReceiver()
    {
        override fun onReceive(context: Context, intent: Intent) {
            time = intent.getDoubleExtra(TimerService.TIME_EXTRA, 0.0)
            try{
                if(time<timeMax){
                    pbrecord.progress = (time/timeMax*100).toInt()
                }else{
                    if (statepb == 0){
                        staterecord = 1
                        recordAudio(null)
                    }
                    else{
                        stateplay = 1
                        playAudio(null)
                        pbrecord.progress = 0
                    }

                }
            }catch (e: Exception){
                Toast.makeText(applicationContext, "Error:"+e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    fun recordAudio(v: View?){
        try {
            when(staterecord){
                0->{  //record
                    if (txtnickname.text.toString()  != ""){
                        chngRecordtoStop()
                        //outputFile = "$outputFile/RECOGNIGTION/USERS/$txtnickname.mp3"
                        //outputFile = "$outputFile/${txtnickname.text}.mp3"
                        outputFile = settingsAudio.startRecording(txtnickname.text.toString(), true)
                        txtdetail.text = outputFile
                        startStopTimer()
                        staterecord = 1
                        statepb = 0
                    }else{
                        Toast.makeText(applicationContext, "Write your name!", Toast.LENGTH_LONG).show()
                    }
                }
                1->{ //stoprecord
                    if (time > 3.0){
                        chngStoptoRecord()
                        settingsAudio.stopRecording()
                        resetTimer()
                        staterecord = 2
                    }else{
                        Toast.makeText(applicationContext, "Please say RECOGNIGTION!!!", Toast.LENGTH_LONG).show()
                    }
                }
                2->{
                    val builder = AlertDialog.Builder(this)
                    builder.setMessage("Are you sure you want to re-record the audio?")
                        .setCancelable(false)
                        .setPositiveButton("Yes") { dialog, id ->
                            staterecord = 0
                            timeMax = 15.0
                            pbrecord.progress = 0
                            recordAudio(null)
                        }
                        .setNegativeButton("No") { dialog, id ->
                            dialog.dismiss()
                        }
                    val alert = builder.create()
                    alert.show()
                }
            }
            v.toString()
        }catch(e:Exception){
            Toast.makeText(applicationContext, "Error:"+e.message, Toast.LENGTH_LONG).show()
        }
    }
    private fun convertInttoTime(timeInt: Int): Double{
        return timeInt/1000+1.0
    }
    fun playAudio(v: View?) {
        try {
            when(stateplay){
                0->{ //playaudio
                    if (staterecord == 2){

                        pbrecord.progress=0
                        chngPlaytoStop()
                        stateplay = 1
                        statepb = 1
                        settingsAudio.playaudio(outputFile)
                        timeMax = convertInttoTime(settingsAudio.getDuration())
                        startStopTimer()
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
                    resetTimer()
                    stateplay = 0
                    settingsAudio.stopaudio()
                }
            }
            v.toString()
        }catch(e:Exception){
            Toast.makeText(applicationContext, "Error:"+e.message, Toast.LENGTH_LONG).show()
        }
    }

    fun save(v: View?){
        try {
            v.toString()
            val intent = Intent(this, ProcessingSingUpActivity::class.java)
            val bundle = Bundle()
            bundle.putString("type", "0")
            bundle.putString("outputFile", outputFile)
            intent.putExtras(bundle)
            startActivity(intent)
        }catch(e:Exception){
            Toast.makeText(applicationContext, "Error:"+e.message, Toast.LENGTH_LONG).show()
        }
    }
    private fun chngRecordtoStop(){
        btnrecord.setImageResource(R.drawable.iconostop)
    }
    private fun chngStoptoRecord(){
        btnrecord.setImageResource(R.drawable.iconorec)
    }
    private fun chngPlaytoStop(){
        try{
            btnplay.text = resources.getString(R.string.stop_audio)
        }catch(e:Exception){
            Toast.makeText(applicationContext, "Error:"+e.message, Toast.LENGTH_LONG).show()
        }
    }
    private fun chngStoptoPlay(){
        try{
            btnplay.text = resources.getString(R.string.play_audio)
        }catch(e:Exception){
            Toast.makeText(applicationContext, "Error:"+e.message, Toast.LENGTH_LONG).show()
        }
    }
}

