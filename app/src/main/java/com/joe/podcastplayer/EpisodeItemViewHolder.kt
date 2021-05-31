package com.joe.podcastplayer

import android.text.method.LinkMovementMethod
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.TextView
import com.joe.podcastplayer.base.BaseViewHolder
import com.joe.podcastplayer.databinding.HolderItemEpisodeBinding
import com.joe.podcastplayer.extension.onClick
import com.prof.rssparser.Article
import java.text.SimpleDateFormat
import java.util.*

class EpisodeItemViewHolder(parent: ViewGroup) : BaseViewHolder<HolderItemEpisodeBinding>(fetchViewBinding(parent)) {
    companion object {
        fun newInstance(parent: ViewGroup) = EpisodeItemViewHolder(parent)
    }

    private var onClickListener: ((article: Article) -> Unit)? = null
    private var article: Article? = null

    init {
        viewBinding.cardView.onClick {
            if (article != null) onClickListener?.invoke(article!!)
        }
    }

    fun bind(article: Article) {

        var pubDateString = article.pubDate

        try {
            val sourceDateString = article.pubDate
            val sourceSdf = SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.ENGLISH)
            if (sourceDateString != null) {
                val date = sourceSdf.parse(sourceDateString)
                if (date != null) {
                    val sdf = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
                    pubDateString = sdf.format(date)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        viewBinding.title.text = article.title
        imageLoader.load(url = article.image, iv = viewBinding.image, cornerRadius = 4, placeHolderResId = R.drawable.placeholder)
        viewBinding.pubDate.text = pubDateString

        itemView.setOnClickListener {
            //show article content inside a dialog
            val articleView = WebView(itemView.context)

            articleView.settings.loadWithOverviewMode = true
            articleView.settings.javaScriptEnabled = true
            articleView.isHorizontalScrollBarEnabled = false
            articleView.webChromeClient = WebChromeClient()
            articleView.loadDataWithBaseURL(
                null, "<style>img{display: inline; height: auto; max-width: 100%;} " +

                        "</style>\n" + "<style>iframe{ height: auto; width: auto;}" + "</style>\n" + article.content, null, "utf-8", null
            )

            val alertDialog = androidx.appcompat.app.AlertDialog.Builder(itemView.context).create()
            alertDialog.setTitle(article.title)
            alertDialog.setView(articleView)
            alertDialog.setButton(
                androidx.appcompat.app.AlertDialog.BUTTON_NEUTRAL, "OK"
            ) { dialog, _ -> dialog.dismiss() }
            alertDialog.show()

            (alertDialog.findViewById<View>(android.R.id.message) as TextView).movementMethod = LinkMovementMethod.getInstance()
        }
    }

    fun setOnClickListener(listener: ((article: Article) -> Unit)?) {
        onClickListener = listener
    }
}
