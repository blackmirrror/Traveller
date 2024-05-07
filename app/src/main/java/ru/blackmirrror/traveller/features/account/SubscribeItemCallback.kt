package ru.blackmirrror.traveller.features.account

import androidx.recyclerview.widget.DiffUtil.ItemCallback
import ru.blackmirrror.traveller.domain.models.UserResponse

class SubscribeItemCallback: ItemCallback<UserResponse>() {

    override fun areItemsTheSame(oldItem: UserResponse, newItem: UserResponse): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: UserResponse, newItem: UserResponse): Boolean {
        return oldItem == newItem
    }
}