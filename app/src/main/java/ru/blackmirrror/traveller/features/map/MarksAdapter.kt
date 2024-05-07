package ru.blackmirrror.traveller.features.map

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.blackmirrror.traveller.R
import ru.blackmirrror.traveller.domain.models.Mark
import ru.blackmirrror.traveller.domain.models.MarkLocal
import ru.blackmirrror.traveller.features.utils.TextFormatter

class MarksAdapter: ListAdapter<MarkLocal, MarksAdapter.MarksViewHolder>(MarkItemCallback()) {

    var onMarkItemClickListener: ((MarkLocal) -> Unit)? = null
    var onLikeClickListener: ((MarkLocal) -> Unit)? = null

    class MarksViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val description = itemView.findViewById<TextView>(R.id.tv_mark_description)
        val likesAndAuthor = itemView.findViewById<TextView>(R.id.tv_mark_author_and_likes)
        val like = itemView.findViewById<ImageButton>(R.id.item_favorite_btn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarksViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_mark, parent, false)
        return MarksViewHolder(view)
    }

    override fun onBindViewHolder(holder: MarksViewHolder, position: Int) {
        val mark = getItem(position)
        with(holder) {
            description.text = mark.description
            likesAndAuthor.text = TextFormatter.likesAndAuthorToText(mark.likes, mark.user)
            if (mark.isFavorite)
                like.setImageResource(R.drawable.ic_favorite_is)
            else
                like.setImageResource(R.drawable.ic_favorite)
            itemView.setOnClickListener {
                onMarkItemClickListener?.invoke(mark)
            }
            like.setOnClickListener {
                onLikeClickListener?.invoke(mark)
            }
        }
    }
}