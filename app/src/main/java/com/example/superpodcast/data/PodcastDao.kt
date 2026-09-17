package com.example.superpodcast.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * Assignment 8 - Room Database
 *
 * PodcastDao is the Data Access Object (DAO) for the
 * podcasts table.
 *
 * It contains the database operations that can be
 * performed on saved podcasts.
 */
@Dao
interface PodcastDao {

    /**
     * Saves a podcast in the Room database.
     *
     * REPLACE means that if a podcast with the same
     * podcastId already exists, Room will replace
     * the existing record instead of creating a duplicate.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPodcast(podcast: PodcastEntity)


    /**
     * Removes a podcast from the Room database.
     */
    @Delete
    suspend fun deletePodcast(podcast: PodcastEntity)


    /**
     * Returns all podcasts currently saved
     * in the local database.
     *
     * Podcasts are displayed alphabetically by title.
     */
    @Query("SELECT * FROM podcasts ORDER BY title ASC")
    suspend fun getAllPodcasts(): List<PodcastEntity>


    /**
     * Finds one podcast using its unique podcast ID.
     *
     * If the podcast is not stored in the database,
     * Room returns null.
     */
    @Query("SELECT * FROM podcasts WHERE podcastId = :podcastId LIMIT 1")
    suspend fun getPodcastById(
        podcastId: Long
    ): PodcastEntity?


    /**
     * Checks whether a particular podcast has already
     * been saved in the Room database.
     *
     * A result greater than zero means that the
     * podcast exists.
     */
    @Query("SELECT COUNT(*) FROM podcasts WHERE podcastId = :podcastId")
    suspend fun podcastExists(
        podcastId: Long
    ): Int
}