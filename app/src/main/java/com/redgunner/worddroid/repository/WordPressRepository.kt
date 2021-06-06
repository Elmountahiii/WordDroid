package com.redgunner.worddroid.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.redgunner.worddroid.network.WordpressApi
import com.redgunner.worddroid.paging.PostPagingSource
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class WordPressRepository @Inject constructor(private val wordpressApi: WordpressApi) {


    fun getPostByCategory(CategoryId: Int) = Pager(
        config = PagingConfig(
            pageSize = 10,
            maxSize = 100,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            PostPagingSource (
                categoryId = CategoryId,
                wordpressApi = wordpressApi
            )
        }
    ).liveData

    suspend fun getCategories() = wordpressApi.getCategories()


    suspend fun getPostById(postId: Int) = wordpressApi.getPostById(postId = postId)

    suspend fun getPostComments(postId: Int)=wordpressApi.getPostComments(postId = postId)


}