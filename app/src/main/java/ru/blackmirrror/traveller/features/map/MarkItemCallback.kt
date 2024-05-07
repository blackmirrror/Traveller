package ru.blackmirrror.traveller.features.map

import androidx.recyclerview.widget.DiffUtil.ItemCallback
import ru.blackmirrror.traveller.domain.models.Mark
import ru.blackmirrror.traveller.domain.models.MarkLocal

class MarkItemCallback: ItemCallback<MarkLocal>() {

    override fun areItemsTheSame(oldItem: MarkLocal, newItem: MarkLocal): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: MarkLocal, newItem: MarkLocal): Boolean {
        return oldItem == newItem
    }
}