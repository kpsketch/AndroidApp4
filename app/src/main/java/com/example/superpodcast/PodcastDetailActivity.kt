package com.example.superpodcast

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.superpodcast.data.RssFeedParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PodcastDetailActivity :
    AppCompatActivity() {

    private lateinit var imageArtwork: ImageView
    private lateinit var textTitle: TextView
    private lateinit var textArtist: TextView
    private lateinit var buttonSubscribe: Button
    private lateinit var buttonPlay: Button
    private lateinit var buttonBack: Button

    private var podcastId: Long = 0L
    private var podcastTitle: String = ""
    private var artistName: String = ""
    private var artworkUrl: String? = null
    private var feedUrl: String? = null

    // MediaPlayer streams the latest podcast episode.
    private var mediaPlayer: MediaPlayer? = null

    // Tracks whether audio is currently playing.
    private var isPlaying = false

    // SharedPreferences file used to remember subscriptions.
    private val preferencesName =
        "SuperPodcastSubscriptions"

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_podcast_detail
        )

        // Connect Kotlin variables to XML controls.
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

        // Read the podcast information
        // supplied by MainActivity.
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

        // Display the selected podcast.
        textTitle.text =
            podcastTitle

        textArtist.text =
            artistName

        Glide.with(this)
            .load(artworkUrl)
            .into(imageArtwork)

        // Restore the saved subscription state.
        updateSubscribeButton()

        // Subscribe or unsubscribe from the selected podcast.
        buttonSubscribe.setOnClickListener {

            if (isSubscribed()) {

                unsubscribePodcast()

            } else {

                subscribePodcast()
            }

            updateSubscribeButton()
        }

        // Play or pause the latest episode.
        buttonPlay.setOnClickListener {

            if (isPlaying) {

                pausePodcast()

            } else {

                loadAndPlayLatestEpisode()
            }
        }

        // Return to the podcast search screen.
        buttonBack.setOnClickListener {

            finish()
        }
    }

    // Loads the RSS feed on a background thread
    // so network work does not block the user interface.
    private fun loadAndPlayLatestEpisode() {

        val url = feedUrl

        if (url.isNullOrEmpty()) {

            Toast.makeText(
                this,
                "No podcast feed available.",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        buttonPlay.isEnabled = false

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

            if (episode == null) {

                buttonPlay.isEnabled = true

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

            playAudio(
                episode.audioUrl
            )
        }
    }

    // Streams the selected podcast episode using MediaPlayer.
    private fun playAudio(
        audioUrl: String
    ) {

        try {

            // Release any previous MediaPlayer
            // before creating a new one.
            mediaPlayer?.release()

            mediaPlayer =
                MediaPlayer().apply {

                    setDataSource(
                        audioUrl
                    )

                    // prepareAsync prevents audio preparation
                    // from freezing the UI.
                    setOnPreparedListener {

                        it.start()

                        this@PodcastDetailActivity
                            .isPlaying = true

                        buttonPlay.isEnabled =
                            true

                        buttonPlay.text =
                            "Pause Podcast"
                    }

                    // Reset the button when the episode finishes.
                    setOnCompletionListener {

                        this@PodcastDetailActivity
                            .isPlaying = false

                        buttonPlay.text =
                            "Play Podcast"
                    }

                    // Handle streaming errors without crashing.
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

    // Pauses the current episode.
    private fun pausePodcast() {

        mediaPlayer?.let {

            if (it.isPlaying) {

                it.pause()
            }
        }

        isPlaying = false

        buttonPlay.text =
            "Play Podcast"
    }

    // Checks whether the current podcast
    // has been saved as a subscription.
    private fun isSubscribed(): Boolean {

        val preferences =
            getSharedPreferences(
                preferencesName,
                Context.MODE_PRIVATE
            )

        return preferences.getBoolean(
            podcastId.toString(),
            false
        )
    }

    // Saves the selected podcast as subscribed.
    private fun subscribePodcast() {

        val preferences =
            getSharedPreferences(
                preferencesName,
                Context.MODE_PRIVATE
            )

        preferences.edit()
            .putBoolean(
                podcastId.toString(),
                true
            )
            .apply()

        Toast.makeText(
            this,
            "Subscribed to $podcastTitle",
            Toast.LENGTH_SHORT
        ).show()
    }

    // Removes the selected podcast subscription.
    private fun unsubscribePodcast() {

        val preferences =
            getSharedPreferences(
                preferencesName,
                Context.MODE_PRIVATE
            )

        preferences.edit()
            .remove(
                podcastId.toString()
            )
            .apply()

        Toast.makeText(
            this,
            "Unsubscribed from $podcastTitle",
            Toast.LENGTH_SHORT
        ).show()
    }

    // Changes the button text to show
    // whether the podcast is subscribed.
    private fun updateSubscribeButton() {

        if (isSubscribed()) {

            buttonSubscribe.text =
                "Subscribed ✓"

        } else {

            buttonSubscribe.text =
                "Subscribe"
        }
    }

    // MediaPlayer must be released when
    // this Activity is destroyed.
    override fun onDestroy() {

        super.onDestroy()

        mediaPlayer?.release()

        mediaPlayer = null
    }
}