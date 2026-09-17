package com.example.superpodcast

import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.superpodcast.data.PodcastDatabase
import com.example.superpodcast.data.PodcastEntity
import com.example.superpodcast.data.RssFeedParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Assignment 8 - Room Database
 *
 * PodcastDetailActivity displays information about the podcast
 * selected by the user.
 *
 * This Activity also allows the user to:
 *
 * 1. Subscribe to a podcast.
 * 2. Unsubscribe from a podcast.
 * 3. Store subscriptions using the Room database.
 * 4. Play the latest podcast episode.
 * 5. Pause podcast playback.
 */
class PodcastDetailActivity : AppCompatActivity() {

    // ---------------------------------------------------------
    // UI CONTROLS
    // ---------------------------------------------------------

    private lateinit var imageArtwork: ImageView
    private lateinit var textTitle: TextView
    private lateinit var textArtist: TextView
    private lateinit var buttonSubscribe: Button
    private lateinit var buttonPlay: Button
    private lateinit var buttonBack: Button


    // ---------------------------------------------------------
    // PODCAST INFORMATION
    // ---------------------------------------------------------

    private var podcastId: Long = 0L
    private var podcastTitle: String = ""
    private var artistName: String = ""
    private var artworkUrl: String? = null
    private var feedUrl: String? = null


    // ---------------------------------------------------------
    // ROOM DATABASE
    // ---------------------------------------------------------

    // The database gives us access to PodcastDao.
    private lateinit var database: PodcastDatabase

    // Keeps track of the subscription state currently shown
    // on the screen.
    private var subscribed = false


    // ---------------------------------------------------------
    // MEDIA PLAYER
    // ---------------------------------------------------------

    // MediaPlayer streams the latest podcast episode.
    private var mediaPlayer: MediaPlayer? = null

    // Tracks whether audio is currently playing.
    private var isPlaying = false


    // ---------------------------------------------------------
    // ON CREATE
    // ---------------------------------------------------------

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_podcast_detail
        )


        // -----------------------------------------------------
        // CONNECT KOTLIN VARIABLES TO XML CONTROLS
        // -----------------------------------------------------

        imageArtwork =
            findViewById(
                R.id.imageDetailArtwork
            )

        textTitle =
            findViewById(
                R.id.textDetailTitle
            )

        textArtist =
            findViewById(
                R.id.textDetailArtist
            )

        buttonSubscribe =
            findViewById(
                R.id.buttonSubscribe
            )

        buttonPlay =
            findViewById(
                R.id.buttonPlay
            )

        buttonBack =
            findViewById(
                R.id.buttonBack
            )


        // -----------------------------------------------------
        // CREATE / OPEN ROOM DATABASE
        // -----------------------------------------------------

        database =
            PodcastDatabase.getDatabase(
                applicationContext
            )


        // -----------------------------------------------------
        // RECEIVE PODCAST INFORMATION FROM MAINACTIVITY
        // -----------------------------------------------------

        podcastId =
            intent.getLongExtra(
                "podcastId",
                0L
            )

        podcastTitle =
            intent.getStringExtra(
                "title"
            ) ?: "Unknown Podcast"

        artistName =
            intent.getStringExtra(
                "artist"
            ) ?: "Unknown Artist"

        artworkUrl =
            intent.getStringExtra(
                "artwork"
            )

        feedUrl =
            intent.getStringExtra(
                "feedUrl"
            )


        // -----------------------------------------------------
        // DISPLAY PODCAST INFORMATION
        // -----------------------------------------------------

        textTitle.text =
            podcastTitle

        textArtist.text =
            artistName


        // Glide downloads and displays the podcast artwork.
        Glide.with(this)
            .load(artworkUrl)
            .into(imageArtwork)


        // -----------------------------------------------------
        // CHECK ROOM DATABASE FOR SUBSCRIPTION
        // -----------------------------------------------------

        checkSubscriptionStatus()


        // -----------------------------------------------------
        // SUBSCRIBE BUTTON
        // -----------------------------------------------------

        buttonSubscribe.setOnClickListener {

            if (subscribed) {

                unsubscribePodcast()

            } else {

                subscribePodcast()
            }
        }


        // -----------------------------------------------------
        // PLAY / PAUSE BUTTON
        // -----------------------------------------------------

        buttonPlay.setOnClickListener {

            if (isPlaying) {

                pausePodcast()

            } else {

                loadAndPlayLatestEpisode()
            }
        }


        // -----------------------------------------------------
        // BACK BUTTON
        // -----------------------------------------------------

        buttonBack.setOnClickListener {

            finish()
        }
    }


    // ---------------------------------------------------------
    // ROOM DATABASE - CHECK SUBSCRIPTION
    // ---------------------------------------------------------

    /**
     * Checks the Room database to determine whether
     * this podcast is already subscribed.
     *
     * Database operations are performed using Dispatchers.IO
     * so they do not block the user interface.
     */
    private fun checkSubscriptionStatus() {

        lifecycleScope.launch {

            val savedPodcast =
                withContext(
                    Dispatchers.IO
                ) {

                    database
                        .podcastDao()
                        .getPodcastById(
                            podcastId
                        )
                }

            subscribed =
                savedPodcast != null

            updateSubscribeButton()
        }
    }


    // ---------------------------------------------------------
    // ROOM DATABASE - SUBSCRIBE
    // ---------------------------------------------------------

    /**
     * Saves the selected podcast into the Room database.
     */
    private fun subscribePodcast() {

        // Create a PodcastEntity containing the podcast
        // information that will be stored in Room.
        val podcast =
            PodcastEntity(
                podcastId = podcastId,
                title = podcastTitle,
                artist = artistName,
                artworkUrl = artworkUrl ?: "",
                feedUrl = feedUrl ?: ""
            )


        lifecycleScope.launch {

            // Insert database records on the IO thread.
            withContext(
                Dispatchers.IO
            ) {

                database
                    .podcastDao()
                    .insertPodcast(
                        podcast
                    )
            }


            // Update local subscription state.
            subscribed = true

            updateSubscribeButton()


            Toast.makeText(
                this@PodcastDetailActivity,
                "Subscribed to $podcastTitle",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    // ---------------------------------------------------------
    // ROOM DATABASE - UNSUBSCRIBE
    // ---------------------------------------------------------

    /**
     * Removes the current podcast from the Room database.
     */
    private fun unsubscribePodcast() {

        val podcast =
            PodcastEntity(
                podcastId = podcastId,
                title = podcastTitle,
                artist = artistName,
                artworkUrl = artworkUrl ?: "",
                feedUrl = feedUrl ?: ""
            )


        lifecycleScope.launch {

            // Delete the podcast from Room.
            withContext(
                Dispatchers.IO
            ) {

                database
                    .podcastDao()
                    .deletePodcast(
                        podcast
                    )
            }


            // Update local subscription state.
            subscribed = false

            updateSubscribeButton()


            Toast.makeText(
                this@PodcastDetailActivity,
                "Unsubscribed from $podcastTitle",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    // ---------------------------------------------------------
    // UPDATE SUBSCRIBE BUTTON
    // ---------------------------------------------------------

    /**
     * Changes the Subscribe button text depending
     * on whether the podcast exists in Room.
     */
    private fun updateSubscribeButton() {

        if (subscribed) {

            buttonSubscribe.text =
                "Subscribed ✓"

        } else {

            buttonSubscribe.text =
                "Subscribe"
        }
    }


    // ---------------------------------------------------------
    // LOAD LATEST PODCAST EPISODE
    // ---------------------------------------------------------

    /**
     * Downloads the podcast RSS feed and retrieves
     * the newest playable episode.
     */
    private fun loadAndPlayLatestEpisode() {

        val url =
            feedUrl


        // Make sure the podcast has an RSS feed.
        if (url.isNullOrEmpty()) {

            Toast.makeText(
                this,
                "No podcast feed available.",
                Toast.LENGTH_SHORT
            ).show()

            return
        }


        // Disable the button while the RSS feed loads.
        buttonPlay.isEnabled =
            false

        buttonPlay.text =
            "Loading..."


        lifecycleScope.launch {

            // RSS downloading and parsing happen
            // on the IO thread.
            val episode =
                withContext(
                    Dispatchers.IO
                ) {

                    RssFeedParser
                        .getLatestEpisode(
                            url
                        )
                }


            // No playable episode was found.
            if (episode == null) {

                buttonPlay.isEnabled =
                    true

                buttonPlay.text =
                    "Play Podcast"


                Toast.makeText(
                    this@PodcastDetailActivity,
                    "Unable to find a playable episode.",
                    Toast.LENGTH_LONG
                ).show()

                return@launch
            }


            Toast.makeText(
                this@PodcastDetailActivity,
                "Playing: ${episode.title}",
                Toast.LENGTH_SHORT
            ).show()


            // Start streaming the episode.
            playAudio(
                episode.audioUrl
            )
        }
    }


    // ---------------------------------------------------------
    // PLAY AUDIO
    // ---------------------------------------------------------

    /**
     * Streams the selected podcast episode
     * using Android MediaPlayer.
     */
    private fun playAudio(
        audioUrl: String
    ) {

        try {

            // Release any previous MediaPlayer before
            // creating a new MediaPlayer instance.
            mediaPlayer?.release()


            mediaPlayer =
                MediaPlayer().apply {

                    // URL of the podcast MP3/audio file.
                    setDataSource(
                        audioUrl
                    )


                    // MediaPlayer prepares the remote audio
                    // asynchronously so the UI does not freeze.
                    setOnPreparedListener {

                        it.start()


                        this@PodcastDetailActivity
                            .isPlaying = true


                        buttonPlay.isEnabled =
                            true


                        buttonPlay.text =
                            "Pause Podcast"
                    }


                    // Reset the Play button when
                    // the episode finishes.
                    setOnCompletionListener {

                        this@PodcastDetailActivity
                            .isPlaying = false


                        buttonPlay.text =
                            "Play Podcast"
                    }


                    // Handle playback errors without
                    // crashing the application.
                    setOnErrorListener {
                            _,
                            _,
                            _ ->


                        this@PodcastDetailActivity
                            .isPlaying = false


                        buttonPlay.isEnabled =
                            true


                        buttonPlay.text =
                            "Play Podcast"


                        Toast.makeText(
                            this@PodcastDetailActivity,
                            "Unable to play this episode.",
                            Toast.LENGTH_LONG
                        ).show()


                        true
                    }


                    // Start preparing the online audio.
                    prepareAsync()
                }

        } catch (
            exception: Exception
        ) {

            buttonPlay.isEnabled =
                true


            buttonPlay.text =
                "Play Podcast"


            Toast.makeText(
                this,
                "Playback error.",
                Toast.LENGTH_LONG
            ).show()
        }
    }


    // ---------------------------------------------------------
    // PAUSE AUDIO
    // ---------------------------------------------------------

    /**
     * Pauses the currently playing podcast episode.
     */
    private fun pausePodcast() {

        mediaPlayer?.let {

            if (it.isPlaying) {

                it.pause()
            }
        }


        isPlaying =
            false


        buttonPlay.text =
            "Play Podcast"
    }


    // ---------------------------------------------------------
    // CLEAN UP MEDIAPLAYER
    // ---------------------------------------------------------

    /**
     * MediaPlayer must be released when the Activity
     * is destroyed so that system audio resources
     * are not unnecessarily retained.
     */
    override fun onDestroy() {

        super.onDestroy()


        mediaPlayer?.release()


        mediaPlayer =
            null
    }
}