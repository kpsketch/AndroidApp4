plugins {
    alias(libs.plugins.android.application)
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

        // Assignment project minimum SDK.
        minSdk = 24

        targetSdk = 36

        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner =
            "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    // Standard Android libraries
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)

    // Retrofit handles communication with the iTunes API.
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)

    // Glide loads podcast artwork from image URLs.
    implementation(libs.glide)
    ksp(libs.glide.ksp)

    // Gson converts JSON responses into Kotlin objects.
    implementation(libs.gson)

    // Displays podcast search results as a scrolling list.
    implementation(libs.recyclerview)

    // Provides lifecycleScope for lifecycle-aware coroutines.
    implementation(libs.lifecycle.viewmodel)

    // Testing libraries
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}