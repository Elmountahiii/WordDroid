package com.redgunner.worddroid.state

import com.redgunner.worddroid.models.post.Post

sealed class PostState

{
    object Loading:PostState()
    data class Success(val post: Post ):PostState()
    data class Error(val message:String):PostState()
    object Empty:PostState()


}