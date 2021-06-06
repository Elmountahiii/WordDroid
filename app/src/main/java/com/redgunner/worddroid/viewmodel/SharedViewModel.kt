package com.redgunner.worddroid.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.redgunner.worddroid.models.category.Categories
import com.redgunner.worddroid.models.comments.Comments
import com.redgunner.worddroid.repository.WordPressRepository
import com.redgunner.worddroid.state.PostState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(private val wordPressRepository: WordPressRepository) :
    ViewModel() {


    var tabLayoutPosition = 0
    private var localPostId = 0


    private val currentCategoryPosition = MutableLiveData(DEFAULT_CATEGORY_POSITION)


    val posts = currentCategoryPosition.switchMap { categoryId ->

        if (categories.value.isNotEmpty()) {
            wordPressRepository.getPostByCategory(categories.value[categoryId].id)
                .cachedIn(viewModelScope)

        } else {
            wordPressRepository.getPostByCategory(categoryId).cachedIn(viewModelScope)

        }

    }


    private val _categoryList = MutableStateFlow<List<Categories>>(emptyList())
    val categories: StateFlow<List<Categories>> = _categoryList


    private val _postState = MutableStateFlow<PostState>(PostState.Empty)
    val postState: StateFlow<PostState> = _postState


    private val _commentEventChannel = Channel<List<Comments>>()
    val comments = _commentEventChannel.receiveAsFlow()


    init {

        viewModelScope.launch {

            try {
                _categoryList.value = wordPressRepository.getCategories()

            } catch (exception: IOException) {

            } catch (exception: HttpException) {

            }


        }
    }

    fun getPostById(postId: Int) {
        if (postId!=localPostId){
            viewModelScope.launch {
                _postState.value = PostState.Loading


                try {
                    _postState.value =
                        PostState.Success(wordPressRepository.getPostById(postId = postId))
                        localPostId=postId

                } catch (exception: IOException) {

                    _postState.value = PostState.Error(exception.message.toString())
                } catch (exception: HttpException) {
                    _postState.value = PostState.Error(exception.message.toString())

                }

            }

        }

    }


    fun getPostByCategory(categoryPosition: Int) {


        currentCategoryPosition.value = categoryPosition


    }

    fun getPostComments(postId: Int) {

        viewModelScope.launch {

            try {

                _commentEventChannel.send(wordPressRepository.getPostComments(postId))
            } catch (e: Exception) {


            }
        }


    }


    fun saveTabLayoutPosition(position: Int) {
        tabLayoutPosition = position
        Log.d("tabPosition", "the tab position is $tabLayoutPosition")
    }

    companion object {
        private const val DEFAULT_CATEGORY_POSITION = 0
    }


}