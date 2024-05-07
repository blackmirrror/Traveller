package ru.blackmirrror.traveller.features.account

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.blackmirrror.traveller.R
import ru.blackmirrror.traveller.domain.models.Subscribe
import ru.blackmirrror.traveller.domain.models.UserResponse

class SubscribeAdapter: ListAdapter<UserResponse, SubscribeAdapter.SubscribeViewHolder>(SubscribeItemCallback()) {

    var onSubscribeItemClickListener: ((UserResponse) -> Unit)? = null

    class SubscribeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name = itemView.findViewById<TextView>(R.id.tv_sub_name)
        val deleteBtn = itemView.findViewById<ImageView>(R.id.btn_sub_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubscribeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_subscribe, parent, false)
        return SubscribeViewHolder(view)
    }

    override fun onBindViewHolder(holder: SubscribeViewHolder, position: Int) {
        val subscribe = getItem(position)
        with(holder) {
            name.text = subscribe.username
            deleteBtn.setOnClickListener {
                onSubscribeItemClickListener?.invoke(subscribe)
            }
        }
    }
}