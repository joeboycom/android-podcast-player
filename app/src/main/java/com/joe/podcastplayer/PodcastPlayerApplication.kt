package com.joe.podcastplayer

import androidx.multidex.MultiDexApplication

class PodcastPlayerApplication: MultiDexApplication() {

    companion object {
        lateinit var application: PodcastPlayerApplication
    }

    init {
        application = this
    }

    override fun onCreate() {
        super.onCreate()
    }
}
