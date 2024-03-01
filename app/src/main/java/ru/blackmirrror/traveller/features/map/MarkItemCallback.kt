package ru.blackmirrror.traveller.features.map

import androidx.recyclerview.widget.DiffUtil.ItemCallback
import ru.blackmirrror.traveller.domain.models.MarkResponse

class MarkItemCallback: ItemCallback<MarkResponse>() {
    override fun areItemsTheSame(oldItem: MarkResponse, newItem: MarkResponse): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: MarkResponse, newItem: MarkResponse): Boolean {
        return oldItem == newItem
    }
}