package bangkit.roy.storyappmaster.viewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import bangkit.roy.storyappmaster.data.StoryRepository
import bangkit.roy.storyappmaster.di.Injection
import bangkit.roy.storyappmaster.model.ListStoryItem

class StoryViewModel( private val storyRepository: StoryRepository): ViewModel() {


    fun story(header: String): LiveData<PagingData<ListStoryItem>> =  storyRepository.getStoryForPaging(header).cachedIn(viewModelScope)

    class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
        override fun <T: ViewModel> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(StoryViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return StoryViewModel(Injection.provideRepository(context)) as T
            }
            else throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

}