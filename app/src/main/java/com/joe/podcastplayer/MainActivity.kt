package com.joe.podcastplayer

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.joe.podcastplayer.base.BaseActivity
import com.joe.podcastplayer.databinding.ActivityMainBinding

class MainActivity : BaseActivity<ActivityMainBinding>() {

    companion object {
        const val PODCAST_RSS_URL = "https://feeds.soundcloud.com/users/soundcloud:users:322164009/sounds.rss"
    }

    private var homeFragment = HomeFragment.newInstance(PODCAST_RSS_URL)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        showFragment(homeFragment)
    }

    override fun init() {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(viewBinding.contentFrame.id, homeFragment, homeFragment.hashCode().toString())
        transaction.disallowAddToBackStack().commitAllowingStateLoss()
    }

    private fun showFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        if (fragment is HomeFragment) transaction.show(homeFragment) else transaction.hide(homeFragment)
        transaction.disallowAddToBackStack().commitAllowingStateLoss()
    }
}