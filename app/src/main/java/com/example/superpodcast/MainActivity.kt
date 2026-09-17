package com.example.superpodcast

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.superpodcast.data.RetrofitClient
import com.example.superpodcast.ui.PodcastAdapter
import kotlinx.coroutines.launch

/**
 * Assignment 8 - SuperPodcast
 *
 * MainActivity is the main podcast search screen.
 *
 * The user can:
 * 1. Search for podcasts using the iTunes API.
 * 2. View podcast search results.
 * 3. Open a podcast detail screen.
 * 4. Open the My Subscriptions screen to view
 *    podcasts stored locally in the Room database.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var editTextSearch: EditText
    private lateinit var buttonSearch: Button

    // Assignment 8:
    // Opens podcasts stored in the Room database.
    private lateinit var buttonMySubscriptions: Button

    private lateinit var recyclerView: RecyclerView
    private lateinit var podcastAdapter: PodcastAdapter

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_main
        )

        // ---------------------------------------------------------
        // CONNECT XML VIEWS
        // ---------------------------------------------------------

        editTextSearch =
            findViewById(
                R.id.editTextSearch
            )

        buttonSearch =
            findViewById(
                R.id.buttonSearch
            )

        buttonMySubscriptions =
            findViewById(
                R.id.buttonMySubscriptions
            )

        recyclerView =
            findViewById(
                R.id.recyclerView
            )

        // ---------------------------------------------------------
        // PODCAST SEARCH RESULTS
        // ---------------------------------------------------------

        // Create the RecyclerView adapter.
        //
        // When the user taps a podcast result,
        // PodcastDetailActivity opens.
        podcastAdapter =
            PodcastAdapter { podcast ->

                val intent =
                    Intent(
                        this,
                        PodcastDetailActivity::class.java
                    )

                // Pass the selected podcast ID.
                intent.putExtra(
                    "podcastId",
                    podcast.collectionId
                )

                // Pass the podcast title.
                intent.putExtra(
                    "title",
                    podcast.collectionName
                )

                // Pass the podcast artist/publisher.
                intent.putExtra(
                    "artist",
                    podcast.artistName
                )

                // Pass the podcast artwork URL.
                intent.putExtra(
                    "artwork",
                    podcast.artworkUrl100
                )

                // Pass the RSS feed URL.
                intent.putExtra(
                    "feedUrl",
                    podcast.feedUrl
                )

                startActivity(intent)
            }

        // Display podcast search results vertically.
        recyclerView.layoutManager =
            LinearLayoutManager(this)

        recyclerView.adapter =
            podcastAdapter

        // ---------------------------------------------------------
        // SEARCH BUTTON
        // ---------------------------------------------------------

        buttonSearch.setOnClickListener {

            val searchTerm =
                editTextSearch
                    .text
                    .toString()
                    .trim()

            // Prevent an empty search.
            if (searchTerm.isEmpty()) {

                Toast.makeText(
                    this,
                    "Please enter a podcast to search for.",
                    Toast.LENGTH_SHORT
                ).show()

            } else {

                searchPodcasts(
                    searchTerm
                )
            }
        }

        // ---------------------------------------------------------
        // MY SUBSCRIPTIONS BUTTON
        // ---------------------------------------------------------

        // Assignment 8:
        // Open the screen containing podcasts that
        // have been saved in the Room database.
        buttonMySubscriptions.setOnClickListener {

            val intent =
                Intent(
                    this,
                    SubscriptionsActivity::class.java
                )

            startActivity(intent)
        }
    }

    /**
     * Sends the user's search term to the iTunes API
     * and displays the returned podcasts.
     */
    private fun searchPodcasts(
        searchTerm: String
    ) {

        // lifecycleScope automatically stops its work
        // if MainActivity is destroyed.
        lifecycleScope.launch {

            try {

                val response =
                    RetrofitClient
                        .api
                        .searchPodcasts(
                            searchTerm
                        )

                // Display all returned podcast results.
                podcastAdapter.updateList(
                    response.results
                )

                // Inform the user when the API
                // returns no podcast results.
                if (
                    response.results.isEmpty()
                ) {

                    Toast.makeText(
                        this@MainActivity,
                        "No podcasts found.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (
                exception: Exception
            ) {

                // Prevent network errors from
                // crashing the application.
                Toast.makeText(
                    this@MainActivity,
                    "Unable to load podcasts.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}