package com.redgunner.worddroid.adapter

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.redgunner.worddroid.R
import com.redgunner.worddroid.models.comments.Comments
import kotlinx.android.synthetic.main.comments_view_holder_layout.view.*

class CommentsListAdapter :ListAdapter<Comments,CommentsListAdapter.CommentsViewHolder>(CommentsComparator()){



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.comments_view_holder_layout, parent, false)

        return CommentsViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentsViewHolder, position: Int) {

        val current = getItem(position)
        holder.bind(current)
    }


    class CommentsViewHolder(itemView: View):RecyclerView.ViewHolder(itemView)
    {
        private val author=itemView.authorName
        private val date=itemView.commentDate
        private val content=itemView.commentContent


        fun bind(comment:Comments){

            author.text=comment.author_name
            date.text=comment.date
            content.text=Html.fromHtml(Html.fromHtml(comment.content).toString())


        }


    }

    class CommentsComparator():DiffUtil.ItemCallback<Comments>(){
        override fun areItemsTheSame(oldItem: Comments, newItem: Comments): Boolean {
            return true
        }

        override fun areContentsTheSame(oldItem: Comments, newItem: Comments): Boolean {

            return true
        }

    }


}