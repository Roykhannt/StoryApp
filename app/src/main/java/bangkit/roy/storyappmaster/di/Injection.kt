package bangkit.roy.storyappmaster.di

import android.content.Context
import bangkit.roy.storyappmaster.api.RetrofitClient
import bangkit.roy.storyappmaster.data.StoryRepository
import bangkit.roy.storyappmaster.database.StoryDatabase

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val database = StoryDatabase.getDatabase(context)
        val apiService = RetrofitClient.getRetrofitClient()

        return StoryRepository(database, apiService)
    }
}