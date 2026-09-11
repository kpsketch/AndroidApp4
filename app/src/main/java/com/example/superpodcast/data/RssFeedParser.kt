package com.example.superpodcast.data

import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import java.net.URL

// Reads a podcast RSS feed and finds the newest playable episode.
object RssFeedParser {

    fun getLatestEpisode(
        feedUrl: String
    ): PodcastEpisode? {

        return try {

            // Open a connection to the podcast's RSS feed.
            val connection =
                URL(feedUrl).openConnection()

            connection.connectTimeout = 10000
            connection.readTimeout = 10000

            val inputStream =
                connection.getInputStream()

            // Android's XML parser is used to read RSS data.
            val parser =
                Xml.newPullParser()

            parser.setInput(
                inputStream,
                null
            )

            var eventType =
                parser.eventType

            var insideItem = false
            var episodeTitle: String? = null
            var audioUrl: String? = null

            while (
                eventType !=
                XmlPullParser.END_DOCUMENT
            ) {

                when (eventType) {

                    XmlPullParser.START_TAG -> {

                        when (
                            parser.name.lowercase()
                        ) {

                            // Each item represents one podcast episode.
                            "item" -> {
                                insideItem = true
                                episodeTitle = null
                                audioUrl = null
                            }

                            // Save the title of the current episode.
                            "title" -> {

                                if (insideItem) {
                                    episodeTitle =
                                        parser.nextText()
                                }
                            }

                            // The enclosure URL normally contains
                            // the playable podcast audio file.
                            "enclosure" -> {

                                if (insideItem) {

                                    audioUrl =
                                        parser.getAttributeValue(
                                            null,
                                            "url"
                                        )
                                }
                            }
                        }
                    }

                    XmlPullParser.END_TAG -> {

                        // Return the first complete playable item.
                        // Podcast feeds normally list newest items first.
                        if (
                            parser.name.equals(
                                "item",
                                ignoreCase = true
                            ) &&
                            insideItem
                        ) {

                            if (
                                !episodeTitle.isNullOrEmpty() &&
                                !audioUrl.isNullOrEmpty()
                            ) {

                                inputStream.close()

                                return PodcastEpisode(
                                    title = episodeTitle,
                                    audioUrl = audioUrl
                                )
                            }

                            insideItem = false
                        }
                    }
                }

                eventType =
                    parser.next()
            }

            inputStream.close()

            null

        } catch (exception: Exception) {

            // Returning null lets the Activity display
            // a friendly error rather than crashing.
            null
        }
    }
}