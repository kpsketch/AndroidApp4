package com.example.superpodcast.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.superpodcast.R
import com.example.superpodcast.data.PodcastEntity

/**
 * Assignment 8 - Room Database
 *
 * SubscriptionAdapter displays podcasts that have
 * been saved in the Room database.
 *
 * Each podcast is displayed inside the
 * My Subscriptions RecyclerView.
 *
 * When the user taps a podcast, the selected
 * PodcastEntity is sent back to SubscriptionsActivity.
 */
class SubscriptionAdapter(
    private val onPodcastClick: (PodcastEntity) -> Unit
) : RecyclerView.Adapter<SubscriptionAdapter.SubscriptionViewHolder>() {

    // List containing podcasts retrieved from Room.
    private var subscriptions:
            List<PodcastEntity> = emptyList()

    /**
     * ViewHolder stores references to the views
     * used for one subscription row.
     */
    class SubscriptionViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        val imageArtwork: ImageView =
            itemView.findViewById(
                R.id.imageSubscriptionArtwork
            )

        val textTitle: TextView =
            itemView.findViewById(
                R.id.textSubscriptionTitle
            )

        val textArtist: TextView =
            itemView.findViewById(
                R.id.textSubscriptionArtist
            )
    }

    /**
     * Creates the layout for each podcast
     * displayed in the RecyclerView.
     */
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SubscriptionViewHolder {

        val view =
            LayoutInflater
                .from(parent.context)
                .inflate(
                    R.layout.item_subscription,
                    parent,
                    false
                )

        return SubscriptionViewHolder(
            view
        )
    }

    /**
     * Places podcast information into
     * each RecyclerView row.
     */
    override fun onBindViewHolder(
        holder: SubscriptionViewHolder,
        position: Int
    ) {

        val podcast =
            subscriptions[position]

        // Display podcast title.
        holder.textTitle.text =
            podcast.title

        // Display podcast artist/publisher.
        holder.textArtist.text =
            podcast.artist

        // Load podcast artwork from its URL.
        Glide.with(
            holder.itemView.context
        )
            .load(
                podcast.artworkUrl
            )
            .into(
                holder.imageArtwork
            )

        // Open the podcast details when
        // the user taps this subscription.
        holder.itemView.setOnClickListener {

            onPodcastClick(
                podcast
            )
        }
    }

    /**
     * Returns the number of subscriptions
     * currently displayed.
     */
    override fun getItemCount(): Int {

        return subscriptions.size
    }

    /**
     * Updates the RecyclerView with the latest
     * subscriptions from the Room database.
     */
    fun submitList(
        newSubscriptions: List<PodcastEntity>
    ) {

        subscriptions =
            newSubscriptions

        // Tell RecyclerView that its data
        // has changed and should be redrawn.
        notifyDataSetChanged()
    }
}