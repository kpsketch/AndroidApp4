package com.example.superpodcast.data

// Represents one podcast returned by the iTunes Search API.
data class Podcast(
    val collectionId: Long,
    val collectionName: String,
    val artistName: String,
    val artworkUrl100: String?,
    val feedUrl: String?
)

// Represents the complete response returned by an iTunes podcast search.
data class PodcastResponse(
    val resultCount: Int,
    val results: List<Podcast>
)