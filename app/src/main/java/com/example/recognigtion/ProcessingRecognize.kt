package com.example.recognigtion

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.tensorflow.lite.task.audio.classifier.AudioClassifier
import org.tensorflow.lite.task.audio.classifier.AudioClassifier.AudioClassifierOptions
import org.tensorflow.lite.task.audio.classifier.Classifications
import org.tensorflow.lite.task.core.BaseOptions


class ProcessingRecognize : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_processing_recognize)


        val options = AudioClassifierOptions.builder()
            .setBaseOptions(BaseOptions.builder().useGpu().build())
            .setMaxResults(1)
            .build()
        val classifier = AudioClassifier.createFromFileAndOptions(this, MODEL_FILE, options)

        val record = classifier.createAudioRecord()
        record.startRecording()
        val audioTensor = classifier.createInputTensorAudio()
        audioTensor.load(record)
        val results: List<Classifications> = audioClassifier.classify(audioTensor)

    }

    companion object {
        private const val MODEL_FILE = "recognigtion.tflite"
    }
}