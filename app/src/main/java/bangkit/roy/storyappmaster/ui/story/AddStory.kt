package bangkit.roy.storyappmaster.ui.story

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import bangkit.roy.storyappmaster.R
import bangkit.roy.storyappmaster.api.RetrofitClient
import bangkit.roy.storyappmaster.databinding.ActivityAddStoryBinding
import bangkit.roy.storyappmaster.model.AddNewStory
import bangkit.roy.storyappmaster.utils.UserPref
import bangkit.roy.storyappmaster.utils.reduceFileImage
import bangkit.roy.storyappmaster.utils.rotateBitmap
import bangkit.roy.storyappmaster.utils.uriToFile
import bangkit.roy.storyappmaster.viewModel.UserViewModel
import bangkit.roy.storyappmaster.viewModel.ViewModelFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class AddStory : AppCompatActivity() {

    private lateinit var addStoryBinding: ActivityAddStoryBinding
    private lateinit var addStoryViewModel: UserViewModel
    private var getFileimage: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addStoryBinding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(addStoryBinding.root)

        addStoryViewModel()

        val lat = intent.getFloatExtra(lat, 1000f)
        val lon = intent.getFloatExtra(lon, 1000f)

        if(lat != 1000f && lon != 1000f) {
            val location = "Latitude = $lat, \nLongitude = $lon"
            addStoryBinding.tvLocation.text = location
        }

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        addStoryBinding.btnCamera.setOnClickListener {
            startCameraX()
        }

        addStoryBinding.btnGallery.setOnClickListener {
            startGallery()
        }

        addStoryBinding.btnUpload.setOnClickListener {
            uploadImage(lat, lon)
        }
    }

    private fun addStoryViewModel() {
        addStoryViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPref.getInstance(dataStore))
        )[UserViewModel::class.java]
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(this, getString(R.string.permission_denied), Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun startCameraX() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private fun startGallery() {
        val intentGallery = Intent()
        intentGallery.action = Intent.ACTION_GET_CONTENT
        intentGallery.type = "image/*"
        val pilihan = Intent.createChooser(intentGallery, "Choose a Picture")
        launcherIntentGallery.launch(pilihan)
    }

    private fun uploadImage(lat: Float, lon: Float) {
        showTunggu(true)

        if (getFileimage != null) {
            val fileImage = reduceFileImage(getFileimage as File)
            val descriptionDetail = addStoryBinding.etDescription.text.toString().toRequestBody("text/plain".toMediaType())
            val requestImageFile = fileImage.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                fileImage.name,
                requestImageFile
            )
            addStoryViewModel.getAuth().observe(this) {
                if(it != null) {
                    val clientApi = if(lat != 1000f && lon != 1000f) {
                        RetrofitClient.getRetrofitClient().uploadImageLocation("Bearer " + it.token, imageMultipart, descriptionDetail ,lat, lon)
                    } else {
                        RetrofitClient.getRetrofitClient().uploadImage("Bearer " + it.token, imageMultipart, descriptionDetail)
                    }
                    clientApi.enqueue(object: Callback<AddNewStory> {
                        override fun onResponse(
                            call: Call<AddNewStory>,
                            response: Response<AddNewStory>
                        ) {
                            showTunggu(false)
                            val responseTOBody = response.body()
                            Log.d(EXTRA_ADD_STORY, "onResponse: $responseTOBody")
                            if(response.isSuccessful && responseTOBody?.message == "Story created successfully") {
                                Toast.makeText(this@AddStory, getString(R.string.upload_success), Toast.LENGTH_SHORT).show()
                                val addIntent = Intent(this@AddStory, MainStory::class.java)
                                startActivity(addIntent)
                                finish()
                            } else {
                                Log.e(EXTRA_ADD_STORY, "onFailure1: ${response.message()}")
                                Toast.makeText(this@AddStory, getString(R.string.upload_fail), Toast.LENGTH_SHORT).show()
                            }
                        }
                        override fun onFailure(call: Call<AddNewStory>, t: Throwable) {
                            showTunggu(false)
                            Log.e(EXTRA_ADD_STORY, "onFailure2: ${t.message}" )
                            Toast.makeText(this@AddStory, getString(R.string.upload_fail), Toast.LENGTH_SHORT).show()
                        }
                    })
                }
            }
        }
    }
    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = it.data?.getSerializableExtra("picture") as File
            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean
            getFileimage = myFile

            val result = rotateBitmap(
                BitmapFactory.decodeFile(myFile.path),
                isBackCamera
            )

            addStoryBinding.imgPreview.setImageBitmap(result)
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this@AddStory)
            getFileimage = myFile
            addStoryBinding.imgPreview.setImageURI(selectedImg)
        }
    }

    private fun showTunggu(status: Boolean){
        if(status){
            addStoryBinding.progressBar.visibility= View.VISIBLE
        }else{
            addStoryBinding.progressBar.visibility= View.GONE
        }
    }

    companion object {

        const val EXTRA_ADD_STORY = "AddStoryActivity"
        const val CAMERA_X_RESULT = 200

        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10

        var lat = "lat"
        var lon = "lon"
    }
}