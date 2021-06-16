package com.redgunner.worddroid.view.fragment

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.text.Html
import android.text.TextUtils.replace
import android.util.Log
import android.view.View
import android.webkit.WebChromeClient
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.text.htmlEncode
import androidx.core.text.parseAsHtml
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

                        showPost(post = postState.post)

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

        val htmlContent =
            "<!DOCTYPE html> <html> <head> </head><meta name= viewport content= width=device-width  initial-scale=1.0 > <style>img{display: inline;height: auto;max-width: 100%;} video{display: inline;width: 100%;poster=} p{height: auto;width: 100%; } iframe{width: 100%} </style> <body>   ${post.content.rendered.replace("\"","")} </body></html>"


        postWebView.loadDataWithBaseURL(
            null,
            htmlContent,
            "text/html; charset=utf-8",
            "UTF-8",
            null
        )



        if (!post._embedded.wp_FeaturedMedia.isNullOrEmpty()){
            Glide.with(this@DetailFragment)
                .load(post._embedded.wp_FeaturedMedia[0].source_url)
                .into(postImage)
        }




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
                useWideViewPort = true

            }
            this.setInitialScale(1)
            

            this.webChromeClient= object : WebChromeClient() {

                private  var mCustomView: View? = null
                private var mCustomViewCallback: CustomViewCallback? = null
                private var mOriginalOrientation = 0
                private var mOriginalSystemUiVisibility = 0

                override fun onHideCustomView() {
                    super.onHideCustomView()
                    (activity!!.window.decorView as FrameLayout).removeView(mCustomView)

                    this.mCustomView = null
                    activity!!.window.decorView.setSystemUiVisibility(this.mOriginalSystemUiVisibility)
                    activity!!.requestedOrientation = this.mOriginalOrientation
                    this.mCustomViewCallback?.onCustomViewHidden()
                    this.mCustomViewCallback=null

                }


                override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
                    super.onShowCustomView(view, callback)


                    if (this.mCustomView != null)
                    {
                        onHideCustomView()
                        return
                    }

                    this.mCustomView = view
                    this.mOriginalSystemUiVisibility = activity?.window?.decorView!!.getSystemUiVisibility()
                    this.mOriginalOrientation = activity!!.requestedOrientation
                    this.mCustomViewCallback =callback

                    (activity!!.window.decorView as FrameLayout).addView(
                        mCustomView,
                        FrameLayout.LayoutParams(-1, -1)
                    )
                    activity!!.window.decorView
                        .setSystemUiVisibility(3846 or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)


                }




            }

        }


    }


}