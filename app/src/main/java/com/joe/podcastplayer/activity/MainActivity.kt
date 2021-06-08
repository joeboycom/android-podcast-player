package com.joe.podcastplayer.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.joe.podcastplayer.R
import com.joe.podcastplayer.constant.TransitionEffect
import com.joe.podcastplayer.base.BaseActivity
import com.joe.podcastplayer.databinding.ActivityMainBinding
import com.joe.podcastplayer.extension.useStatusBarAndNavigationBar
import com.joe.podcastplayer.fragment.HomeFragment
import org.apache.commons.lang3.Validate

class MainActivity : BaseActivity<ActivityMainBinding>() {

    companion object {
        const val PODCAST_RSS_URL = "https://feeds.soundcloud.com/users/soundcloud:users:322164009/sounds.rss"
    }

    private var homeFragment = HomeFragment.newInstance(PODCAST_RSS_URL)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun init() {
        useStatusBarAndNavigationBar(0xFF201F23.toInt(), false, 0xFF2F2E33.toInt(), false)

        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(viewBinding.contentFrame.id, homeFragment, homeFragment.hashCode().toString())
        transaction.disallowAddToBackStack().commitAllowingStateLoss()
    }

    fun showFragment(fragment: Fragment, transition: TransitionEffect? = TransitionEffect.NONE) {
        Validate.notNull(fragment)
        val transaction = supportFragmentManager.beginTransaction()
        when (transition) {
            TransitionEffect.FADE -> transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
            TransitionEffect.SLIDE -> transaction.setCustomAnimations(
                R.anim.slide_right_in,
                R.anim.slide_left_out,
                R.anim.slide_left_in,
                R.anim.slide_right_out
            )
        }
        transaction.add(R.id.contentFrame, fragment).addToBackStack(null).commitAllowingStateLoss()
    }
}