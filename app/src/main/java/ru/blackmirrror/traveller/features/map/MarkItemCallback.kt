package ru.blackmirrror.traveller.features.map

import androidx.recyclerview.widget.DiffUtil.ItemCallback
import ru.blackmirrror.traveller.domain.models.Mark

class MarkItemCallback: ItemCallback<Mark>() {
    override fun areItemsTheSame(oldItem: Mark, newItem: Mark): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Mark, newItem: Mark): Boolean {
        return oldItem == newItem
    }
}