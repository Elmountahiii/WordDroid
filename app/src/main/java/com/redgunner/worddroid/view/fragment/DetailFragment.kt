package com.redgunner.worddroid.view.fragment

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import com.bumptech.glide.Glide
import com.redgunner.worddroid.R
import com.redgunner.worddroid.models.post.Post
import com.redgunner.worddroid.state.PostState
import com.redgunner.worddroid.viewmodel.SharedViewModel
import kotlinx.android.synthetic.main.fragment_detail.*
import kotlinx.coroutines.flow.collect


class DetailFragment : Fragment(R.layout.fragment_detail) {


    private val viewModel: SharedViewModel by activityViewModels()
    private val navArgs: DetailFragmentArgs by navArgs()




    override fun onStart() {
        super.onStart()
        setUpWebView()
        viewModel.getPostById(navArgs.PostId)



        lifecycleScope.launchWhenStarted {
            viewModel.postState.collect { postState ->
                when (postState) {

                    is PostState.Empty -> {
                        viewModel.getPostById(navArgs.PostId)

                    }

                    is PostState.Loading -> {
                        shimmerState(isShimmer = true)

                    }

                    is PostState.Success -> {
                        shimmerState(isShimmer = false)

                        showPost(post=postState.post)

                    }

                    is PostState.Error -> {
                        detailShimmerLayout.isVisible = false
                            Toast.makeText(
                            this@DetailFragment.context,
                            postState.message,
                            Toast.LENGTH_LONG
                        ).show()

                    }
                }


            }
        }





    }

    override fun onResume() {
        super.onResume()

        topAppBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        buttonShowComments.setOnClickListener {
            findNavController().navigate(
                DetailFragmentDirections.actionDetailFragmentToCommentsFragment(
                    navArgs.PostId
                )
            )
        }

    }

    private fun showPost(post: Post) {

        val htmlContent = "<html><body> ${post.content.rendered} </body></html>"

        postWebView.loadDataWithBaseURL(
            null,
            htmlContent,
            "text/html; charset=utf-8",
            "UTF-8",
            null
        )



        Glide.with(this@DetailFragment)
            .load(post._embedded.wp_FeaturedMedia[0].source_url)
            .into(postImage)


        postTitle.text = Html.fromHtml(Html.fromHtml(post.title.rendered).toString())
        postCategory.text = post._embedded.wp_Term[0][0].name
    }

    private fun shimmerState(isShimmer: Boolean) {

        if (isShimmer) {
            detailShimmerLayout.isVisible = isShimmer

        } else {
            detailShimmerLayout.isVisible = isShimmer
            coordinatorLayout.isVisible = true


        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setUpWebView() {
        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> {
                if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {


                    WebSettingsCompat.setForceDark(
                        postWebView.settings,
                        WebSettingsCompat.FORCE_DARK_ON
                    )

                }
            }
            Configuration.UI_MODE_NIGHT_NO, Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {


                    WebSettingsCompat.setForceDark(
                        postWebView.settings,
                        WebSettingsCompat.FORCE_DARK_OFF
                    )

                }
            }
            else -> {
                //
            }
        }

        postWebView.apply {

            this.fitsSystemWindows = true
            this.settings.apply {
                javaScriptEnabled = true
                loadWithOverviewMode = true
            }
        }
    }


}