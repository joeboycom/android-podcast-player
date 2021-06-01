package com.joe.podcastplayer

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.joe.podcastplayer.base.BaseFragment
import com.joe.podcastplayer.databinding.HomeFragmentBinding
import com.joe.podcastplayer.extension.setVisibility
import com.prof.rssparser.Channel
import com.prof.rssparser.Parser

class HomeFragment : BaseFragment<HomeFragmentBinding>() {

    companion object {
        private const val BUNDLE_PODCAST_RSS_URL = "BUNDLE_PODCAST_RSS_URL"
//        private const val BUNDLE_IMAGE_URL = "BUNDLE_IMAGE_URL"
//        private const val BUNDLE_DESCRIPTION = "BUNDLE_DESCRIPTION"
        fun newInstance(podcastRssUrl: String): HomeFragment = HomeFragment().apply {
            val bundle = Bundle()
            bundle.putString(BUNDLE_PODCAST_RSS_URL, podcastRssUrl)
//            bundle.putString(BUNDLE_IMAGE_URL, imageUrl)
//            bundle.putString(BUNDLE_DESCRIPTION, description)
//    channelText: String, imageUrl: String, description: String
            arguments = bundle
        }
    }

    private lateinit var adapter: ArticleAdapter
    private lateinit var parser: Parser
    private lateinit var viewModel: HomeViewModel
    private var requestRefreshFromFirstIn = true
    private var podcastRssUrl = ""
    private var imageUrl = ""
    private var description = ""
    private var title: String? = null

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
//        imageUrl = bundle.getString(BUNDLE_IMAGE_URL, "")
//        description = bundle.getString(BUNDLE_DESCRIPTION, "")
    }

    override fun init() {
        viewModel = ViewModelProvider(baseActivity!!, viewModelFactory).get(HomeViewModel::class.java)
        adapter = ArticleAdapter()
    }

    override fun initLayout() {
        viewBinding.recyclerView.setSpacing(0, 0, 8, 0, 12, 12, 12, 12)
        viewBinding.recyclerView.useVerticalLayoutManager()
        viewBinding.recyclerView.setHasFixedSize(true)

//        viewModel.snackbar.observe(this, { value ->
//            value?.let {
//                Snackbar.make(rootLayout, value, Snackbar.LENGTH_LONG).show()
//                viewModel.onSnackbarShowed()
//            }
//        })

        viewBinding.swipeRefresh.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark)
        viewBinding.swipeRefresh.canChildScrollUp()
        viewBinding.swipeRefresh.setOnRefreshListener {
            adapter.articles?.clear()
            adapter.notifyDataSetChanged()
            viewBinding.swipeRefresh.isRefreshing = true
            viewModel.fetchFeed()
        }

//        if (!isOnline()) {
//            val builder = AlertDialog.Builder(this)
//            builder.setMessage(R.string.alert_message)
//                .setTitle(R.string.alert_title)
//                .setCancelable(false)
//                .setPositiveButton(R.string.alert_positive
//                ) { _, _ -> finish() }
//
//            val alert = builder.create()
//            alert.show()
//        } else if (isOnline()) {
//            viewModel.fetchFeed()
//        }

        viewModel.fetchForUrlAndParseRawData(MainActivity.PODCAST_RSS_URL)
    }

    override fun initAction() {
        adapter.setOnClickListener {
            val fragment = EpisodeFragment.newInstance(title, it.title, it.image, it.description)
            (activity as MainActivity?)?.showFragment(fragment, TransitionEffect.SLIDE)
        }
    }

    override fun initObserver() {
        viewModel.rssChannel.observe(this, { channel ->
            processChannelData(channel)
        })
    }

    private fun processChannelData(channel: Channel) {
        if (channel.title != null) {
            title = channel.title
        }

        imageLoader.load(channel.image?.url, viewBinding.headerImageView)
        adapter.articles = channel.articles
        viewBinding.recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()
        viewBinding.progLoading.setVisibility(false)
        viewBinding.swipeRefresh.isRefreshing = false
    }
}
