package bangkit.roy.storyappmaster.ui.auth

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import bangkit.roy.storyappmaster.R
import bangkit.roy.storyappmaster.api.RetrofitClient
import bangkit.roy.storyappmaster.databinding.ActivityLoginBinding
import bangkit.roy.storyappmaster.model.LoginResponse
import bangkit.roy.storyappmaster.model.userAuth
import bangkit.roy.storyappmaster.ui.story.MainStory
import bangkit.roy.storyappmaster.utils.UserPref
import bangkit.roy.storyappmaster.viewModel.UserViewModel
import bangkit.roy.storyappmaster.viewModel.ViewModelFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class Login : AppCompatActivity() {

    private lateinit var userViewModel : UserViewModel
    private lateinit var loginBinding : ActivityLoginBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(loginBinding.root)

        loginBinding.etEmail.typeEditText = "email"
        loginBinding.etPassword.typeEditText = "password"

        LoginviewModel()
        loginBinding.btnLogin.setOnClickListener{
            val Iemail = loginBinding.etEmail.text.toString()
            val Ipassword = loginBinding.etPassword.text.toString()
            loginUser(Iemail,Ipassword)
        }

        loginBinding.regis.setOnClickListener{
            Intent(this@Login, Registrasi::class.java).also {
                startActivity(it)
            }
        }
    }

    private fun LoginviewModel() {
        userViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPref.getInstance(dataStore))
        )[UserViewModel::class.java]

        userViewModel.getAuth().observe(this) { user ->
            if (user.isLogin) {
                val intent = Intent(this, MainStory::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun loginUser(Iemail: String?, Ipassword: String?) {
        showTunggu(true)

        val ClientApi = RetrofitClient.getRetrofitClient().getlogin(Iemail, Ipassword)
        ClientApi.enqueue(object: Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                showTunggu(false)
                val responTObody = response.body()
                Log.d(EXTRA_LOGIN, "onResponse: $responTObody")
                if(response.isSuccessful && responTObody?.message == "success") {
                    userViewModel.saveSession(userAuth(responTObody.loginResult.token, true))
                    val loginInten = Intent(this@Login, MainStory::class.java)
                    Toast.makeText(this@Login, getString(R.string.login_succes), Toast.LENGTH_SHORT).show()
                    startActivity(loginInten)
                    finish()

                } else {
                    Log.e(EXTRA_LOGIN, "onFailure: ${response.message()}")
                    Toast.makeText(this@Login, getString(R.string.login_fail), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                showTunggu(false)
                Log.e(EXTRA_LOGIN, "onFailure: ${t.message}")
                Toast.makeText(this@Login, getString(R.string.login_fail), Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun showTunggu(status: Boolean){
        if(status){
            loginBinding.prgrsbar.visibility= View.VISIBLE
        }else{
            loginBinding.prgrsbar.visibility= View.GONE
        }
    }
    companion object {
        private const val EXTRA_LOGIN = "Login Activity"
    }
}