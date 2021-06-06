package com.redgunner.worddroid.models.comments

data class Comments(
    val author_name: String,
    val content: String,
    val date: String,
    val id: Int,
    val post: Int
)