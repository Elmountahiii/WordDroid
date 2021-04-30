package com.redgunner.worddroid.view.fragment

import android.content.res.Configuration
import android.text.Html
import android.util.Base64
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
import com.redgunner.worddroid.utils.AppConstants
import com.redgunner.worddroid.viewmodel.SharedViewModel
import kotlinx.android.synthetic.main.fragment_detail.*
import kotlinx.coroutines.flow.collect


class DetailFragment : Fragment(R.layout.fragment_detail) {



    private val viewModel: SharedViewModel by activityViewModels()
    private val navArgs :DetailFragmentArgs by navArgs()





    override fun onStart() {
        super.onStart()

        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> {
                if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {


                    WebSettingsCompat.setForceDark(postWebView.settings, WebSettingsCompat.FORCE_DARK_ON)

                }
            }
            Configuration.UI_MODE_NIGHT_NO, Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {


                    WebSettingsCompat.setForceDark(postWebView.settings, WebSettingsCompat.FORCE_DARK_OFF)

                }
            }
            else -> {
                //
            }
        }

        postWebView.apply {

            this.fitsSystemWindows=true
            this.settings.apply {
                 javaScriptEnabled=true
                loadWithOverviewMode=true
            }
        }






        viewModel.getPostById(navArgs.PostId)


    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launchWhenCreated {


            viewModel.post.collect { post ->
                    val htmlContent="<html><body> ${post.content.rendered} </body></html>"

                postWebView.loadDataWithBaseURL(null,htmlContent,"text/html; charset=utf-8", "UTF-8", null)



                Glide.with(this@DetailFragment)
                    .load(post._embedded.wp_FeaturedMedia[0].source_url)
                    .into(postImage)


                postTitle.text=  Html.fromHtml(Html.fromHtml(post.title.rendered).toString())
                postCategory.text=post._embedded.wp_Term[0][0].name


                detailShimmerLayout.stopShimmer()
                detailShimmerLayout.isVisible=false
                coordinatorLayout.isVisible=true









            }
        }

        topAppBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

    }








}