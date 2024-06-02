package com.dicoding.picodiploma.loginwithanimation.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.picodiploma.loginwithanimation.customview.withDateFormat
import com.dicoding.picodiploma.loginwithanimation.response.ListStory
import com.dicoding.picodiploma.loginwithanimation.databinding.ListStoryBinding

import com.dicoding.picodiploma.loginwithanimation.view.detail.DetailActivity

class StoryAdapter : PagingDataAdapter<ListStory, StoryAdapter.StoryViewHolder>(DIFF_CALLBACK) {

    class StoryViewHolder(private val binding: ListStoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ListStory) {
            Glide.with(binding.root.context)
                .load(data.photoUrl)
                .into(binding.rvStory)

            binding.rvUser.text = data.name
            binding.rvDescription.text = data.description
            binding.rvDate.text = data.createdAt.withDateFormat()

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, DetailActivity::class.java).apply {
                    putExtra(DetailActivity.NAME, data.name)
                    putExtra(DetailActivity.CREATE_AT, data.createdAt)
                    putExtra(DetailActivity.DESCRIPTION, data.description)
                    putExtra(DetailActivity.PHOTO_URL, data.photoUrl)
                    putExtra(DetailActivity.LONGITUDE, data.lon.toString())
                    putExtra(DetailActivity.LATITUDE, data.lat.toString())
                }
                itemView.context.startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(itemView.context as Activity).toBundle())
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding = ListStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val data = getItem(position)
        data?.let {
            holder.bind(it)
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStory>() {
            override fun areItemsTheSame(oldItem: ListStory, newItem: ListStory): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ListStory, newItem: ListStory): Boolean {
                return oldItem == newItem
            }
        }
    }
}
