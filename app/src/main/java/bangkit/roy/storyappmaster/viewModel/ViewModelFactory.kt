package bangkit.roy.storyappmaster.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import bangkit.roy.storyappmaster.utils.UserPref

class ViewModelFactory (private val prefrerence: UserPref) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T: ViewModel> create(modelClass: Class<T>) : T {
        return when {
            modelClass.isAssignableFrom(UserViewModel::class.java) -> {
                UserViewModel(prefrerence) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }
}