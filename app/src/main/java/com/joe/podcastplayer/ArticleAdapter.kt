/*
 *   Copyright 2016 Marco Gomiero
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package com.joe.podcastplayer

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.prof.rssparser.FeedItem

class ArticleAdapter : RecyclerView.Adapter<EpisodeItemViewHolder>() {

    private var onClickListener: ((article: FeedItem) -> Unit)? = null
    var articles: ArrayList<FeedItem>? = null
        set(value) {
            field = value
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = EpisodeItemViewHolder.newInstance(parent).apply {
        setOnClickListener {
            this@ArticleAdapter.onClickListener?.invoke(it)
        }
    }
    override fun getItemCount() = articles?.size ?: 0
    override fun onBindViewHolder(holderEpisode: EpisodeItemViewHolder, position: Int) {
        if (articles.isNullOrEmpty()) return
        holderEpisode.bind(articles!![position])
    }

    fun setOnClickListener(listener: ((article: FeedItem) -> Unit)?) {
        onClickListener = listener
    }
}