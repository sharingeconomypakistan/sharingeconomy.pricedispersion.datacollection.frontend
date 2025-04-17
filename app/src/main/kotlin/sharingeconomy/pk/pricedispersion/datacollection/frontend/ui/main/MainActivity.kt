/*
 * MainActivity.kt
 * (Price Dispersion Application for data collection)s
 * Q@sharingeconomy.pk
 *
 * This activity handles location tracking using both GPS and Network providers.
 * It updates the UI with the latest coordinates from these providers.
 */

package sharingeconomy.pk.pricedispersion.datacollection.frontend.ui.main

import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.*

import androidx.appcompat.app.AppCompatActivity

import java.io.File

// Import the required resources from the application
import sharingeconomy.pk.pricedispersion.datacollection.frontend.app.PriceDispersionApplication
import sharingeconomy.pk.pricedispersion.datacollection.frontend.controller.VoiceRecorder
import sharingeconomy.pk.pricedispersion.datacollection.frontend.model.*
import sharingeconomy.pk.pricedispersion.datacollection.frontend.R
import sharingeconomy.pk.pricedispersion.datacollection.frontend.ui.extra.RequestActivity

class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener, OnClickListener {

    private lateinit var businessNameEditText: EditText
    // ********************************************* //
    //  Data directory where sound files get stored  //
    // ********************************************* //
    private lateinit var dataDirectory: File
    // Declare an Intent variable to navigate to RequestActivity
    // It will be initialized later before use
    private lateinit var intentRequestActivity: Intent
    // LocationManager to manage location services
    private lateinit var locationManager: LocationManager
    private lateinit var voiceRecorder: VoiceRecorder

    // ******************************************************************************************** //
    //  File name prefix for all sound files, each of such files gets stored in the data directory  //
    // ******************************************************************************************** //
    private var fileName: String = "${FILE_NAME_PREFIX}"
    // **************************************************************************************** //
    //  Each sound file has same prefix and gets distiguished by other files is by this suffix  //
    // **************************************************************************************** //
    private var fileNumberSufix: UInt = 0u

    // Flags to check if location updates have been received
    private var flagGPSExtra: Boolean = false
    private var flagNetExtra: Boolean = false
    // Declare a Button variable for "start recording" action
    // It will be initialized later in onCreate()
    // The variable can hold a Button instance, but it is also allowed to be null
    private var recordingButton: Button? = null
    private var soundRecorded: Boolean = false
    private var spinner: Spinner? = null
    // Declare a Button variable for the submit action
    // It will be initialized later in onCreate()
    // The variable can hold a Button instance, but it is also allowed to be null
    private var submitButton: Button? = null
    
    // UI elements to display GPS and Network coordinates
    private var tvDataGps: TextView? = null
    private var tvDataNet: TextView? = null

    /**
     * Listener for GPS-based location updates.
     */
    private val locationListenerGPSProvider: LocationListener = object: LocationListener {

        override fun onLocationChanged(loc: Location) {

            // Update UI with the latest GPS coordinates
            tvDataGps?.text = getString(R.string.current_gps_coordinates_are, loc.latitude, loc.longitude)

            // Uncomment the following line if passing data via Intent
            intentRequestActivity.putExtra(LOCATION_DATA_EXTRA_GPS, loc)

            flagGPSExtra = true
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
            // Handle status change if needed
        }

        override fun onProviderEnabled(provider: String) {
            // Handle provider enabled event
        }

        override fun onProviderDisabled(provider: String) {
            // Handle provider disabled event
        }
    }

    /**
     * Listener for Network-based location updates.
     */
    private val locationListenerNetworkProvider: LocationListener = object: LocationListener {
        override fun onLocationChanged(loc: Location) {

            // Update UI with the latest Network-based coordinates
            tvDataNet?.text = getString(R.string.current_net_coordinates_are, loc.latitude, loc.longitude)

            // Uncomment the following line if passing data via Intent
            intentRequestActivity.putExtra(LOCATION_DATA_EXTRA_NET, loc)

            flagNetExtra = true
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
            // Handle status change if needed
        }

        override fun onProviderEnabled(provider: String) {
            // Handle provider enabled event
        }

        override fun onProviderDisabled(provider: String) {
            // Handle provider disabled event
        }
    }

    /**
     * Called when the activity is first created.
     * Initializes the UI and sets up layout.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity_layout);

        // *************************************************************************************** //
        //  Initialize data directory. It is a directory where sound files are temporarily stored  //
        // *************************************************************************************** //
        dataDirectory = File("${PriceDispersionApplication.getApplicationContext().externalCacheDir?.absolutePath}/${DIRECTORY_NAME}")

        businessNameEditText = findViewById(R.id.business_name_edit_text)
        // Initialize an intent to navigate to RequestActivity
        // This will be used to pass data and start the RequestActivity when needed.
        intentRequestActivity = Intent(PriceDispersionApplication.getApplicationContext(), RequestActivity::class.java)
        // /////////////////////////////////////////////////////////////////////////////////////////// //
        //  Pass intent data which is an absolute path to the directory where sound files get stored   //
        // /////////////////////////////////////////////////////////////////////////////////////////// //
        intentRequestActivity.putExtra(DATA_DIRECTORY_ABSOLUTE_PATH_EXTRA, dataDirectory.absolutePath + "/")

        // Initialize the "start recording" button by finding its reference in the layout
        recordingButton = findViewById(R.id.recording_button)

        spinner = findViewById(R.id.business_types_spinner)
        // Initialize the submit button by finding its reference in the layout
        submitButton = findViewById(R.id.submit_button)

        voiceRecorder = VoiceRecorder(dataDirectory)

        // Initialize LocationManager to access system location services
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        // Check if Network Provider is enabled and request location updates
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            try {
                locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    MINIMUM_TIME_IN_MILLI_SECONDS,
                    MINIMUM_DISTANCE_IN_METERS,
                    locationListenerNetworkProvider
                )
            } catch (e: java.lang.Exception) {
                Log.e("MainActivity", e.message)
            } finally {
                // Initialize TextView for Network coordinates
                tvDataNet = findViewById(R.id.tv_data_Net)
                if (tvDataNet == null) {
                    throw Exception("MainActivity: unable to get reference of a view R.id.tv_data_Net")
                } else {
                    tvDataNet?.text = getString(R.string.gps_coordinates_are)
                }
            }
        } else {
            Log.i("MainActivity", "NETWORK_PROVIDER is not enabled in your AndroidManifest.xml")
        }

        // Check if GPS Provider is enabled and request location updates
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            try {
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    MINIMUM_TIME_IN_MILLI_SECONDS,
                    MINIMUM_DISTANCE_IN_METERS,
                    locationListenerGPSProvider
                )
            } catch (e: java.lang.Exception) {
                Log.e("MainActivity", e.message)
            } finally {
                // Initialize TextView for GPS coordinates
                tvDataGps = findViewById(R.id.tv_data_Gps)
                if (tvDataGps == null) {
                    throw Exception("MainActivity: unable to get reference of a view R.id.tv_data_Gps")
                }
                else {
                    tvDataGps?.text = getString(R.string.gps_coordinates_are)
                }
            }
        }
        else {
            Log.i("MainActivity", "GPS is not enabled")
        }

        // Set click listener for the "start recording" button
        // This binds the button click event to the onClick function implemented in this activity.
        // The `?.` ensures the method is only called if recordingButton is not null
        recordingButton?.setOnClickListener(this)

        // Set click listener for the submit button
        // This binds the button click event to the onClick function implemented in this activity.
        // The `?.` ensures the method is only called if submitButton is not null
        submitButton?.setOnClickListener(this)

        spinner?.onItemSelectedListener = this

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(this,
            R.array.business_types_string_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner?.adapter = adapter
        }
    }

    /**
     * Handles click events for UI elements.     
     * This method will be executed for every button click event.
     * However, the `when` statement inside will only handle cases explicitly listed.     
     */
    override fun onClick(p0: View?) {
        when (p0) {

            recordingButton -> {

                if (recordingButton?.text == getString(R.string.start_recording_text)) {

                    recordingButton?.text = getString(R.string.stop_recording_text)

                    fileNumberSufix = fileNumberSufix + 1u

                    voiceRecorder.startRecording(fileName+"${fileNumberSufix}.3gp")
                }
                else {

                    recordingButton?.text = getString(R.string.start_recording_text)

                    voiceRecorder.stopRecording()

                    intentRequestActivity.putExtra(SOUND_FILE_NAME_STRING_EXTRA, fileName+"${fileNumberSufix}.3gp")

                    soundRecorded = true;
                }
            }

            // When the submit button is clicked
            submitButton -> {

                // GPS data is not available, then put dummy values
                if (!flagGPSExtra) {
                    intentRequestActivity.putExtra(LOCATION_DATA_EXTRA_GPS, Location(LocationManager.PASSIVE_PROVIDER))
                } else {
                    flagGPSExtra = false
                }

                // Network data is not available, then put dummy values
                if (!flagNetExtra) {
                    intentRequestActivity.putExtra(LOCATION_DATA_EXTRA_NET, Location(LocationManager.PASSIVE_PROVIDER))
                } else {
                    flagNetExtra = false
                }

                // Business name, could be the case that user did not fill this field
                intentRequestActivity.putExtra(BUSINESS_NAME_STRING_EXTRA, businessNameEditText.text.toString())

                if (!soundRecorded) {

                    intentRequestActivity.putExtra(SOUND_FILE_NAME_STRING_EXTRA, "none")
                } else {

                    soundRecorded = false
                }

                startActivity(intentRequestActivity)
            }
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {

        //TODO("Not yet implemented")
        intentRequestActivity.putExtra(BUSINESS_TYPE_STRING_EXTRA, parent?.getItemAtPosition(pos).toString())
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

        //TODO("Not yet implemented")
        intentRequestActivity.putExtra(BUSINESS_TYPE_STRING_EXTRA, "")
    }
}