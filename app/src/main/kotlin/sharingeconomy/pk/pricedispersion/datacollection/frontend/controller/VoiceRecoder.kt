/*
 * controller/VoiceRecorder.kt
 * (Price Dispersion Application for data collection)s
 * Q@sharingeconomy.pk
 *
 *
 *
 */

package sharingeconomy.pk.pricedispersion.datacollection.frontend.controller

import android.media.MediaRecorder
import android.util.Log

import androidx.appcompat.app.AppCompatActivity

import java.io.File
import java.io.IOException

// Import the required resources from the application
import sharingeconomy.pk.pricedispersion.datacollection.frontend.app.PriceDispersionApplication
import sharingeconomy.pk.pricedispersion.datacollection.frontend.model.DIRECTORY_NAME
import sharingeconomy.pk.pricedispersion.datacollection.frontend.model.SAMPLING_RATE

/* *************************************************************************************************** *
 *  @ddir, absolute path to data directory                                                             *
 *  @prefix, each data file name has two parts prefix and suffix. The prefix is same across all files  *
 * *************************************************************************************************** */
class VoiceRecorder constructor (ddir: File) : AppCompatActivity() {

    private var absolutePath: String
    private var recorder: MediaRecorder? = null

    // Initializer block. During the initialization of an instance, the initializer blocks are executed in the same order as they appear in the class body
    init {

        try {

            if (ddir.mkdir()) {
                Log.i("VoiceRecorder", "Created directory " + ddir)
            }
            else {
                Log.i("VoiceRecorder", "Directory " + ddir + " already exists")
            }
        }
        catch (e: Exception) {

            Log.e("VoiceRecorder Error: ", e.message)
        }

        absolutePath = ddir.toString() + "/"
    }

    /* *********************************************************************************************************************************************
     *  @suffix, each data file name has two parts prefix and suffix. The prefix is same across all files abd suffix is different across all files *
     * ********************************************************************************************************************************************* */
    fun startRecording(fileName: String): Unit {

        if (recorder == null) {
            recorder = MediaRecorder().apply {

                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setOutputFile("$absolutePath$fileName")
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                setAudioSamplingRate(SAMPLING_RATE)

                try {
                    prepare()
                } catch (e: IOException) {
                    Log.e("VoiceRecorder Error: ", e.message)
                }

                start()
            }
        }

        //Log.i("VoiceRecorder", "Voice recording started " + "$fileName$sufix")
        Log.i("VoiceRecorder", "Voice recording started and will be stored in " + "$absolutePath$fileName")
    }

    fun stopRecording(): Unit {

        if (recorder != null) {

            recorder?.apply {

                stop()
                release()
            }

            recorder = null
        }

        Log.i("VoiceRecorder", "Voice recording stopped")
    }

};