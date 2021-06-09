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

package com.joe.podcastplayer.adpter

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.joe.podcastplayer.view.EpisodeItemViewHolder
import com.prof.rssparser.FeedItem

class EpisodeAdapter : ListAdapter<FeedItem, EpisodeItemViewHolder>(FeedItem.DiffCallback) {

    private var onClickListener: ((article: FeedItem) -> Unit)? = null
    var feedItemList: ArrayList<FeedItem>? = null
        set(value) {
            field = value
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = EpisodeItemViewHolder.newInstance(parent).apply {
        setOnClickListener {
            this@EpisodeAdapter.onClickListener?.invoke(it)
        }
    }
    override fun getItemCount() = feedItemList?.size ?: 0
    override fun onBindViewHolder(holderEpisodeItem: EpisodeItemViewHolder, position: Int) {
        if (feedItemList.isNullOrEmpty()) return
        holderEpisodeItem.bind(feedItemList!![position])
    }

    fun setOnClickListener(listener: ((article: FeedItem) -> Unit)?) {
        onClickListener = listener
    }
}