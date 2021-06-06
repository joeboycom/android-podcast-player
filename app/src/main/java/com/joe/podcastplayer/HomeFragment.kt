package com.joe.podcastplayer

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.joe.podcastplayer.base.BaseFragment
import com.joe.podcastplayer.databinding.HomeFragmentBinding
import com.joe.podcastplayer.extension.setVisibility
import com.joe.podcastplayer.extension.toJson
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

    private lateinit var adapter: ArticleAdapter
    private lateinit var viewModel: HomeViewModel
    private var podcastRssUrl = ""
    private var title: String? = null
    private var feedItems: ArrayList<FeedItem> = ArrayList()

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
        adapter = ArticleAdapter()
    }

    override fun initLayout() {
        viewBinding.recyclerView.setSpacing(0, 0, 8, 0, 12, 12, 12, 12)
        viewBinding.recyclerView.useVerticalLayoutManager()
        viewBinding.recyclerView.setHasFixedSize(true)

        viewBinding.swipeRefresh.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark)
        viewBinding.swipeRefresh.canChildScrollUp()
        viewBinding.swipeRefresh.setOnRefreshListener {
            adapter.articles?.clear()
            adapter.notifyDataSetChanged()
            viewBinding.swipeRefresh.isRefreshing = true
            viewModel.fetchFeed()
        }

        viewModel.fetchForUrlAndParseRawData(MainActivity.PODCAST_RSS_URL)
    }

    override fun initAction() {
        adapter.setOnClickListener {
            val fragment = EpisodeFragment.newInstance(title, it.toJson(), feedItems.toJson())
            (activity as MainActivity?)?.showFragment(fragment, TransitionEffect.SLIDE)
        }
    }

    override fun initObserver() {
        viewModel.rssChannel.observe(this, { channel ->
            processChannelData(channel)
        })
    }

    private fun processChannelData(channel: Feed) {
        if (channel.title != null) {
            title = channel.title
        }

        feedItems = channel.articles
        imageLoader.load(channel.image?.url, viewBinding.ivHeaderImage)
        adapter.articles = channel.articles
        viewBinding.recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()
        viewBinding.progLoading.setVisibility(false)
        viewBinding.swipeRefresh.isRefreshing = false
    }
}
