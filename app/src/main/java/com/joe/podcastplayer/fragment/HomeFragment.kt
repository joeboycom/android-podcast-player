package com.joe.podcastplayer.fragment

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.joe.podcastplayer.*
import com.joe.podcastplayer.activity.MainActivity
import com.joe.podcastplayer.adpter.EpisodeAdapter
import com.joe.podcastplayer.base.BaseFragment
import com.joe.podcastplayer.constant.TransitionEffect
import com.joe.podcastplayer.databinding.HomeFragmentBinding
import com.joe.podcastplayer.extension.*
import com.joe.podcastplayer.viewModel.HomeViewModel
import com.prof.rssparser.Feed
import com.prof.rssparser.FeedItem


class HomeFragment : BaseFragment<HomeFragmentBinding>() {

    companion object {
        private const val BUNDLE_PODCAST_RSS_URL = "BUNDLE_PODCAST_RSS_URL"
        fun newInstance(podcastRssUrl: String): HomeFragment = HomeFragment().apply {
            val bundle = Bundle()
            bundle.putString(BUNDLE_PODCAST_RSS_URL, podcastRssUrl)
            arguments = bundle
        }
    }

    private lateinit var adapter: EpisodeAdapter
    private lateinit var viewModel: HomeViewModel
    private var feedItems: ArrayList<FeedItem> = ArrayList()
    private var podcastRssUrl = ""
    private var title: String? = null
    private var isDescriptionExpanded = false

    override fun enableEventBus(): Boolean = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun initName() {
        // set screen name
    }

    override fun initIntent() {
        val bundle = arguments
        podcastRssUrl = bundle!!.getString(BUNDLE_PODCAST_RSS_URL, "")
    }

    override fun init() {
        viewModel = ViewModelProvider(baseActivity!!, viewModelFactory).get(HomeViewModel::class.java)
        adapter = EpisodeAdapter()
    }

    override fun initLayout() {
        viewBinding.ivExpand.setBackgroundResource(R.drawable.ic_baseline_expand_more_24)

        viewBinding.recyclerView.setSpacing(0, 12, 8, 0, 12, 12, 12, 12)
        viewBinding.recyclerView.useVerticalLayoutManager()
        viewBinding.recyclerView.setHasFixedSize(true)

        viewBinding.swipeRefresh.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark)
        viewBinding.swipeRefresh.canChildScrollUp()
        viewBinding.swipeRefresh.setOnRefreshListener {
            adapter.notifyDataSetChanged()
            viewBinding.swipeRefresh.isRefreshing = true
            viewModel.fetchForUrlAndParseRawData(MainActivity.PODCAST_RSS_URL)
        }

        viewModel.fetchForUrlAndParseRawData(MainActivity.PODCAST_RSS_URL)
    }

    override fun initAction() {
        viewBinding.flExpand.onClick {
            if (!isDescriptionExpanded) {
                isDescriptionExpanded = true
                val animation = ObjectAnimator.ofInt(viewBinding.tvChannelDescription, "maxLines", 40)
                animation.setDuration(200).start()
                viewBinding.ivExpand.setBackgroundResource(R.drawable.ic_baseline_expand_less_24)
            } else {
                isDescriptionExpanded = false
                val animation = ObjectAnimator.ofInt(viewBinding.tvChannelDescription, "maxLines", 2)
                animation.setDuration(200).start()
                viewBinding.ivExpand.setBackgroundResource(R.drawable.ic_baseline_expand_more_24)
            }
        }

        adapter.setOnClickListener {
            val fragment = EpisodeFragment.newInstance(title, it.toJson(), feedItems.toJson())
            (activity as MainActivity?)?.showFragment(fragment, TransitionEffect.SLIDE)
        }
    }

    override fun initObserver() {
        viewModel.rssSuccessLiveData.observe(this, { channel ->
            processChannelData(channel)
        })
        viewModel.rssFailLiveData.observe(this, { text ->
            Toast.makeText(baseActivity, text, Toast.LENGTH_SHORT).show()
        })
    }

    private fun processChannelData(channel: Feed) {
        title = channel.title

        feedItems = channel.articles
        adapter.feedItemList = channel.articles
        adapter.notifyDataSetChanged()
        viewBinding.tvChannelTitle.text = title
        viewBinding.tvChannelDescription.text = channel.description
        viewBinding.recyclerView.adapter = adapter
        viewBinding.swipeRefresh.isRefreshing = false
        viewBinding.loadingSpinner.setVisibility(false)
        viewBinding.ivExpand.setVisibility(true)
        viewBinding.ivHeaderImage.load(channel.image?.url)
    }
}
