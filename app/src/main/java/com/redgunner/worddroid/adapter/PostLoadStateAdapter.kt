package com.redgunner.worddroid.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.redgunner.worddroid.R
import kotlinx.android.synthetic.main.shimmer_post_footer_layout.view.*

class PostLoadStateAdapter: LoadStateAdapter<PostLoadStateAdapter.LoadStateViewHolder>() {

    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) {


        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadStateViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.shimmer_post_footer_layout, parent, false)
        return LoadStateViewHolder(view)
    }
    class LoadStateViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        private val shimmerFrameLayout=itemView.ShimmerFrameLayout

        fun bind(loadState: LoadState){

            shimmerFrameLayout.isVisible =loadState is LoadState.Loading

        }


    }


}