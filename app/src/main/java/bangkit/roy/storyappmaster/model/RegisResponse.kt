package bangkit.roy.storyappmaster.model

import com.google.gson.annotations.SerializedName

data class RegisResponse(
    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String
)
