package com.example.part4_chapter5.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.part4_chapter5.data.entity.GithubRepoEntity
import com.example.part4_chapter5.databinding.RepositoryItemBinding
import com.example.part4_chapter5.extensions.loadCenterInside

class RepositoryRecyclerAdapter(private val clicked:(GithubRepoEntity) -> Unit) :
    ListAdapter<GithubRepoEntity, RepositoryRecyclerAdapter.ItemViewHolder>(diffUtil) {
    inner class ItemViewHolder(val binding: RepositoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: GithubRepoEntity) = with(binding) {
            ownerProfileImageView.loadCenterInside(item.owner.avatarUrl, 24f)
            ownerNameTextView.text =item.owner.login
            nameTextView.text = item.fullName
            subtextTextView.text=item.description
            stargazersCountText.text = item.stargazerCount.toString()
            item.language.let { language ->
                languageText.isGone =false
                languageText.text = language
            } ?:kotlin.run {
                languageText.isVisible = true
                languageText.text= ""
            }
            itemView.setOnClickListener {
                clicked(item)
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            RepositoryItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<GithubRepoEntity>() {
            override fun areItemsTheSame(
                oldItem: GithubRepoEntity,
                newItem: GithubRepoEntity
            ): Boolean {
                return oldItem.fullName == newItem.fullName
            }

            override fun areContentsTheSame(
                oldItem: GithubRepoEntity,
                newItem: GithubRepoEntity
            ): Boolean {
                return oldItem == newItem
            }

        }
    }
}