package com.joe.podcastplayer

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import com.prof.rssparser.Parser

class MainActivity : AppCompatActivity() {

    companion object {
        const val PODCAST_URL = "https://feeds.soundcloud.com/users/soundcloud:users:322164009/sounds.rss"
    }
    private lateinit var adapter: ArticleAdapter
    private lateinit var parser: Parser

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val swipeLayout = findViewById<SwipeRefreshLayout>(R.id.swipe_layout)
        val rootLayout = findViewById<RelativeLayout>(R.id.root_layout)

        parser = Parser.Builder()
            .context(this)
            // If you want to provide a custom charset (the default is utf-8):
            // .charset(Charset.forName("ISO-8859-7"))
            .cacheExpirationMillis(24L * 60L * 60L * 100L) // one day
            .build()

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.setHasFixedSize(true)

        viewModel.rssChannel.observe(this, { channel ->
            if (channel != null) {
                if (channel.title != null) {
                    title = channel.title
                }
                adapter = ArticleAdapter(channel.articles)
                recyclerView.adapter = adapter
                adapter.notifyDataSetChanged()
                progressBar.visibility = View.GONE
                swipeLayout.isRefreshing = false
            }
        })

        viewModel.snackbar.observe(this, { value ->
            value?.let {
                Snackbar.make(rootLayout, value, Snackbar.LENGTH_LONG).show()
                viewModel.onSnackbarShowed()
            }
        })

        swipeLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark)
        swipeLayout.canChildScrollUp()
        swipeLayout.setOnRefreshListener {
            adapter.articles.clear()
            adapter.notifyDataSetChanged()
            swipeLayout.isRefreshing = true
            viewModel.fetchFeed(parser)
        }

        if (!isOnline()) {
            val builder = AlertDialog.Builder(this)
            builder.setMessage(R.string.alert_message)
                .setTitle(R.string.alert_title)
                .setCancelable(false)
                .setPositiveButton(R.string.alert_positive
                ) { _, _ -> finish() }

            val alert = builder.create()
            alert.show()
        } else if (isOnline()) {
            viewModel.fetchFeed(parser)
        }

        viewModel.fetchForUrlAndParseRawData(PODCAST_URL)
    }

    @Suppress("DEPRECATION")
    fun isOnline(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                else -> false
            }
        } else {
            val activeNetworkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
            return activeNetworkInfo != null && activeNetworkInfo.isConnected
        }
    }
}