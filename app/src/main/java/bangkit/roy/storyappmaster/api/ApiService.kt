package bangkit.roy.storyappmaster.api

import bangkit.roy.storyappmaster.model.AddNewStory
import bangkit.roy.storyappmaster.model.AllStoriesResponse
import bangkit.roy.storyappmaster.model.LoginResponse
import bangkit.roy.storyappmaster.model.RegisResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("register")
    fun getRegistrasiApi(
        @Field("name") name: String? = null,
        @Field("email") email: String? = null,
        @Field("password") password: String? = null
    ) : Call<RegisResponse>

    @FormUrlEncoded
    @POST("login")
    fun getlogin(
        @Field("email") email: String? = null,
        @Field("password") password: String? = null
    ) : Call<LoginResponse>

    @Multipart
    @POST("stories")
    fun uploadImage(
        @Header("Authorization") header: String,
        @Part file: MultipartBody.Part,
        @Part("description") descriptionDetail: RequestBody,
    ): Call<AddNewStory>

    @GET("stories")
    suspend fun getAllStories(
        @Header("Authorization") header: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ) : AllStoriesResponse

    @GET("stories")
    fun getStoryLocation(
        @Header("Authorization") authorization: String,
        @Query("location") includeLocation: Int = 1
    ): Call<AllStoriesResponse>

    @Multipart
    @POST("stories")
    fun uploadImageLocation(
        @Header("Authorization") header: String,
        @Part file: MultipartBody.Part,
        @Part("description") descriptionDetail: RequestBody,
        @Part("lat") lat: Float,
        @Part("lon") lon: Float
    ) : Call<AddNewStory>

}