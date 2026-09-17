plugins {
    // Android application plugin
    alias(libs.plugins.android.application)

    // KSP is used to generate code for libraries such as
    // Glide and Room.
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.example.superpodcast"

    // API 37.1 is required by the current Android dependencies.
    compileSdk {
        version = release(37) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.example.superpodcast"

        // Minimum Android version supported by the application.
        minSdk = 24

        // Target Android SDK for the project.
        targetSdk = 36

        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner =
            "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            // Optimization is disabled for this assignment project.
            optimization {
                enable = false
            }
        }
    }

    compileOptions {
        // Java compatibility used by the project.
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    // ---------------------------------------------------------
    // Standard Android libraries
    // ---------------------------------------------------------

    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)


    // ---------------------------------------------------------
    // Assignment 7 - Retrofit
    // ---------------------------------------------------------

    // Retrofit handles communication with the iTunes API.
    implementation(libs.retrofit)

    // Converts JSON responses returned by Retrofit into
    // Kotlin objects.
    implementation(libs.retrofit.gson)


    // ---------------------------------------------------------
    // Assignment 7 - Glide
    // ---------------------------------------------------------

    // Glide loads podcast artwork from image URLs.
    implementation(libs.glide)

    // KSP generates code required by Glide.
    ksp(libs.glide.ksp)


    // ---------------------------------------------------------
    // Assignment 7 - Gson
    // ---------------------------------------------------------

    // Gson is used for JSON parsing.
    implementation(libs.gson)


    // ---------------------------------------------------------
    // Assignment 7 - RecyclerView
    // ---------------------------------------------------------

    // RecyclerView displays podcast search results
    // as a scrolling list.
    implementation(libs.recyclerview)


    // ---------------------------------------------------------
    // Assignment 7 - Lifecycle / ViewModel
    // ---------------------------------------------------------

    // Provides lifecycle-aware ViewModel support.
    implementation(libs.lifecycle.viewmodel)


    // ---------------------------------------------------------
    // Assignment 8 - Room Database
    // ---------------------------------------------------------

    // Room Runtime provides the main Room database APIs.
    implementation(libs.room.runtime)

    // Room KTX provides Kotlin extensions and coroutine
    // support for Room.
    implementation(libs.room.ktx)

    // Room Compiler generates the database implementation
    // from our Entity, DAO and Database classes.
    ksp(libs.room.compiler)


    // ---------------------------------------------------------
    // Testing libraries
    // ---------------------------------------------------------

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}