package com.example.swu_guru2_17

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.swu_guru2_17.databinding.ItemMemoBinding
import java.io.File

class MemoListAdapter(
    private val onDeleteClick: (Memo) -> Unit,
    private val onEditClick: (Memo) -> Unit,
    private val onCompletedChange: (Memo, Boolean) -> Unit
) : ListAdapter<Memo, MemoListAdapter.MemoViewHolder>(MemoDiffCallback()) {

    class MemoViewHolder(private val binding: ItemMemoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            memo: Memo,
            onDeleteClick: (Memo) -> Unit,
            onEditClick: (Memo) -> Unit,
            onCompletedChange: (Memo, Boolean) -> Unit
        ) {
            binding.apply {
                textTitle.text = memo.title
                textAuthor.text = memo.author
                textPublisher.text = memo.publisher
                textMemo.text = memo.memo
                textDate.text = memo.date
                checkboxCompleted.isChecked = memo.isCompleted

                if (memo.imagePath.isNotEmpty()) {
                    Glide.with(root.context)
                        .load(File(memo.imagePath))
                        .placeholder(R.drawable.book_cover)
                        .error(R.drawable.book_cover)
                        .into(imageBookCover)
                } else {
                    imageBookCover.setImageResource(R.drawable.book_cover)
                }

                buttonDelete.setOnClickListener { onDeleteClick(memo) }
                buttonEdit.setOnClickListener { onEditClick(memo) }
                checkboxCompleted.setOnCheckedChangeListener { _, isChecked ->
                    onCompletedChange(memo, isChecked)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoViewHolder {
        val binding = ItemMemoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MemoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MemoViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem, onDeleteClick, onEditClick, onCompletedChange)
    }
}

class MemoDiffCallback : DiffUtil.ItemCallback<Memo>() {
    override fun areItemsTheSame(oldItem: Memo, newItem: Memo): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Memo, newItem: Memo): Boolean {
        return oldItem == newItem
    }
}