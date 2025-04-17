/*
 * extra/RequestActivity.kt
 * (Price Dispersion Application for data collection)s
 * Q@sharingeconomy.pk
 *
 * This file defines the RequestActivity, which is responsible for handling user interactions
 * related to a specific request in the Price Dispersion Application.
 * It extends AppCompatActivity and handles permission results if needed
 */

package sharingeconomy.pk.pricedispersion.datacollection.frontend.ui.extra

// Import necessary Android framework classes
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley

import java.io.File
import java.net.URLEncoder

// Import the required resources from the application
import sharingeconomy.pk.pricedispersion.datacollection.frontend.app.PriceDispersionApplication
import sharingeconomy.pk.pricedispersion.datacollection.frontend.controller.MultiPartRequest
import sharingeconomy.pk.pricedispersion.datacollection.frontend.model.*
import sharingeconomy.pk.pricedispersion.datacollection.frontend.R

/**
 * RequestActivity - An activity that provides UI for handling user requests.
 * Implements the OnRequestPermissionsResultCallback interface, which is actually
 * not necessary unless you are handling runtime permissions
 */
class RequestActivity : AppCompatActivity(), /* This interface is implied to be here. You can remove and it would still work */ ActivityCompat.OnRequestPermissionsResultCallback {

    // ********************************************* //
    //  Data directory where sound files get stored  //
    // ********************************************* //
    private lateinit var absolutePathToDataDir: String

    private lateinit var businessNameString: String
    private lateinit var businessTypeString: String
    // A variable to hold an intent, which may be used to pass data between activities
    private lateinit var intentRequestActivity: Intent
    private lateinit var locationGps: Location
    private lateinit var locationNet: Location
    private lateinit var replyTextView: TextView
    // ******************************************************************************* //
    //  File name of a sound file, each of such files is stored in the data directory  //
    // ******************************************************************************* //
    private var soundFileName: String? = null

    /**
     * onCreate() - Called when the activity is first created.
     * This is where UI elements are initialized and setup logic is executed
     */
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState) // Call the superclass implementation
        setContentView(R.layout.request_activity_layout) // Set the layout for this activity

        // Enables the back button (Up navigation) in the action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Retrieve the intent that started this activity
        intentRequestActivity = this.intent

        businessNameString = intentRequestActivity.getStringExtra(BUSINESS_NAME_STRING_EXTRA)
        businessTypeString = intentRequestActivity.getStringExtra(BUSINESS_TYPE_STRING_EXTRA)

        // ************************************************************************************************************************* //
        //  It is a fully qualified path, you just need to appemed the name of the file to get the complete path to the sound file.  //
        //  The word qualified already suggests that the paths are made of many parts, and what we get here is the final outcome     //
        // ************************************************************************************************************************* //
        absolutePathToDataDir = intentRequestActivity.getStringExtra(DATA_DIRECTORY_ABSOLUTE_PATH_EXTRA)

        locationGps = intentRequestActivity.getParcelableExtra(LOCATION_DATA_EXTRA_GPS)
        locationNet = intentRequestActivity.getParcelableExtra(LOCATION_DATA_EXTRA_NET)

        // **************************** //
        //  The name of the sound file  //
        // **************************** //
        soundFileName = intentRequestActivity.getStringExtra(SOUND_FILE_NAME_STRING_EXTRA)

        Log.i("RequestActivity", "YES -> " + absolutePathToDataDir + soundFileName)

        replyTextView = findViewById(R.id.textview_to_show_reply)

        // Create a new request queue for handling network requests
        val q = Volley.newRequestQueue(PriceDispersionApplication.getApplicationContext())

        /* **************************************************************************** */
        /* Multipart request                                                            */
        /* **************************************************************************** */
        /*val multiPartRequest = object : MultiPartRequest(SERVER_URL,
            Response.Listener { response ->
                // Handle the response data
            },
            Response.ErrorListener { error ->
                // Handle errors
            }) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["description"] = "Voice data"
                return params
            }

            override fun getByteData(): Map<String, DataPart> {
                val params = HashMap<String, DataPart>()
                params["file"] = DataPart(soundFileName, getFileDataFromUri(absolutePathToDataDir), "video/3gpp") // media/format
                return params
            }
        }*/
        // The .3gp file
        val audioFile = File(absolutePathToDataDir, soundFileName)
        // Metadata (optional)
        val params = mapOf(
            "title" to "Meeting Recording",
            "duration" to "120"  // in seconds
        )
        val multiPartRequest = MultiPartRequest(url = MULTIPART_SERVER_URL, params = params, audioFile = audioFile, fileFieldName = "audio",
            Response.Listener { response ->
                Log.d("AudioUpload", "Success: $response")
                replyTextView.text = "${response}"
                // Handle server response (e.g., JSON confirmation)
            }, Response.ErrorListener { error ->
                Log.e("AudioUpload", "Error: ${error.message}")
                replyTextView.text = "${error.message}"
                // Handle VolleyError (timeout, server error, etc.)
            }
        )
        /* **************************************************************************** */
        /* Multipart request ends here                                                  */
        /* **************************************************************************** */

        val jasonArrayRequest = object : JsonArrayRequest(Method.POST, SERVER_URL, null, Response.Listener {

            Log.i("RequestActivity", "Response -> $it")
            replyTextView.text = "$it"
        }, Response.ErrorListener {

            replyTextView.text = "$it"
        }) {
            override fun getBody(): ByteArray {

                var message = "Address.Name=" + URLEncoder.encode(businessNameString,"utf-8")
                message = message + "&" + "Address.Type=" + URLEncoder.encode(businessTypeString,"utf-8")
                message = message + "&" + "Address.GpsLat=" + locationGps.latitude.toString()
                message = message + "&" + "Address.GpsLng=" + locationGps.longitude.toString()
                message = message + "&" + "Address.NetLat=" + locationNet.latitude.toString()
                message = message + "&" + "Address.NetLng=" + locationNet.longitude.toString()
                message = message + "&" + "Address.DataDirectory=" + absolutePathToDataDir.toString()
                if (soundFileName != null)
                {
                    message += "&Address.SoundFileName=${soundFileName}"
                }

                return message.toByteArray()
            }

            override fun getBodyContentType(): String {

                return "application/x-www-form-urlencoded"
            }
        }

        q.add(jasonArrayRequest)
        q.add(multiPartRequest)
    }

    /**
     * Handles the event when the user presses the back button in the action bar.
     * Calls finish() to close this activity and return to the previous one
     */
    override fun onSupportNavigateUp(): Boolean {
        
        /**
         * The finish() method in Android only closes the current activity. 
         * If the current activity was started from another activity, 
         * the user is naturally returned to the previous activity in the back stack. 
         * So, calling finish() ensures that the activity is removed from memory and the user goes back         
         */ 
        finish() // Close this activity and return to the previous one
        /**
         * FOR DOCUMENTATION PURPOSES:-
         * Purpose of return super.onSupportNavigateUp()
         * ------------------------------------------------
         * super.onSupportNavigateUp() is the default implementation of the "Up" button behavior in AppCompatActivity.
         * In many cases, it internally calls finish() (or handles navigation in a parent activity hierarchy if defined in AndroidManifest.xml).
         * Calling super.onSupportNavigateUp() after finish() has no additional effect, because finish() already terminates the activity.
         * Is return super.onSupportNavigateUp() Necessary?
         * ---------------------------------------------------
         * In the case of this acticity, it isn't needed because finish() already ensures the activity is closed.
         * If this acticity was using parent-child navigation (task-based activities), super.onSupportNavigateUp() could be useful for following defined navigation paths
         */
        /**
         * Conclusion
         * -------------
         * finish(): Closes the activity and returns to the previous one.
         * super.onSupportNavigateUp(): Calls the default system behavior, which in most cases, is redundant after calling finish()
         */  
        /*return super.onSupportNavigateUp()*/ // Ensure default behavior is also executed

        return true // Indicating that the event is handled
    }
}

/* 
 * IS THIS POSSIBLE?
 * override fun onSupportNavigateUp(): Boolean {
 *     return super.onSupportNavigateUp()
 * }
 * ------------------------------------------------
 * super.onSupportNavigateUp() is called, which returns the default behavior defined in AppCompatActivity.
 * The default behavior usually pops the current activity from the back stack (similar to finish()), but:
 *    It only works if the parent activity is properly defined in AndroidManifest.xml using android:parentActivityName.
 *    Otherwise, it does nothing, and pressing the "Up" button may not navigate back.
 * WHY THIS MIGHT NOT WORK AS EXPECTED?
 * ---------------------------------------
 * If you do not specify a parent activity in AndroidManifest.xml, super.onSupportNavigateUp() won't close the activity.
 * Example of a correct manifest setup for it to work:
 *  <activity android:name=".RequestActivity"
 *            android:parentActivityName=".MainActivity">
 *      <meta-data android:name="android.support.PARENT_ACTIVITY"
 *                 android:value=".MainActivity"/>
 *  </activity>
 * If android:parentActivityName is missing, the system might not know where to navigate.
 */