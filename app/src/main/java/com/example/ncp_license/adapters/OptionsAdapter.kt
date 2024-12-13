package com.example.ncp_license.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ncp_license.databinding.ItemOptionBinding

class OptionsAdapter(
    private val onOptionClicked: (Int) -> Unit
) : RecyclerView.Adapter<OptionsAdapter.OptionViewHolder>() {

    private var options: List<String> = emptyList()

    fun submitList(options: List<String>) {
        this.options = options
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OptionViewHolder {
        val binding = ItemOptionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OptionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OptionViewHolder, position: Int) {
        holder.bind(options[position], position)
    }

    override fun getItemCount(): Int = options.size

    inner class OptionViewHolder(private val binding: ItemOptionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(option: String, position: Int) {
            binding.optionText.text = option
            binding.root.setOnClickListener { onOptionClicked(position) }
        }
    }
}