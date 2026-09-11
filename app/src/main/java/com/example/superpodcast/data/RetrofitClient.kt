package com.example.superpodcast.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Creates and stores the Retrofit service used throughout the application.
object RetrofitClient {

    private const val BASE_URL =
        "https://itunes.apple.com/"

    // Lazy initialization means Retrofit is created only when first required.
    val api: ITunesApi by lazy {

        Retrofit.Builder()
            .baseUrl(BASE_URL)

            // Gson converts JSON responses into PodcastResponse objects.
            .addConverterFactory(
                GsonConverterFactory.create()
            )

            .build()

            .create(ITunesApi::class.java)
    }
}