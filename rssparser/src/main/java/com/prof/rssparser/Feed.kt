package com.prof.rssparser

import java.io.Serializable

data class Feed(
        val title: String? = null,
        val link: String? = null,
        val description: String? = null,
        val image: Image? = null,
        val lastBuildDate: String? = null,
        val updatePeriod: String? = null,
        val articles: ArrayList<FeedItem> = ArrayList()
): Serializable