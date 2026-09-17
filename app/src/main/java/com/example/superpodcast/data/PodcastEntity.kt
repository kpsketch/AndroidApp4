package com.example.superpodcast.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Assignment 8 - Room Database
 *
 * PodcastEntity represents a podcast that is stored
 * locally in the Room database.
 *
 * Each PodcastEntity object becomes one row
 * in the "podcasts" database table.
 */
@Entity(tableName = "podcasts")
data class PodcastEntity(

    // The podcast ID is used as the unique primary key.
    // This prevents the same podcast from being stored
    // more than once in the database.
    @PrimaryKey
    val podcastId: Long,

    // Name/title of the podcast.
    val title: String,

    // Artist or publisher of the podcast.
    val artist: String,

    // URL for the podcast artwork image.
    val artworkUrl: String,

    // RSS feed URL used to retrieve podcast episodes.
    val feedUrl: String
)