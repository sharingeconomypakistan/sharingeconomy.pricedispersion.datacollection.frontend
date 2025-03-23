/*
    App level gradle build script
 */

plugins {

    // https://mvnrepository.com/artifact/com.android.application/com.android.application.gradle.plugin?repo=google
    id("com.android.application") version "8.9.0-rc02"

    // This is the Kotlin plugin for Gradle
    // https://plugins.gradle.org/plugin/org.jetbrains.kotlin.android
    //id("org.jetbrains.kotlin.android") version "1.6.20-M1"
    id("org.jetbrains.kotlin.android") version "1.7.20"
    //id("org.jetbrains.kotlin.android") version "2.1.20-RC"
}

//kotlin {
//    jvmToolchain(21) // Set the JVM target version (e.g., 21)
//}

//buildscript {
repositories {
    google()
    mavenCentral()  
    //flatDir {
    //           dirs("libs") // This tells Gradle to look in the `libs` folder
    //}  
}
//}

android {    
    applicationVariants.all {
        outputs.all {
            (this as? com.android.build.gradle.internal.api.BaseVariantOutputImpl)?.outputFileName =
                "se_data_collection.apk"
        }
    }

    //compileSdkVersion(28)
    defaultConfig {        
        applicationId="sharingeconomy.pk.pricedispersion.datacollection.frontend"
        // Google Play services need minimum apiLevel 19?
        // That is for kitkat on my android due core. Android 4.4.4(API level 19)
        // The version of the SDK you compiled this app with
        compileSdkVersion(28)
        //compileSdkVersion(30)
        // targetSdkVersion is the device you have tested your app to
        //targetSdkVersion(29)
        // API level at least be this. minSdk Up to which version your app will work on. app will not work on device if device version is less than minSdk
        minSdkVersion(19)
        // https://developer.android.com/studio/build/multidex#mdex-gradle
        multiDexEnabled=true
    }

    // https://developer.android.com/kotlin/add-kotlin#source
    sourceSets {
        getByName("main") {
            java.srcDir("src/main/kotlin")
        }
    }
    namespace = "sharingeconomy.pk.pricedispersion.datacollection.frontend"
}

dependencies {

    //implementation(files("./libs/kotlin-gradle-plugin-2.1.20-RC-gradle85.jar"))
    //implementation(files("./libs/bundletool-1.8.2.jar"))

    /*
        The minCompileSdk (31) specified in a
        dependency's AAR metadata (META-INF/com/android/build/gradle/aar-metadata.properties)
        is greater than this module's compileSdkVersion (android-28).
     */
    // Version 1.4.1 is available. I need to keep compileSdkVersion(28). The version 1.4.1 needs minSdkVersion(31)
    implementation("androidx.appcompat:appcompat:1.3.0")

    // To include multidex support prior to Android 5.0
    implementation("androidx.multidex:multidex:2.0.1")

    implementation("com.android.volley:volley:1.2.1")

    implementation("com.google.android.gms:play-services-location:20.0.0")
}

