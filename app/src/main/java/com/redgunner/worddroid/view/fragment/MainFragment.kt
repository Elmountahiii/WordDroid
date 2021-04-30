package com.redgunner.worddroid.view.fragment

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.google.android.material.tabs.TabLayout
import com.redgunner.worddroid.R
import com.redgunner.worddroid.adapter.PostListAdapter
import com.redgunner.worddroid.adapter.PostLoadStateAdapter
import com.redgunner.worddroid.models.category.Categories
import com.redgunner.worddroid.viewmodel.SharedViewModel
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


class MainFragment : Fragment(R.layout.fragment_main) {

    private val viewModel: SharedViewModel by activityViewModels()
    private val postAdapter = PostListAdapter { postId ->

        findNavController().navigate(MainFragmentDirections.actionGlobalDetailFragment(postId))


    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRecyclerView()

        tablayout_shimmer_view_container.startShimmer()

        lifecycleScope.launch {


            postAdapter.loadStateFlow.map {
                it.refresh

            }
                .distinctUntilChanged()
                .collect { LoadState ->

                    when (LoadState) {
                        is LoadState.Loading -> {


                            HomePostList.isVisible = false
                            list_shimmer_view_container.isVisible = true


                            list_shimmer_view_container.startShimmer()

                        }
                        is LoadState.NotLoading -> {

                            list_shimmer_view_container.stopShimmer()
                            list_shimmer_view_container.isVisible = false
                            HomePostList.isVisible = true



                        }
                        is LoadState.Error -> {


                        }
                    }

                }
        }


        viewModel.posts.observe(viewLifecycleOwner, { pagingData ->

            postAdapter.submitData(lifecycle = lifecycle, pagingData = pagingData)

        })


        lifecycleScope.launchWhenStarted {


            viewModel.categories.collect { categories ->

                if (categories.isNotEmpty()) {
                    tablayout_shimmer_view_container.stopShimmer()

                    tablayout_shimmer_view_container.isVisible = false
                    tabLayout.isVisible = true

                    setUpCategoriesTabLayout(categories)
                }

            }
        }




    }


    override fun onResume() {
        super.onResume()

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {


            override fun onTabSelected(tab: TabLayout.Tab?) {


                HomePostList.scrollToPosition(0)

                viewModel.getPostByCategory(tab!!.position)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

        })
    }

    private fun setUpRecyclerView() {
        HomePostList.apply {
            this.adapter = postAdapter.withLoadStateFooter(PostLoadStateAdapter())
        }
    }

    private fun setUpCategoriesTabLayout(categories: List<Categories>) {

        for (category in categories) {
            val tab = tabLayout.newTab()
            tab.text = category.name
            tabLayout.addTab(tab)
        }


    }



}