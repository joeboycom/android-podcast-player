package com.joe.podcastplayer.service.ui.song

import android.graphics.*
import android.graphics.drawable.Drawable
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.joe.podcastplayer.R
import com.joe.podcastplayer.base.BaseViewHolder
import com.joe.podcastplayer.databinding.ItemSongLayoutBinding
import com.joe.podcastplayer.service.data.Song

class SongListAdapter(private val onClick: (Song) -> Unit) :
    ListAdapter<Song, SongViewHolder>(Song.DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        return SongViewHolder.newInstance(parent, onClick)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val mediaStoreSong = getItem(position)
        holder.bind(mediaStoreSong)
    }
}


class SongViewHolder(parent: ViewGroup, onClick: (Song) -> Unit) :BaseViewHolder<ItemSongLayoutBinding>(fetchViewBinding(parent)) {

    companion object {
        fun newInstance(parent: ViewGroup, onClick: (Song) -> Unit) = SongViewHolder(parent, onClick)
    }
    
    init {
        viewBinding.songLayout.setOnClickListener {
            val song = viewBinding.songLayout.tag as? Song ?: return@setOnClickListener
            onClick(song)
        }
    }

    fun bind(mediaStoreSong: Song) {
        viewBinding.songLayout.tag = mediaStoreSong

        viewBinding.songNameText.text = mediaStoreSong.title
        viewBinding.artistNameText.text = mediaStoreSong.artistName

        Glide.with(activity!!)
            .asBitmap()
            .load(mediaStoreSong.coverPath)
            .thumbnail(0.33f)
            .centerCrop()
            .error(R.drawable.ic_default_cover_icon)
            .into(object : CustomTarget<Bitmap>() {
                override fun onLoadCleared(placeholder: Drawable?) {
                    //Do nothing
                }

                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    viewBinding.songCoverImage.setImageBitmap(resource)
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    super.onLoadFailed(errorDrawable)
                    viewBinding.songCoverImage.setBackgroundResource(R.drawable.ic_default_cover_background)
                    viewBinding.songCoverImage.setImageDrawable(errorDrawable)
                }
            })
    }

}
