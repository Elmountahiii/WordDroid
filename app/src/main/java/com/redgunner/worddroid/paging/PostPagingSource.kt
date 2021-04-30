package com.redgunner.worddroid.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.redgunner.worddroid.models.post.Post
import com.redgunner.worddroid.network.WordpressApi
import retrofit2.HttpException
import java.io.IOException


private const val WORDPRESS_STARTING_PAGE_INDEX = 1

class PostPagingSource( private val categoryId:Int,
                        private val wordpressApi: WordpressApi
):
PagingSource<Int, Post>()


{
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Post> {

        return try {
            val position = params.key ?: 1

            val posts = wordpressApi.getPostsByCategories(categories = categoryId,page = position, perPage = params.loadSize ,embed = true)

            LoadResult.Page(
                data = posts,
                prevKey = if (position == WORDPRESS_STARTING_PAGE_INDEX) null else position - 1,
                nextKey = if (posts.isEmpty()) null else position + 1
            )

        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {

            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Post>): Int? {
        return state.anchorPosition
    }


}