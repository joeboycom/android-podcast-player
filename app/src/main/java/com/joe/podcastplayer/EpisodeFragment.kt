package com.joe.podcastplayer

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.os.postDelayed
import androidx.recyclerview.widget.RecyclerView
import com.joe.podcastplayer.base.BaseFragment
import com.joe.podcastplayer.databinding.HomeFragmentBinding
import com.joe.podcastplayer.extension.className
import com.prof.rssparser.Parser

class EpisodeFragment : BaseFragment<HomeFragmentBinding>() {

    companion object {
        private const val BUNDLE_CHANNEL_TEXT = "BUNDLE_CHANNEL_TEXT"
        private const val BUNDLE_IMAGE_URL = "BUNDLE_IMAGE_URL"
        private const val BUNDLE_DESCRIPTION = "BUNDLE_DESCRIPTION"
        fun newInstance(channelText: String, imageUrl: String, description: String): EpisodeFragment = EpisodeFragment().apply {
            val bundle = Bundle()
            bundle.putString(BUNDLE_CHANNEL_TEXT, channelText)
            bundle.putString(BUNDLE_IMAGE_URL, imageUrl)
            bundle.putString(BUNDLE_DESCRIPTION, description)
            arguments = bundle
        }
    }

    private lateinit var adapter: ArticleAdapter
    private lateinit var parser: Parser
    private var requestRefreshFromFirstIn = true
    private var channelText = ""
    private var imageUrl = ""
    private var description = ""

    override fun enableEventBus(): Boolean = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun initName() {
        // set screen name
    }

    override fun initIntent() {
        val bundle = arguments
        channelText = bundle!!.getString(BUNDLE_CHANNEL_TEXT, "")
        imageUrl = bundle.getString(BUNDLE_IMAGE_URL, "")
        description = bundle.getString(BUNDLE_DESCRIPTION, "")
    }

    override fun init() {
//        myCoursesAdapter = MyCoursesAdapter {
//            analyticsCenter.sendEvent(screenName, "click_retry")
//            viewModel.retry()
//        }

//        parser = Parser.Builder()
//            .context(this)
//            // If you want to provide a custom charset (the default is utf-8):
//            // .charset(Charset.forName("ISO-8859-7"))
//            .cacheExpirationMillis(24L * 60L * 60L * 100L) // one day
//            .build()
    }

    override fun initLayout() {
//        viewBinding.swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(coreActivity!!, R.color.colorHeroPrimary))
//        viewBinding.recyclerView.setSpacing(8, 8, 8, 0, 16, 0, 16, 0)
//        viewBinding.recyclerView.useVerticalLayoutManager()
//        viewBinding.recyclerView.adapter = adapter
//        viewBinding.recyclerView.setHasFixedSize(true)
    }

    override fun initAction() {
//        myCoursesAdapter!!.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
//            override fun onChanged() = updateArea()
//            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) = updateArea()
//            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) = updateArea()
//        })
//
//        viewBinding.swipeRefreshLayout.setOnRefreshListener {
//            analyticsCenter.sendEvent(screenName, "swipe_to_refresh")
//            handler.postDelayed(1000L) {
//                viewModel.refresh()
//                viewBinding.swipeRefreshLayout.isRefreshing = false
//            }
//        }
    }

    override fun initObserver() {
//        viewModel.myCoursesLiveData.observe(coreActivity!!) { myCoursesAdapter!!.submitList(it) }
//        viewModel.networkStateLiveData.observe(coreActivity!!) { myCoursesAdapter!!.setNetworkState(it) }
    }

//    private fun updateArea() {
//        if (!isViewCreated) return
//        val isEmpty = myCoursesAdapter!!.itemCount == 0
//        viewBinding.llAreaEmpty.setVisibility(isEmpty)
//        viewBinding.flAreaList.setVisibility(true)
//    }
//
//    private fun handleRefreshOnVisible() {
//        if (!isViewCreated) return
//        if (!requestRefreshFromFirstIn) return
//        requestRefreshFromFirstIn = false
//        viewModel.refresh()
//    }
//
//    fun refresh() {
//        viewModel.refresh()
//    }
//
//    fun activate() {
//        Log.i(className, "activate\tisViewCreated:$isViewCreated")
//        if (!isViewCreated) return
//        updateArea()
//        handleRefreshOnVisible()
//    }
//
//    override fun onResume() {
//        super.onResume()
//        Log.i(className, "onResume\tisViewCreated:$isViewCreated")
//        if (!isViewCreated) return
//        updateArea()
//        handleRefreshOnVisible()
//    }
}
