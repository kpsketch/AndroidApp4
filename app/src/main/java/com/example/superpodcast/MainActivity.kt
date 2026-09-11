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

class MainActivity : AppCompatActivity() {

    private lateinit var editTextSearch: EditText
    private lateinit var buttonSearch: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var podcastAdapter: PodcastAdapter

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_main
        )

        // Connect Kotlin variables to the views in activity_main.xml.
        editTextSearch =
            findViewById(
                R.id.editTextSearch
            )

        buttonSearch =
            findViewById(
                R.id.buttonSearch
            )

        recyclerView =
            findViewById(
                R.id.recyclerView
            )

        // Create the RecyclerView adapter.
        // When the user taps a result, open the detail activity.
        podcastAdapter =
            PodcastAdapter { podcast ->

                val intent =
                    Intent(
                        this,
                        PodcastDetailActivity::class.java
                    )

                // Pass the selected podcast data
                // to the detail screen.
                intent.putExtra(
                    "podcastId",
                    podcast.collectionId
                )

                intent.putExtra(
                    "title",
                    podcast.collectionName
                )

                intent.putExtra(
                    "artist",
                    podcast.artistName
                )

                intent.putExtra(
                    "artwork",
                    podcast.artworkUrl100
                )

                intent.putExtra(
                    "feedUrl",
                    podcast.feedUrl
                )

                startActivity(intent)
            }

        // RecyclerView displays results vertically.
        recyclerView.layoutManager =
            LinearLayoutManager(this)

        recyclerView.adapter =
            podcastAdapter

        // Perform a podcast search when Search is pressed.
        buttonSearch.setOnClickListener {

            val searchTerm =
                editTextSearch
                    .text
                    .toString()
                    .trim()

            if (searchTerm.isEmpty()) {

                Toast.makeText(
                    this,
                    "Please enter a podcast to search for.",
                    Toast.LENGTH_SHORT
                ).show()

            } else {

                searchPodcasts(searchTerm)
            }
        }
    }

    // Sends the user's search term to the iTunes API.
    private fun searchPodcasts(
        searchTerm: String
    ) {

        // lifecycleScope automatically stops
        // its work if this Activity is destroyed.
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

                // Prevent a network problem from crashing the app.
                Toast.makeText(
                    this@MainActivity,
                    "Unable to load podcasts.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}