package bangkit.roy.storyappmaster.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import bangkit.roy.storyappmaster.model.userAuth
import bangkit.roy.storyappmaster.utils.UserPref
import kotlinx.coroutines.launch

class   UserViewModel (private val Authpref: UserPref) : ViewModel() {
    fun getAuth(): LiveData<userAuth> {
        return Authpref.getAuth().asLiveData()
    }

    fun saveSession(user: userAuth) {
        viewModelScope.launch {
            Authpref.saveSession(user)
        }
    }

    fun logoutSession() {
        viewModelScope.launch {
            Authpref.logoutSession()
        }
    }
}
