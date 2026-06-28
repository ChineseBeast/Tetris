package com.example.tetris.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tetris.databinding.ItemRecordBinding
import com.example.tetris.logic.local.Entity.GameRecord
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date

// 实现自己的Adapter
class RecordAdapter(
    // 这里相当于onNoteClick 里面有一个方法，接收 GameRecord，无返回值，默认不传的时候，值就是 null
    private val onNoteClick: ((GameRecord) -> Unit)? = null
): ListAdapter<GameRecord, RecordAdapter.ViewHolder>(DiffCallback())  {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    private val numberFormat = NumberFormat.getNumberInstance(Locale.US)

    inner class ViewHolder(private val binding: ItemRecordBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(record: GameRecord){
            binding.tvDate.text = dateFormat.format(Date(record.playDate))
            binding.tvScore.text = numberFormat.format(record.score)
            binding.tvDuration.text = formatDuration(record.duration)
            binding.tvNote.text = "备注"
            binding.tvNote.setOnClickListener {
                onNoteClick?.invoke(record)
            }
            binding.root.setOnLongClickListener {
                onNoteClick?.invoke(record)
                true
            }
        }
        private fun formatDuration(totalSeconds: Int): String {
            val h = totalSeconds / 3600
            val m = (totalSeconds % 3600) / 60
            val s = totalSeconds % 60
            return if (h > 0) {
                String.format("%d:%02d:%02d", h, m, s)
            } else {
                String.format("%02d:%02d", m, s)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRecordBinding.inflate(
            LayoutInflater.from(parent.context),parent,false
        )
        return ViewHolder(binding)
    }
    // 进行item绑定
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<GameRecord>() {
        override fun areItemsTheSame(oldItem: GameRecord, newItem: GameRecord): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: GameRecord, newItem: GameRecord): Boolean =
            oldItem == newItem
    }
}