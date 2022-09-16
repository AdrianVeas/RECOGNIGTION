package com.example.recognigtion

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.tensorflow.lite.task.audio.classifier.AudioClassifier
import java.io.BufferedInputStream
import java.io.DataInputStream
import java.io.File
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.scheduleAtFixedRate


class ProcessingSingUpActivity : AppCompatActivity() {
    private lateinit var outputFile : String
    private lateinit var type : String
    private lateinit var pbProgress : ProgressBar
    private lateinit var file : File
    private var count :Int = 0
    private lateinit var audiofloata: FloatArray
    var probabilityThreshold: Float = 0.3f
    private lateinit var outputStr: String
    private var confirmation : Boolean = false

    private var time = 0.0
    private var timeMax = 10.0
    private lateinit var serviceIntent:Intent
    private var timerStarted: Boolean = false
    private var state: Int = 0
    val bundle = Bundle()
    private lateinit var nickname: String
    private lateinit var txtdetail : TextView

    private var statepoint = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_processing_sing_up)
        try {
            val bun = intent.extras
            type = bun?.getString("type").toString()
            outputFile = bun?.getString("outputFile").toString()

            file = File(outputFile)

            txtdetail = findViewById(R.id.txtdetail)

            audiofloata = AudioConverter.readAudioSimple(file)

            pbProgress = findViewById(R.id.pbProgress)
            pbProgress.progress = 1

            serviceIntent = Intent(applicationContext,TimerService::class.java)
            registerReceiver(updateTime, IntentFilter(TimerService.TIMER_UPDATE))

            startStopTimer()

            //val intent = Intent(this, ConfirmationSingUp::class.java)

            val classifier = AudioClassifier.createFromFile(this, MODEL_FILE)

            val tensor = classifier.createInputTensorAudio()



            Timer().scheduleAtFixedRate(1, 100) {


                // val numberOfSamples = tensor.load(audiofloata)
                val output = classifier.classify(tensor)


                val filteredModelOutput = output[0].categories.filter {
                    it.score > probabilityThreshold
                }

                outputStr =
                    filteredModelOutput.sortedBy { -it.score }
                        .joinToString(separator = "\n") { "${it.label} -> ${it.score}" }

                if (outputStr.isNotEmpty())
                    runOnUiThread {

                    }
            }

        }catch (e: Exception){
            Toast.makeText(applicationContext, "Error:"+e.message, Toast.LENGTH_LONG).show()
        }
    }
    object AudioConverter {
        fun readAudioSimple(path: File): FloatArray {
            val input =
                BufferedInputStream(FileInputStream(path))
            val buff = ByteArray(path.length().toInt())
            val dis = DataInputStream(input)
            dis.readFully(buff)
            // remove wav header at first 44 bytes
            return floatMe(shortMe(buff.sliceArray(buff.indices)) ) ?: FloatArray(
                0
            )
        }

        fun FloatArray.sliceTo(step: Int): List<FloatArray> {
            val slicedAudio = arrayListOf<FloatArray>()
            var startAt = 0
            var endAt = 15600
            val stepSize = if (step != 0) (15600 * (1f / (2 * step))).toInt() else 0
            while ((startAt + 15600) < this.size) {
                if (startAt != 0) {
                    startAt = endAt - stepSize
                    endAt = startAt + 15600
                }
                slicedAudio.add(this.copyOfRange(startAt, endAt))
                startAt = endAt
            }
            return slicedAudio
        }

        private fun shortMe(bytes: ByteArray): ShortArray {
            val out = ShortArray(bytes.size / 2)
            ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(out)
            return out
        }

        private fun floatMe(pcms: ShortArray): FloatArray {
            val floats = FloatArray(pcms.size)
            pcms.forEachIndexed { index, sh ->
                // The input must be normalized to floats between -1 and +1.
                // To normalize it, we just need to divide all the values by 2**16 or in our code, MAX_ABS_INT16 = 32768
                floats[index] = sh.toFloat() / 32768.0f

            }
            return floats
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
        pbProgress.progress = 0
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
                if(time<=timeMax){
                    pbProgress.progress = (time/timeMax*100).toInt()
                    if (statepoint == 0){
                        txtdetail.text = "${lstdetails[count]}."
                        statepoint = 1
                    }else{
                        if (statepoint ==1){
                            txtdetail.text = "${lstdetails[count]}.."
                            statepoint = 2
                        }else{
                            txtdetail.text = "${lstdetails[count]}..."
                            statepoint = 0
                            if (count<3)
                                count++
                            else
                                state=1
                        }
                    }
                }else{
                        val int = Intent(applicationContext, ConfirmationSingUp::class.java)
                        bundle.putString("output", outputStr)
                        int.putExtras(bundle)
                        startActivity(int)
                }
            }catch (e: Exception){
                Toast.makeText(applicationContext, "Error:"+e.message, Toast.LENGTH_LONG).show()
            }
        }
    }
    companion object{
        private const val MODEL_FILE = "soundclassifier_with_metadata.tflite"
        private val lstdetails = arrayOf(
            "Decomposing Audio", "Converting Audio to Numbers",
            "Sending the Model", "Saving to the Model")
    }

}
