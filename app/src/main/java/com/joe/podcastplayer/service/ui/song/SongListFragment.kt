package com.joe.podcastplayer.service.ui.song

import android.Manifest
import android.content.pm.PackageManager
import android.os.Handler
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.joe.podcastplayer.base.BaseFragment
import com.joe.podcastplayer.databinding.FragmentSongListBinding
import com.joe.podcastplayer.service.data.Song
import com.joe.podcastplayer.service.di.InjectorUtils
import com.joe.podcastplayer.service.ui.nowplaying.NowPlayingFragment

class SongListFragment : BaseFragment<FragmentSongListBinding>(){

    companion object {
        private const val READ_EXTERNAL_STORAGE_REQUEST = 1
        fun newInstance() = SongListFragment()
    }

    private val episodeViewModel: EpisodeViewModel by viewModels() {
        InjectorUtils.provideSongListViewModel(requireContext())
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            READ_EXTERNAL_STORAGE_REQUEST -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showSongs()
                }
            }
        }
    }

    override fun initName() {
    }

    override fun initIntent() {
    }

    override fun init() {
    }

    override fun initLayout() {
        val songAdapter = SongListAdapter {
            clickSong(it)
        }

        with(viewBinding.songRecyclerView) {
            layoutManager = LinearLayoutManager(activity)
            adapter = songAdapter
        }
        episodeViewModel.songs.observe(this.viewLifecycleOwner, Observer<List<Song>> { songs ->
            songAdapter.submitList(songs)
        })

        openMediaStore()
    }

    override fun initAction() {

    }

    override fun initObserver() {

    }

    fun playSongDirectly() {
        Handler().postDelayed({
            clickSong(episodeViewModel.songs.value?.getOrNull(0) ?: return@postDelayed)
        }, 2000)
    }

    private fun haveStoragePermission() =
        ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

    private fun requestPermission() {
        if (!haveStoragePermission()) {
            val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
            requestPermissions(
                permissions,
                READ_EXTERNAL_STORAGE_REQUEST
            )
        }
    }

    private fun openMediaStore() {
        if (haveStoragePermission()) {
            showSongs()
        } else {
            requestPermission()
        }
    }

    private fun showSongs() {
//        episodeViewModel.loadSongs()
    }

    private fun clickSong(song: Song) {
//        songListViewModel.playMedia(song)
        showNowPlaying()
    }

    private fun showNowPlaying() {
        with(parentFragmentManager.beginTransaction()) {
            val fragment = parentFragmentManager.findFragmentByTag(NowPlayingFragment.TAG)
            if (fragment == null) {
//                add(
//                    R.id.nav_host_fragment,
//                    NowPlayingFragment.newInstance(),
//                    NowPlayingFragment.TAG
//                )
                commit()
            }
        }
    }
}