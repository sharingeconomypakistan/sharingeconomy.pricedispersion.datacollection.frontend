/*
 * PriceDispersionApplication.kt
 * (Price Dispersion Application for data collection)
 * Q@sharingeconomy.pk
 *
 *
 *
 */

package sharingeconomy.pk.pricedispersion.datacollection.frontend.app

import android.content.Context
import androidx.multidex.MultiDexApplication

class PriceDispersionApplication : MultiDexApplication() {

    companion object {

        private lateinit var instance: PriceDispersionApplication
        fun getApplicationContext(): Context = instance.applicationContext
    }

    override fun onCreate() {

        super.onCreate()
        instance = this
    }
}