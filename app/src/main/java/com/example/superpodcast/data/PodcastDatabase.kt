package com.example.superpodcast.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Assignment 8 - Room Database
 *
 * PodcastDatabase is the main Room database class
 * for the SuperPodcast application.
 *
 * It connects the PodcastEntity table with the
 * PodcastDao database operations.
 */
@Database(
    entities = [PodcastEntity::class],
    version = 1,
    exportSchema = false
)
abstract class PodcastDatabase : RoomDatabase() {

    /**
     * Provides access to the PodcastDao.
     *
     * The DAO contains the functions used to insert,
     * delete and retrieve podcasts from the database.
     */
    abstract fun podcastDao(): PodcastDao

    companion object {

        /**
         * INSTANCE stores the single database instance.
         *
         * @Volatile makes sure that changes to INSTANCE
         * are immediately visible to all threads.
         */
        @Volatile
        private var INSTANCE: PodcastDatabase? = null

        /**
         * Returns the existing database instance or
         * creates it if it does not already exist.
         *
         * Using a singleton prevents multiple copies
         * of the Room database from being created.
         */
        fun getDatabase(context: Context): PodcastDatabase {

            return INSTANCE ?: synchronized(this) {

                // Create the Room database.
                val instance =
                    Room.databaseBuilder(
                        context.applicationContext,
                        PodcastDatabase::class.java,
                        "superpodcast_database"
                    ).build()

                // Save the newly created instance.
                INSTANCE = instance

                instance
            }
        }
    }
}