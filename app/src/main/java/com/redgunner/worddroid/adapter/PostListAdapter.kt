package com.redgunner.worddroid.adapter

import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.redgunner.worddroid.R
import com.redgunner.worddroid.models.post.Post
import kotlinx.android.synthetic.main.post_view_holder_layout.view.*
import java.text.SimpleDateFormat

class PostListAdapter (val postClick:(postId:Int)->Unit):
    PagingDataAdapter<Post, PostListAdapter.PostViewHolder>(POST_COMPARATOR){

    inner class PostViewHolder(itemView: View, private val context: Context) :
        RecyclerView.ViewHolder(itemView){


        private val image = itemView.PostImage
        private val title = itemView.PostTitle
        private val category = itemView.postCategory
        private val time = itemView.PostTime

        init {

            image.setOnClickListener {
                getItem(adapterPosition)?.let { it1 -> postClick(it1.id) }
            }
        }



        fun bind(post: Post){


            Glide.with(context)
                .load(post._embedded.wp_FeaturedMedia[0].source_url)
                .into(image)

            title.text = Html.fromHtml(Html.fromHtml(post.title.rendered).toString())


            category.text = post._embedded.wp_Term[0][0].name


            time.text = SimpleDateFormat("yyyy-MM-dd").parse(post.date).toString()

        }



    }




    companion object{
        private val POST_COMPARATOR= object : DiffUtil.ItemCallback<Post>() {
            override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {

                return oldItem.id==newItem.id
            }

            override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
                return oldItem==newItem
            }

        }
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val currentItem = getItem(position)

        if (currentItem != null) {
            holder.bind(currentItem)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.post_view_holder_layout, parent, false)
        return PostViewHolder(view,parent.context)    }


}