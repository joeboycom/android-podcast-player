package com.joe.podcastplayer.service

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.joe.podcastplayer.R
import com.joe.podcastplayer.service.ui.song.SongListFragment

class MainActivity1 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main1)
        setSupportActionBar(findViewById(R.id.toolbar))
    }

    override fun onStart() {
        super.onStart()
        if (intent?.categories?.contains("android.shortcut.play.song") == true) {
            val navHostFragment =
                supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            val songListFragment =
                navHostFragment.childFragmentManager.fragments[0] as? SongListFragment
            songListFragment?.playSongDirectly()
        }
    }
}