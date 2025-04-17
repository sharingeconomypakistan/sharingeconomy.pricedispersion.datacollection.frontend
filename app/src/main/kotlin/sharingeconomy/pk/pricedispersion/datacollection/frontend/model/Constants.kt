/*
 * Constants.kt
 * (Price Dispersion Application for data collection)
 * Q@sharingeconomy.pk
 *
 *
 *
 */

package sharingeconomy.pk.pricedispersion.datacollection.frontend.model

// Top level constant
const val PACKAGE_NAME = "shaingeconomy.pricedispersion.datacollection.frontend"

//const val FILE_NAME = "SHARING.txt"
const val BUSINESS_NAME_STRING_EXTRA = PACKAGE_NAME + "business_name_extra_string"
const val BUSINESS_TYPE_STRING_EXTRA = PACKAGE_NAME + "business_type_extra_string"
const val DATA_DIRECTORY_ABSOLUTE_PATH_EXTRA = PACKAGE_NAME + "absolute_path_to_data_directory_extra"
const val DIRECTORY_NAME = "recorded"
const val FILE_NAME_PREFIX = "record"
const val LOCATION_DATA_EXTRA_GPS = PACKAGE_NAME + "location_data_extra_gps"
const val LOCATION_DATA_EXTRA_NET = PACKAGE_NAME + "location_data_extra_net"
const val MINIMUM_TIME_IN_MILLI_SECONDS = 5000L
const val MINIMUM_DISTANCE_IN_METERS = 10.0f
const val REQUEST_RECORD_AUDIO_PERMISSION = 200
const val SAMPLING_RATE = 44100
const val SOUND_FILE_NAME_STRING_EXTRA = PACKAGE_NAME + "sound_file_name_extra_string"
const val SPEECH_REQUEST_CODE = 0

// Original my own server
//const val SERVER_URL = "http://192.168.100.16:3968"

// Used with CRD
//const val SERVER_URL = "http://192.168.100.16:5000"

// Used with OLD_CRD
//const val SERVER_URL = "http://192.168.100.17:3968/Dispersion/Append"

const val SERVER_URL = "http://192.168.100.17:3968/data.html"
const val MULTIPART_SERVER_URL = "http://192.168.100.17:3968/multipart.html"

//const val SERVER_URL = "http://192.168.100.16:5000/Addresses/Create"

/*
 * https://stackoverflow.com/questions/71044525/how-to-access-asp-net-core-web-server-from-another-device
 * https://docs.microsoft.com/en-us/aspnet/core/fundamentals/servers/kestrel/endpoints?view=aspnetcore-6.0
 */
//const val SERVER_URL = "http://192.168.100.16:5001/Addresses/Create"
