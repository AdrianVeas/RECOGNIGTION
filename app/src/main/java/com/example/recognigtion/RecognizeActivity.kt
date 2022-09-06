package com.example.recognigtion

import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.*

class RecognizeActivity : AppCompatActivity() {

    val settingsAudio = SettingsAudio(this)
    private lateinit var btnrecord: ImageView
    private lateinit var btnplay: Button
    private var outputFile: String = Environment.getExternalStorageDirectory().absolutePath
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
        setContentView(R.layout.activity_recognize)
        try {
            pbrecord = findViewById(R.id.pbRecognize)
            btnrecord = findViewById(R.id.btnRecord)
            btnplay = findViewById(R.id.btnPlayAudio)
            pbrecord.progress = 0
            serviceIntent = Intent(applicationContext,TimerService::class.java)
            txtdetail = findViewById(R.id.txtdetailrecog)
            registerReceiver(updateTime, IntentFilter(TimerService.TIMER_UPDATE))
        } catch (e: Exception) {
            Toast.makeText(applicationContext, "Error:"+e.message, Toast.LENGTH_LONG).show()
        }
    }
    fun recordAudio(v: View?) {
        try {
            when(staterecord){
                0->{ //record
                    //outputFile = "$outputFile/record.wav"
                    outputFile = settingsAudio.startRecording("record", true)
                    //settingsAudio.startRecording(outputFile)
                    txtdetail.text = outputFile
                    startStopTimer()
                    chngRecordtoStop()
                    staterecord = 1
                    statepb = 0
                }
                1->{ //stoprecord
                    if (time > 3.0){
                        chngStoptoRecord()
                        settingsAudio.stopRecording()
                        resetTimer()
                        staterecord = 2
                    }else{
                        Toast.makeText(applicationContext, "Please say RECOGNIGTION!!!", Toast.LENGTH_LONG).show()
                        staterecord = 1
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
    fun recognize(v: View?) {
        try {
            if (staterecord == 2){
                val intent = Intent(this, ProcessingRecognize::class.java)
                val bundle = Bundle()
                bundle.putString("type", "1")
                bundle.putString("outputFile", outputFile)
                intent.putExtras(bundle)
                startActivity(intent)
            }else{
                if(staterecord == 1){
                    Toast.makeText(applicationContext, "Stop record first", Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(applicationContext, "Record audio first", Toast.LENGTH_LONG).show()
                }
            }
        } catch (e: Exception) {
            Toast.makeText(applicationContext, "Error:"+e.message, Toast.LENGTH_LONG).show()
        }
        v.toString()
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
                    settingsAudio.stopaudio()
                    resetTimer()
                    chngStoptoPlay()
                    stateplay = 0
                }
            }
            v.toString()
        }catch(e:Exception){
            Toast.makeText(applicationContext, "Error:"+e.message, Toast.LENGTH_LONG).show()
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
            Toast.makeText(applicationContext, "Error:"+ex.message, Toast.LENGTH_SHORT).show()
        }
    }
    private fun stopTimer() {
        try{
            stopService(serviceIntent)
            timerStarted = false
        }catch (ex : Exception){
            Toast.makeText(applicationContext, "Error:"+ex.message, Toast.LENGTH_SHORT).show()
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
                    }else{
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