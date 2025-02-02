package com.example.swu_guru2_17

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.swu_guru2_17.databinding.ItemCalendarMemoBinding
import java.io.File

class CalendarMemoAdapter : ListAdapter<Memo, CalendarMemoAdapter.ViewHolder>(CalendarMemoDiffCallback()) {

    class ViewHolder(private val binding: ItemCalendarMemoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(memo: Memo) {
            binding.apply {
                bookTitle.text = memo.title
                bookAuthor.text = memo.author
                bookPublisher.text = memo.publisher

                if (memo.imagePath.isNotEmpty()) {
                    Glide.with(itemView.context)
                        .load(File(memo.imagePath))
                        .placeholder(R.drawable.book_cover)
                        .error(R.drawable.book_cover)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .override(150,200)
                        .into(bookCover)
                } else {
                    bookCover.setImageResource(R.drawable.book_cover)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemCalendarMemoBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class CalendarMemoDiffCallback : DiffUtil.ItemCallback<Memo>() {
    override fun areItemsTheSame(oldItem: Memo, newItem: Memo): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Memo, newItem: Memo): Boolean {
        return oldItem == newItem
    }
}