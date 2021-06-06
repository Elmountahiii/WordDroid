package com.redgunner.worddroid.view.fragment

import android.util.Log
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavArgs
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.redgunner.worddroid.R
import com.redgunner.worddroid.adapter.CommentsListAdapter
import com.redgunner.worddroid.viewmodel.SharedViewModel
import kotlinx.android.synthetic.main.fragment_comments.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class CommentsFragment : Fragment(R.layout.fragment_comments) {

    private val viewModel: SharedViewModel by activityViewModels()

    private val navArgs:CommentsFragmentArgs by navArgs()

    private val commentAdapter=CommentsListAdapter()


    override fun onStart() {
        super.onStart()
        setUpRecyclerView()

        viewModel.getPostComments(navArgs.postId)

    }

    override fun onResume() {
        super.onResume()

        lifecycleScope.launch {
            viewModel.comments.collect {Comments ->


                if(Comments.isNotEmpty()){

                    NoComments.isVisible=false
                    CommentsList.isVisible=true

                    commentAdapter.submitList(Comments)


                }else{

                    NoComments.isVisible=true
                    CommentsList.isVisible=false

                }

            }
        }


        commentTopAppBar.setNavigationOnClickListener {

            findNavController().popBackStack()

        }
    }



    private fun setUpRecyclerView()
    {


        CommentsList.apply {

            this.adapter=commentAdapter

        }
    }





}