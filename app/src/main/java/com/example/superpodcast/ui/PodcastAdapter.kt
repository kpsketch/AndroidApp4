package com.example.superpodcast.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.superpodcast.R
import com.example.superpodcast.data.Podcast

// Adapter used to display podcast search results in the RecyclerView.
class PodcastAdapter(
    private var items: List<Podcast> = emptyList(),

    // MainActivity provides the action to perform when a podcast is selected.
    private val onItemClick: (Podcast) -> Unit

) : RecyclerView.Adapter<
        PodcastAdapter.PodcastViewHolder
        >() {

    // Replaces the previous podcast results and refreshes the list.
    fun updateList(
        newItems: List<Podcast>
    ) {

        items = newItems

        notifyDataSetChanged()
    }

    // Creates the row layout used for each podcast result.
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PodcastViewHolder {

        val view =
            LayoutInflater
                .from(parent.context)
                .inflate(
                    R.layout.item_podcast,
                    parent,
                    false
                )

        return PodcastViewHolder(view)
    }

    // Connects one Podcast object to one visible RecyclerView row.
    override fun onBindViewHolder(
        holder: PodcastViewHolder,
        position: Int
    ) {

        holder.bind(
            podcast = items[position],
            onItemClick = onItemClick
        )
    }

    // RecyclerView uses this value to determine
    // how many podcast rows should be displayed.
    override fun getItemCount(): Int {

        return items.size
    }

    class PodcastViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        private val imageArtwork: ImageView =
            itemView.findViewById(
                R.id.imageViewArtwork
            )

        private val textTitle: TextView =
            itemView.findViewById(
                R.id.textViewTitle
            )

        private val textArtist: TextView =
            itemView.findViewById(
                R.id.textViewArtist
            )

        // Places podcast information into the current row.
        fun bind(
            podcast: Podcast,
            onItemClick: (Podcast) -> Unit
        ) {

            textTitle.text =
                podcast.collectionName

            textArtist.text =
                podcast.artistName

            // Glide downloads and displays
            // the podcast artwork from its URL.
            Glide.with(itemView.context)
                .load(podcast.artworkUrl100)
                .into(imageArtwork)

            // Send the selected podcast back to MainActivity.
            itemView.setOnClickListener {

                onItemClick(podcast)
            }
        }
    }
}