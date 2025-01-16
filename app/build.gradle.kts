plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
    id ("kotlin-parcelize")
    id("kotlin-kapt")

}

android {
    namespace = "online.song.onlinesong"
    compileSdk = 35

    defaultConfig {
        applicationId = "online.song.onlinesong"
        minSdk = 24
        //noinspection EditedTargetSdkVersion
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "2.0.0"

    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation ("androidx.compose.runtime:runtime-livedata:1.7.5")
//    implementation( "androidx.compose.runtime:runtime:1.7.5")
    // UI & Animation
    implementation(libs.lottie.compose)
    implementation(libs.androidx.material.icons.extended)
    // Navigation & Lifecycle
    implementation("androidx.navigation:navigation-compose:2.8.4")
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation (libs.androidx.navigation.compose)
    //Refresh
    implementation ("com.google.accompanist:accompanist-swiperefresh:0.28.0")
    implementation ("androidx.compose.material:material:1.7.5")

    // Coil
    implementation("io.coil-kt.coil3:coil-compose:3.0.4")
    implementation ("io.coil-kt:coil-compose:2.4.0")

    //Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
    implementation ("com.google.firebase:firebase-firestore-ktx:25.1.1")
    implementation ("com.google.firebase:firebase-storage-ktx:21.0.1")
    implementation ("com.google.firebase:firebase-auth-ktx:23.1.0")
    implementation ("com.google.firebase:firebase-database-ktx:21.0.0")
    implementation ("com.google.firebase:firebase-analytics-ktx:22.1.2")
    implementation ("com.google.firebase:firebase-bom:33.7.0")
    implementation ("com.cloudinary:cloudinary-android:3.0.2")
    implementation ("com.google.firebase:firebase-appcheck-playintegrity:18.0.0")
    implementation ("com.google.firebase:firebase-auth-ktx:23.1.0")
    implementation ("com.google.android.gms:play-services-auth:21.3.0")
    //ExoPlayer
    implementation ("androidx.media3:media3-exoplayer:1.5.0")
    implementation ("androidx.media3:media3-session:1.5.0")
    implementation ("androidx.media3:media3-ui:1.0.3")
    implementation (files("C:\\Users\\Hamza\\AndroidStudioProjects\\OnlineSong\\app\\spotify-app-remote-release-0.8.0.aar"))
    //Spotify

    implementation ("com.spotify.android:auth:2.1.0") // Maven dependency

    // All other dependencies for your app should also be here:
    implementation ("androidx.browser:browser:1.8.0")
    implementation ("com.spotify.sdk:spotify-app-remote-release:0.7.2")
    //For advanced use cases like searching, fetching user playlists, or analyzing tracks, you can use the Spotify Web API.
    // It doesn't require an SDK dependency but uses HTTP requests.
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.10.0")

    //Refresh
    implementation ("com.google.accompanist:accompanist-swiperefresh:0.28.0")
    //Preview
    implementation ("androidx.compose.ui:ui-tooling-preview:1.7.6")
    //GSON
    implementation ("com.google.code.gson:gson:2.10.1")
    //MediaSession
    implementation ("androidx.media:media:1.7.0")
    implementation ("androidx.media2:media2-session:1.3.0")
    implementation ("com.google.android.exoplayer:exoplayer-core:2.19.1")
    implementation ("com.google.android.exoplayer:exoplayer-hls:2.19.1")
    implementation ("com.google.android.exoplayer:exoplayer-ui:2.19.1")
    implementation ("com.google.android.exoplayer:exoplayer:2.19.1")

    implementation ("com.google.dagger:hilt-android:2.52")
    kapt ("com.google.dagger:hilt-compiler:2.40.5")
    implementation ("androidx.hilt:hilt-lifecycle-viewmodel:1.0.0-alpha03")
    kapt ("androidx.hilt:hilt-compiler:1.0.0")

}

