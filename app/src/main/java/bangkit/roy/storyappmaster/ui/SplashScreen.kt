package bangkit.roy.storyappmaster.ui

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import bangkit.roy.storyappmaster.databinding.ActivitySplashScreenBinding
import bangkit.roy.storyappmaster.ui.auth.Login
import bangkit.roy.storyappmaster.ui.story.MainStory
import bangkit.roy.storyappmaster.utils.UserPref
import bangkit.roy.storyappmaster.viewModel.UserViewModel
import bangkit.roy.storyappmaster.viewModel.ViewModelFactory


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")


class SplashScreen : AppCompatActivity() {

    private lateinit var splashScreenBinding: ActivitySplashScreenBinding
    private lateinit var splashViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        splashScreenBinding= ActivitySplashScreenBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(splashScreenBinding.root)

        splashScreenBinding.nextTgl.setOnClickListener{
            Intent(this@SplashScreen, Login::class.java).also {
                startActivity(it)
            }
        }
        setupView()
        SplashviewModel()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun SplashviewModel() {
        splashViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPref.getInstance(dataStore))
        )[UserViewModel::class.java]

        splashViewModel.getAuth().observe(this) { user ->
            if (user.isLogin) {
                val intent = Intent(this, MainStory::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

}