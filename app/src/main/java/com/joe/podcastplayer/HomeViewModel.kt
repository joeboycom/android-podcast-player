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

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.joe.podcastplayer.base.BaseViewModel
import com.prof.rssparser.Feed
import com.prof.rssparser.Parser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request

class HomeViewModel : BaseViewModel() {

    private val url = "https://feeds.soundcloud.com/users/soundcloud:users:322164009/sounds.rss"
    private var parser: Parser = Parser.Builder()
        .context(PodcastPlayerApplication.application)
        // If you want to provide a custom charset (the default is utf-8):
        // .charset(Charset.forName("ISO-8859-7"))
        .cacheExpirationMillis(24L * 60L * 60L * 100L) // one day
        .build()

    private val rssFailMutableLiveData = MutableLiveData<String>()
    val rssFailLiveData: LiveData<String>
        get() = rssFailMutableLiveData

    private val rssSuccessMutableLiveData = MutableLiveData<Feed>()
    val rssSuccessLiveData: LiveData<Feed>
        get() = rssSuccessMutableLiveData

    private val okHttpClient by lazy {
        OkHttpClient()
    }

    fun fetchFeed() {
        viewModelScope.launch {
            try {
                val channel = parser.getChannel(url)
                rssSuccessMutableLiveData.postValue(channel)
            } catch (e: Exception) {
                e.printStackTrace()
                rssFailMutableLiveData.value = "An error has occurred. Please retry"
                rssSuccessMutableLiveData.postValue(Feed(null, null, null, null, null, null, arrayListOf()))
            }
        }
    }

    fun fetchForUrlAndParseRawData(url: String) {
        val parser = Parser.Builder().build()

        viewModelScope.launch(Dispatchers.IO) {
            val request = Request.Builder().url(url).build()
            val result = okHttpClient.newCall(request).execute()
            val raw = runCatching { result.body()?.string() }.getOrNull()
            if (raw == null) {
                rssFailMutableLiveData.postValue("Something went wrong!")
            } else {
                val channel = parser.parse(raw)
                rssSuccessMutableLiveData.postValue(channel)
            }
        }
    }
}
