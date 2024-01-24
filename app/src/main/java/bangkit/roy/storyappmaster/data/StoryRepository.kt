package bangkit.roy.storyappmaster.data

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import bangkit.roy.storyappmaster.api.ApiService
import bangkit.roy.storyappmaster.database.StoryDatabase
import bangkit.roy.storyappmaster.model.ListStoryItem

class StoryRepository (private val storyDatabase: StoryDatabase, private val apiService: ApiService) {
    fun getStoryForPaging(header: String): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                StoryPagingSource(apiService,header)
            }
        ).liveData
    }
}