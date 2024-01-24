package bangkit.roy.storyappmaster.ui.story

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import bangkit.roy.storyappmaster.databinding.ActivityMainStoryBinding
import bangkit.roy.storyappmaster.ui.adapter.ListStoryAdapter
import bangkit.roy.storyappmaster.ui.adapter.LoadingStateAdapter
import bangkit.roy.storyappmaster.ui.auth.Login
import bangkit.roy.storyappmaster.utils.UserPref
import bangkit.roy.storyappmaster.viewModel.StoryViewModel
import bangkit.roy.storyappmaster.viewModel.UserViewModel
import bangkit.roy.storyappmaster.viewModel.ViewModelFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")


class MainStory : AppCompatActivity() {

    private lateinit var userViewModel: UserViewModel
    private lateinit var mainStoryBinding: ActivityMainStoryBinding
    private val mainStoryViewModel: StoryViewModel by viewModels {
        StoryViewModel.ViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainStoryBinding = ActivityMainStoryBinding.inflate(layoutInflater)

        setContentView(mainStoryBinding.root)
        listStoryViewModel()


        val layoutManager = LinearLayoutManager(this)
        mainStoryBinding.rvStory.layoutManager = layoutManager
        getListStory()


        mainStoryBinding.btnAddStory.setOnClickListener{
            Intent(this@MainStory, AddStory::class.java).also {
                startActivity(it)
            }
        }

        mainStoryBinding.btnMaps.setOnClickListener{
            Intent(this@MainStory, MapsActivity::class.java).also {
                startActivity(it)
            }
        }

        mainStoryBinding.logout.setOnClickListener{
            userViewModel.logoutSession()
            Intent(this@MainStory, Login::class.java).also {
                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(it)
                finish()
            }
        }
    }

    private fun listStoryViewModel() {
        userViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPref.getInstance(dataStore))
        )[UserViewModel::class.java]
    }

    private fun getListStory() {
        val listStoryAdapter = ListStoryAdapter()
        mainStoryBinding.rvStory.adapter = listStoryAdapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                listStoryAdapter.retry()
            }
        )
        userViewModel.getAuth().observe(this) { userAuth ->
            if(userAuth != null) {
                mainStoryViewModel.story("Bearer " + userAuth.token).observe(this) { listStory ->
                    listStoryAdapter.submitData(lifecycle, listStory)
                }
            }
        }
    }
}