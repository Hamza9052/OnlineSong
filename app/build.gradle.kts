plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
//    id("com.google.devtools.ksp") // Use KSP instead of kapt
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
}

