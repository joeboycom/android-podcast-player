package com.joe.podcastplayer.mvvm.viewModel.component

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.joe.podcastplayer.HomeViewModel

class ViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) return HomeViewModel() as T
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}
