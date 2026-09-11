package com.example.superpodcast.data

import retrofit2.http.GET
import retrofit2.http.Query

// Defines requests that can be made to the iTunes Search API.
interface ITunesApi {

    // Searches iTunes using the user's search term.
    // Media is limited to podcasts so unrelated iTunes content is excluded.
    @GET("search")
    suspend fun searchPodcasts(
        @Query("term") term: String,
        @Query("media") media: String = "podcast"
    ): PodcastResponse
}