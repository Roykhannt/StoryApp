package bangkit.roy.storyappmaster.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

 @Parcelize
data class userAuth(
    val token: String,
    val isLogin: Boolean
) : Parcelable
