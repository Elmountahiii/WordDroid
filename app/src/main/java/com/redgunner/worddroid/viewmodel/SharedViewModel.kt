package com.redgunner.worddroid.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.redgunner.worddroid.models.category.Categories
import com.redgunner.worddroid.models.post.Post
import com.redgunner.worddroid.repository.WordPressRepository
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



    private val currentCategoryPosition = MutableLiveData(DEFAULT_CATEGORY_POSITION)


    val posts = currentCategoryPosition.switchMap { categoryId ->

        if(categories.value.isNotEmpty()){
            wordPressRepository.getPostByCategory(categories.value[categoryId].id).cachedIn(viewModelScope)

        }else{
            wordPressRepository.getPostByCategory(categoryId).cachedIn(viewModelScope)

        }

    }


    private val _categoryList = MutableStateFlow<List<Categories>>(emptyList())
    val categories: StateFlow<List<Categories>> = _categoryList


    private val _postEventChannel= Channel<Post>()
    val post =_postEventChannel.receiveAsFlow()




    init {
        viewModelScope.launch {

            try{
                _categoryList.value=wordPressRepository.getCategories()

            } catch (exception: IOException) {

            } catch (exception: HttpException) {

            }


        }
    }

    fun getPostById(postId:Int){
        viewModelScope.launch {

            _postEventChannel.send(wordPressRepository.getPostById(postId = postId)
            )
        }
    }


    fun getPostByCategory(categoryPosition: Int) {


        currentCategoryPosition.value=categoryPosition


    }

    companion object {
        private const val DEFAULT_CATEGORY_POSITION = 0
    }


}