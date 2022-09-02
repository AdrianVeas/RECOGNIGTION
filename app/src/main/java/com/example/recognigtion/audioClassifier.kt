package com.example.recognigtion

import android.content.Context
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.ParcelFileDescriptor
import org.tensorflow.lite.support.audio.TensorAudio
import org.tensorflow.lite.support.audio.TensorAudio.TensorAudioFormat
import org.tensorflow.lite.task.audio.classifier.AudioClassifier
import org.tensorflow.lite.task.audio.classifier.AudioClassifier.*
import org.tensorflow.lite.task.audio.classifier.Classifications
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.core.TaskJniUtils
import java.io.File
import java.io.IOException
import java.nio.ByteBuffer


class audioClassifier(cont: Context){
    private val AUDIO_CLASSIFIER_NATIVE_LIB: String = "task_audio_jni"
    private val OPTIONAL_FD_LENGTH: Int = -1
    private val OPTIONAL_FD_OFFSET: Int = -1

    @Throws(IOException::class)
    fun createFromFile(context: Context?, modelPath: String?): AudioClassifier? {
        return createFromFileAndOptions(
            context,
            modelPath,
            AudioClassifierOptions.builder().build()
        )
    }

    @Throws(IOException::class)
    fun createFromFile(modelFile: File?): AudioClassifier? {
        return createFromFileAndOptions(modelFile, AudioClassifierOptions.builder().build())
    }
    fun createFromBuffer(modelBuffer: ByteBuffer?): AudioClassifier? {
        return createFromBufferAndOptions(modelBuffer, AudioClassifierOptions.builder().build())
    }

    @Throws(IOException::class)
    fun createFromFileAndOptions(
        context: Context?, modelPath: String?, options: AudioClassifierOptions
    ): AudioClassifier? {
        return AudioClassifier(
            TaskJniUtils.createHandleFromFdAndOptions(
                context,
                { fileDescriptor, fileDescriptorLength, fileDescriptorOffset, options ->
                    initJniWithModelFdAndOptions(
                        fileDescriptor,
                        fileDescriptorLength,
                        fileDescriptorOffset,
                        options,
                        TaskJniUtils.createProtoBaseOptionsHandle(options.baseOptions)
                    )
                },
                AUDIO_CLASSIFIER_NATIVE_LIB,
                modelPath,
                options
            )
        )
    }

    @Throws(IOException::class)
    fun createFromFileAndOptions(
        modelFile: File?, options: AudioClassifierOptions
    ): AudioClassifier? {
        ParcelFileDescriptor.open(modelFile, ParcelFileDescriptor.MODE_READ_ONLY)
            .use { descriptor ->
                return AudioClassifier(
                    TaskJniUtils.createHandleFromLibrary(
                        {
                            initJniWithModelFdAndOptions(
                                descriptor.fd,  /*fileDescriptorLength=*/
                                OPTIONAL_FD_LENGTH.toLong(),  /*fileDescriptorOffset=*/
                                OPTIONAL_FD_OFFSET.toLong(),
                                options,
                                TaskJniUtils.createProtoBaseOptionsHandle(options.baseOptions)
                            )
                        },
                        AUDIO_CLASSIFIER_NATIVE_LIB
                    )
                )
            }
    }
    fun createFromBufferAndOptions(
        modelBuffer: ByteBuffer, options: AudioClassifierOptions
    ): AudioClassifier? {
        require(modelBuffer.isDirect || modelBuffer is MappedByteBuffer) { "The model buffer should be either a direct ByteBuffer or a MappedByteBuffer." }
        return AudioClassifier(
            TaskJniUtils.createHandleFromLibrary(
                {
                    initJniWithByteBuffer(
                        modelBuffer,
                        options,
                        TaskJniUtils.createProtoBaseOptionsHandle(options.baseOptions)
                    )
                },
                AUDIO_CLASSIFIER_NATIVE_LIB
            )
        )
    }

    private fun AudioClassifier(nativeHandle: Long) {
        super(nativeHandle)
    }


    class AudioClassifierOptions private constructor(builder: Builder) {
        // Not using AutoValue for this class because scoreThreshold cannot have default value
        // (otherwise, the default value would override the one in the model metadata) and `Optional` is
        // not an option here, because
        // 1. java.util.Optional require Java 8 while we need to support Java 7.
        // 2. The Guava library (com.google.common.base.Optional) is avoided in this project. See the
        // comments for labelAllowList.
        val baseOptions: BaseOptions

        @get:UsedByReflection("audio_classifier_jni.cc")
        val displayNamesLocale: String

        @get:UsedByReflection("audio_classifier_jni.cc")
        val maxResults: Int

        @get:UsedByReflection("audio_classifier_jni.cc")
        val scoreThreshold: Float

        @get:UsedByReflection("audio_classifier_jni.cc")
        val isScoreThresholdSet: Boolean

        // As an open source project, we've been trying avoiding depending on common java libraries,
        // such as Guava, because it may introduce conflicts with clients who also happen to use those
        // libraries. Therefore, instead of using ImmutableList here, we convert the List into
        // unmodifiableList in setLabelAllowList() and setLabelDenyList() to make it less
        // vulnerable.
        private val labelAllowList: List<String>
        private val labelDenyList: List<String>

        /** A builder that helps to configure an instance of AudioClassifierOptions.  */
        class Builder private constructor() {
            private var baseOptions = BaseOptions.builder().build()
            private var displayNamesLocale = "en"
            private var maxResults = -1
            private var scoreThreshold = 0f
            private var isScoreThresholdSet = false
            private var labelAllowList: List<String> = ArrayList()
            private var labelDenyList: List<String> = ArrayList()

            /** Sets the general options to configure Task APIs, such as accelerators.  */
            fun setBaseOptions(baseOptions: BaseOptions): Builder {
                this.baseOptions = baseOptions
                return this
            }

            /**
             * Sets the locale to use for display names specified through the TFLite Model Metadata, if
             * any.
             *
             *
             * Defaults to English(`"en"`). See the [TFLite
 * Metadata schema file.](https://github.com/tensorflow/tflite-support/blob/3ce83f0cfe2c68fecf83e019f2acc354aaba471f/tensorflow_lite_support/metadata/metadata_schema.fbs#L147) for the accepted pattern of locale.
             */
            fun setDisplayNamesLocale(displayNamesLocale: String): Builder {
                this.displayNamesLocale = displayNamesLocale
                return this
            }

            /**
             * Sets the maximum number of top scored results to return.
             *
             * @param maxResults if < 0, all results will be returned. If 0, an invalid argument error is
             * returned. Defaults to -1.
             * @throws IllegalArgumentException if maxResults is 0
             */
            fun setMaxResults(maxResults: Int): Builder {
                if (maxResults == 0) {
                    throw IllegalArgumentException("maxResults cannot be 0.")
                }
                this.maxResults = maxResults
                return this
            }

            /**
             * Sets the score threshold.
             *
             *
             * It overrides the one provided in the model metadata (if any). Results below this value
             * are rejected.
             */
            fun setScoreThreshold(scoreThreshold: Float): Builder {
                this.scoreThreshold = scoreThreshold
                isScoreThresholdSet = true
                return this
            }

            /**
             * Sets the optional allowlist of labels.
             *
             *
             * If non-empty, classifications whose label is not in this set will be filtered out.
             * Duplicate or unknown labels are ignored. Mutually exclusive with labelDenyList.
             */
            fun setLabelAllowList(labelAllowList: List<String?>?): Builder {
                this.labelAllowList = Collections.unmodifiableList(ArrayList(labelAllowList))
                return this
            }

            /**
             * Sets the optional denylist of labels.
             *
             *
             * If non-empty, classifications whose label is in this set will be filtered out. Duplicate
             * or unknown labels are ignored. Mutually exclusive with labelAllowList.
             */
            fun setLabelDenyList(labelDenyList: List<String?>?): Builder {
                this.labelDenyList = Collections.unmodifiableList(ArrayList(labelDenyList))
                return this
            }

            fun build(): AudioClassifierOptions {
                return AudioClassifierOptions(this)
            }
        }

        @UsedByReflection("audio_classifier_jni.cc")
        fun getLabelAllowList(): List<String> {
            return ArrayList(labelAllowList)
        }

        @UsedByReflection("audio_classifier_jni.cc")
        fun getLabelDenyList(): List<String> {
            return ArrayList(labelDenyList)
        }

        companion object {
            fun builder(): Builder {
                return Builder()
            }
        }

        init {
            displayNamesLocale = builder.displayNamesLocale
            maxResults = builder.maxResults
            scoreThreshold = builder.scoreThreshold
            isScoreThresholdSet = builder.isScoreThresholdSet
            labelAllowList = builder.labelAllowList
            labelDenyList = builder.labelDenyList
            baseOptions = builder.baseOptions
        }
    }

    /**
     * Performs actual classification on the provided audio tensor.
     *
     * @param tensor a [TensorAudio] containing the input audio clip in float with values
     * between [-1, 1). The `tensor` argument should have the same flat size as the TFLite
     * model's input tensor. It's recommended to create `tensor` using `createInputTensorAudio` method.
     * @throws IllegalArgumentException if an argument is invalid
     * @throws IllegalStateException if error occurs when classifying the audio clip from the native
     * code
     */
    fun classify(tensor: TensorAudio): List<Classifications?>? {
        val buffer = tensor.tensorBuffer
        val format = tensor.format
        checkState(
            buffer.buffer.hasArray(),
            "Input tensor buffer should be a non-direct buffer with a backed array (i.e. not readonly"
                    + " buffer)."
        )
        return classifyNative(
            getNativeHandle(),
            buffer.buffer.array(),
            format.channels,
            format.sampleRate
        )
    }

    /**
     * Creates a [TensorAudio] instance to store input audio samples.
     *
     * @return a [TensorAudio] with the same size as model input tensor
     * @throws IllegalArgumentException if the model is not compatible
     */
    fun createInputTensorAudio(): TensorAudio? {
        val format = getRequiredTensorAudioFormat()
        val bufferSize = getRequiredInputBufferSize()
        val samples = bufferSize / format.channels
        return TensorAudio.create(format, samples.toInt())
    }

    /** Returns the required input buffer size in number of float elements.  */
    fun getRequiredInputBufferSize(): Long {
        return getRequiredInputBufferSizeNative(getNativeHandle())
    }

    /**
     * Creates an [android.media.AudioRecord] instance to record audio stream. The returned
     * AudioRecord instance is initialized and client needs to call [ ][android.media.AudioRecord.startRecording] method to start recording.
     *
     * @return an [android.media.AudioRecord] instance in [     ][android.media.AudioRecord.STATE_INITIALIZED]
     * @throws IllegalArgumentException if the model required channel count is unsupported
     * @throws IllegalStateException if AudioRecord instance failed to initialize
     */
    fun createAudioRecord(): AudioRecord? {
        val format = getRequiredTensorAudioFormat()
        var channelConfig = 0
        when (format.channels) {
            1 -> channelConfig = AudioFormat.CHANNEL_IN_MONO
            2 -> channelConfig = AudioFormat.CHANNEL_IN_STEREO
            else -> throw IllegalArgumentException(
                String.format(
                    "Number of channels required by the model is %d. getAudioRecord method only"
                            + " supports 1 or 2 audio channels.",
                    format.channels
                )
            )
        }
        var bufferSizeInBytes = AudioRecord.getMinBufferSize(
            format.sampleRate, channelConfig, AudioFormat.ENCODING_PCM_FLOAT
        )
        if ((bufferSizeInBytes == AudioRecord.ERROR
                    || bufferSizeInBytes == AudioRecord.ERROR_BAD_VALUE)
        ) {
            throw IllegalStateException(
                String.format(
                    "AudioRecord.getMinBufferSize failed. Returned: %d",
                    bufferSizeInBytes
                )
            )
        }
        // The buffer of AudioRecord should be strictly longer than what model requires so that clients
        // could run `TensorAudio::load(record)` together with `AudioClassifier::classify`.
        val bufferSizeMultiplier = 2
        val modelRequiredBufferSize: Int =
            getRequiredInputBufferSize().toInt() * DataType.FLOAT32.byteSize() * bufferSizeMultiplier
        if (bufferSizeInBytes < modelRequiredBufferSize) {
            bufferSizeInBytes = modelRequiredBufferSize
        }
        val audioRecord = AudioRecord( // including MIC, UNPROCESSED, and CAMCORDER.
            MediaRecorder.AudioSource.VOICE_RECOGNITION,
            format.sampleRate,
            channelConfig,
            AudioFormat.ENCODING_PCM_FLOAT,
            bufferSizeInBytes
        )
        checkState(
            audioRecord.state == AudioRecord.STATE_INITIALIZED,
            "AudioRecord failed to initialize"
        )
        return audioRecord
    }

    /** Returns the [TensorAudioFormat] required by the model.  */
    fun getRequiredTensorAudioFormat(): TensorAudioFormat {
        return TensorAudioFormat.builder()
            .setChannels(getRequiredChannels())
            .setSampleRate(getRequiredSampleRate())
            .build()
    }

    private fun getRequiredChannels(): Int {
        return getRequiredChannelsNative(getNativeHandle())
    }

    private fun getRequiredSampleRate(): Int {
        return getRequiredSampleRateNative(nativeHandle())
    }

    // TODO(b/183343074): JNI method invocation is very expensive, taking about .2ms
    // each time. Consider combining the native getter methods into 1 and cache it in Java layer.
    private external fun getRequiredInputBufferSizeNative(nativeHandle: Long): Long

    private external fun getRequiredChannelsNative(nativeHandle: Long): Int

    private external fun getRequiredSampleRateNative(nativeHandle: Long): Int

    private external fun classifyNative(
        nativeHandle: Long, audioBuffer: ByteArray, channels: Int, sampleRate: Int
    ): List<Classifications?>?

    private external fun initJniWithModelFdAndOptions(
        fileDescriptor: Int,
        fileDescriptorLength: Long,
        fileDescriptorOffset: Long,
        options: AudioClassifierOptions,
        baseOptionsHandle: Long
    ): Long

    private external fun initJniWithByteBuffer(
        modelBuffer: ByteBuffer, options: AudioClassifierOptions, baseOptionsHandle: Long
    ): Long

    /**
     * Releases memory pointed by `nativeHandle`, namely a C++ `AudioClassifier` instance.
     *
     * @param nativeHandle pointer to memory allocated
     */
    protected fun deinit(nativeHandle: Long) {
        deinitJni(nativeHandle)
    }

    /**
     * Native method to release memory pointed by `nativeHandle`, namely a C++ `AudioClassifier`
     * instance.
     *
     * @param nativeHandle pointer to memory allocated
     */
    private external fun deinitJni(nativeHandle: Long)
}