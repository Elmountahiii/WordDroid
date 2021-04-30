package com.redgunner.worddroid.models.post

import com.google.gson.annotations.SerializedName

data class Embedded(
    @SerializedName("wp:featuredmedia")
    val wp_FeaturedMedia: List<WpFeaturedmedia>,
    @SerializedName("wp:term")
    val wp_Term: List<List<WpTerm>>
)