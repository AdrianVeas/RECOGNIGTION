package com.example.recognigtion

import android.content.Intent
import android.os.Bundle
import android.widget.ProgressBar
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
import kotlin.concurrent.scheduleAtFixedRate


class ProcessingSingUpActivity : AppCompatActivity() {
    private lateinit var outputFile : String
    private lateinit var type : String
    private lateinit var pbProgress : ProgressBar
    private lateinit var file : File
    var count :Int = 1
    private lateinit var audiofloata: FloatArray
    var probabilityThreshold: Float = 0.3f
    private lateinit var outputStr: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_processing_sing_up)
        try {
            val bun = intent.extras
            type = bun?.getString("type").toString()
            outputFile = bun?.getString("outputFile").toString()

            file = File(outputFile)

            audiofloata = AudioConverter.readAudioSimple(file)

            pbProgress = findViewById(R.id.pbProgress)
            pbProgress.progress = 1
            val intent = Intent(this, ConfirmationSingUp::class.java)
            val bundle = Bundle()

            val modelPath = "recognigtion.tflite"
            val classifier = AudioClassifier.createFromFile(this, modelPath)
            val tensor = classifier.createInputTensorAudio()


            Timer().scheduleAtFixedRate(1, 100) {


                val numberOfSamples = tensor.load(audiofloata)
                val output = classifier.classify(tensor)

                val filteredModelOutput = output[0].categories.filter {
                    it.score > probabilityThreshold
                }

                outputStr =
                    filteredModelOutput.sortedBy { -it.score }
                        .joinToString(separator = "\n") { "${it.label} -> ${it.score} " }

                if (outputStr.isNotEmpty())
                    runOnUiThread {
                        pbProgress.progress =  100
                        if(pbProgress.progress== 100){

                            bundle.putString("output", outputStr)

                            intent.putExtras(bundle)
                            startActivity(intent)
                        }
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
            return floatMe(shortMe(buff.sliceArray(buff.indices)) ?: ShortArray(0)) ?: FloatArray(
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

}
