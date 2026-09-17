package com.example.superpodcast

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.superpodcast.data.PodcastDatabase
import com.example.superpodcast.data.PodcastEntity
import com.example.superpodcast.ui.SubscriptionAdapter
import kotlinx.coroutines.launch

/**
 * Assignment 8 - Room Database
 *
 * SubscriptionsActivity displays all podcasts that
 * the user has subscribed to.
 *
 * The subscriptions are loaded from the local
 * Room database.
 *
 * When the user selects a podcast, the app opens
 * PodcastDetailActivity so the user can view,
 * play, or unsubscribe from the podcast.
 */
class SubscriptionsActivity : AppCompatActivity() {

    // RecyclerView used to display saved subscriptions.
    private lateinit var recyclerSubscriptions: RecyclerView

    // Message displayed when there are no subscriptions.
    private lateinit var textNoSubscriptions: TextView

    // Button used to return to the main screen.
    private lateinit var buttonBack: Button

    // Adapter used by the RecyclerView.
    private lateinit var subscriptionAdapter: SubscriptionAdapter

    /**
     * Called when the Activity is first created.
     */
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_subscriptions
        )

        // Connect Kotlin variables to XML controls.
        recyclerSubscriptions =
            findViewById(
                R.id.recyclerSubscriptions
            )

        textNoSubscriptions =
            findViewById(
                R.id.textNoSubscriptions
            )

        buttonBack =
            findViewById(
                R.id.buttonSubscriptionsBack
            )

        // Create the adapter.
        //
        // When a podcast is selected,
        // open its detail screen.
        subscriptionAdapter =
            SubscriptionAdapter { podcast ->

                openPodcastDetails(
                    podcast
                )
            }

        // Display subscriptions vertically.
        recyclerSubscriptions.layoutManager =
            LinearLayoutManager(this)

        recyclerSubscriptions.adapter =
            subscriptionAdapter

        // Return to the previous screen.
        buttonBack.setOnClickListener {

            finish()
        }
    }

    /**
     * Reload the subscription list whenever
     * this Activity becomes visible.
     *
     * This is important because the user may
     * open a podcast and unsubscribe from it.
     * When they return, the list should update.
     */
    override fun onResume() {

        super.onResume()

        loadSubscriptions()
    }

    /**
     * Reads all subscribed podcasts from
     * the Room database.
     */
    private fun loadSubscriptions() {

        lifecycleScope.launch {

            // Get the Room database.
            val database =
                PodcastDatabase.getDatabase(
                    applicationContext
                )

            // Get all podcasts saved by the user.
            val subscriptions =
                database
                    .podcastDao()
                    .getAllPodcasts()

            // Send the database results
            // to the RecyclerView adapter.
            subscriptionAdapter.submitList(
                subscriptions
            )

            // Show a helpful message when
            // the database contains no subscriptions.
            if (subscriptions.isEmpty()) {

                textNoSubscriptions.visibility =
                    View.VISIBLE

                recyclerSubscriptions.visibility =
                    View.GONE

            } else {

                textNoSubscriptions.visibility =
                    View.GONE

                recyclerSubscriptions.visibility =
                    View.VISIBLE
            }
        }
    }

    /**
     * Opens PodcastDetailActivity for the
     * podcast selected from My Subscriptions.
     */
    private fun openPodcastDetails(
        podcast: PodcastEntity
    ) {

        val intent =
            Intent(
                this,
                PodcastDetailActivity::class.java
            )

        // Pass the saved podcast information
        // to PodcastDetailActivity.
        intent.putExtra(
            "podcastId",
            podcast.podcastId
        )

        intent.putExtra(
            "title",
            podcast.title
        )

        intent.putExtra(
            "artist",
            podcast.artist
        )

        intent.putExtra(
            "artwork",
            podcast.artworkUrl
        )

        intent.putExtra(
            "feedUrl",
            podcast.feedUrl
        )

        startActivity(intent)
    }
}