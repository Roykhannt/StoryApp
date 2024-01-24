package bangkit.roy.storyappmaster.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import bangkit.roy.storyappmaster.api.ApiService
import bangkit.roy.storyappmaster.model.ListStoryItem

class StoryPagingSource(private val apiService: ApiService, private val header: String) : PagingSource<Int, ListStoryItem>() {

    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return try {
            val page = params.key ?: INITIAL_PAGE_INDEX
            val response = apiService.getAllStories(header, page, params.loadSize)

            LoadResult.Page(
                data = response.listStory,
                prevKey = if(page == INITIAL_PAGE_INDEX) null else page - 1,
                nextKey = if(response.listStory.isNullOrEmpty()) null else page + 1
            )
        } catch(exception: Exception) {
            return LoadResult.Error(exception)
        }
    }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}