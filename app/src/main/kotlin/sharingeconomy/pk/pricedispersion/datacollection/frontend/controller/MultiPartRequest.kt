/*
 * controller/MultiPartRequest.kt
 * (Price Dispersion Application for data collection)s
 * Q@sharingeconomy.pk
 *
 *
 *
 *
 */

package sharingeconomy.pk.pricedispersion.datacollection.frontend.controller

import android.util.Log

import com.android.volley.NetworkResponse
import com.android.volley.ParseError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser

import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException

import sharingeconomy.pk.pricedispersion.datacollection.frontend.model.*

/*
 * In Kotlin, the primary constructor is part of the class header (after the class name), and any parameters passed there are either:
 * 1. Properties (if marked with val/var), or
 * 2. Just constructor parameters (if not marked with val/var)
 */
class MultiPartRequest(private val url: String /* Server URL, private property of this class */, private val params: Map<String, String> /* Map/array of server expected key/value pairs, private property of this class */, private val audioFile: File /* File top be sent as multipart, private property of this class */, private val fileFieldName: String = "audio" /* Server's expected param name, private property of this class */, private val listener: Response.Listener<String> /* Private property of this class */, errorListener: Response.ErrorListener /* Constructor parameter directly being passed to super class constructor */) : Request<String>(Method.POST, url, errorListener) /* invoking the superclass constructor, explicitly */ {

    // Executed right after the execution of primary constructor(after the evalution of parameters passed to it) and before secondry constructor. Class can have many "init" and get executed in the order they appar in the definintion of class
    init {

    }

    // Multipart boundary setup
    private val boundary = "AudioUploadBoundary${System.currentTimeMillis()}"
    private val lineEnd = "\r\n"
    private val twoHyphens = "--"

    override fun getHeaders(): MutableMap<String, String> {
        return HashMap<String, String>().apply {
            put("Content-Type", "multipart/form-data; boundary=$boundary")
        }
    }

    private fun addFormField(writer: DataOutputStream, key: String, value: String) {
        writer.writeBytes("$twoHyphens$boundary$lineEnd")
        writer.writeBytes("Content-Disposition: form-data; name=\"$key\"$lineEnd")
        writer.writeBytes("Content-Type: text/plain$lineEnd")
        writer.writeBytes(lineEnd)
        writer.writeBytes(value + lineEnd)
    }

    override fun getBody(): ByteArray {
        val outputStream = ByteArrayOutputStream()
        val writer = DataOutputStream(outputStream)

        try {
            // Add metadata fields (e.g., title, duration)
            for ((key, value) in params) {
                addFormField(writer, key, value)
            }

            // Add the .3gp audio file
            writer.writeBytes("$twoHyphens$boundary$lineEnd")
            writer.writeBytes("Content-Disposition: form-data; name=\"$fileFieldName\"; filename=\"${audioFile.name}\"$lineEnd")
            writer.writeBytes("Content-Type: audio/3gpp$lineEnd")  // MIME type for .3gp
            writer.writeBytes(lineEnd)

            FileInputStream(audioFile).use { inputStream ->
                val buffer = ByteArray(1024)
                var bytesRead: Int
                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    writer.write(buffer, 0, bytesRead)
                }
            }
            writer.writeBytes(lineEnd)

            // End of multipart
            writer.writeBytes("$twoHyphens$boundary$twoHyphens$lineEnd")
            writer.flush()
            return outputStream.toByteArray()
        } catch (e: Exception) {
            Log.e("AudioUpload", "Error creating request body", e)
            throw IOException("Failed to build multipart request: ${e.message}")
        } finally {
            writer.close()
            outputStream.close()
        }
    }

    override fun getBodyContentType(): String {
        return "multipart/form-data; boundary=$boundary"
    }


    // ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////// //
    //  These two methods must be implemented; otherwise, you'll encounter a compile-time error.                             //
    //  The error occurs because `Request` is an abstract class, and the child class (`MultiPartRequest`) must override its  //
    //  abstract methods                                                                                                     //
    // ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////// //
    /**
     * Delivers the response from the server to the registered listener.
     * @param response The parsed response from the server, or `null` if the request failed
     */
    override fun deliverResponse(response: String?) {
        listener.onResponse(response)
    }
    /**
     * Parses the raw network response into a usable format.
     * @param response The raw network response containing data and headers.
     * @return A `Response<String>` object representing success or failure
     */
    override fun parseNetworkResponse(response: NetworkResponse?): Response<String> {
        return try {
            Response.success(
                String(response?.data ?: byteArrayOf()),
                HttpHeaderParser.parseCacheHeaders(response)
            )
        } catch (e: Exception) {
            Response.error(ParseError(e))
        }
    }
}