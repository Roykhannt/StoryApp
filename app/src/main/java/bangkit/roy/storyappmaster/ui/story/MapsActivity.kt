package bangkit.roy.storyappmaster.ui.story

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import bangkit.roy.storyappmaster.R
import bangkit.roy.storyappmaster.api.RetrofitClient
import bangkit.roy.storyappmaster.databinding.ActivityMapsBinding
import bangkit.roy.storyappmaster.model.AllStoriesResponse
import bangkit.roy.storyappmaster.model.ListStoryItem
import bangkit.roy.storyappmaster.utils.UserPref
import bangkit.roy.storyappmaster.viewModel.UserViewModel
import bangkit.roy.storyappmaster.viewModel.ViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var gMaps: GoogleMap
    private lateinit var mapBinding: ActivityMapsBinding
    private lateinit var userViewModel: UserViewModel
    private lateinit var LocationClientMaps: FusedLocationProviderClient

    private val locationMaps = MutableLiveData<List<ListStoryItem>>()
    private val LocationMaps: LiveData<List<ListStoryItem>> = locationMaps

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mapBinding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(mapBinding.root)
        supportActionBar?.hide()

        userViewModel()
        getStoryLocation()
        LocationClientMaps = LocationServices.getFusedLocationProviderClient(this)

        val fragmentMap = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        fragmentMap.getMapAsync(this)
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    getMyLocation()
                }
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    getMyLocation()
                }
                else -> {
                }
            }
        }
    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getMyLocation() {
        if(checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ){
            LocationClientMaps.lastLocation.addOnSuccessListener { locationMaps: Location? ->
                if (locationMaps != null) {
                    gMaps.isMyLocationEnabled = true
                    showMarkerLocation(locationMaps)
                } else {
                    Toast.makeText(
                        this@MapsActivity,
                        getString(R.string.location_not_found),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun showMarkerLocation(locationMaps: Location) {
        lat = locationMaps.latitude
        lon = locationMaps.longitude

        val markerLocation = LatLng(lat, lon)
        gMaps.addMarker(
            MarkerOptions()
                .position(markerLocation)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .draggable(true)
                .title(getString(R.string.your_location))
        )
    }


    private fun getStoryLocation() {
        userViewModel.getAuth().observe(this) {
            if (it != null) {
                val clientApi = RetrofitClient.getRetrofitClient().getStoryLocation("Bearer " + it.token)
                clientApi.enqueue(object: Callback<AllStoriesResponse> {
                    override fun onResponse(
                        call: Call<AllStoriesResponse>,
                        response: Response<AllStoriesResponse>
                    ) {
                        val responseTOBody = response.body()
                        if (response.isSuccessful && responseTOBody?.message == "Stories fetched successfully") {
                            locationMaps.value = responseTOBody.listStory
                        }
                    }

                    override fun onFailure(call: Call<AllStoriesResponse>, t: Throwable) {
                        Toast.makeText(
                            this@MapsActivity,
                            getString(R.string.story_failed),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                })
            }
        }
    }

    private fun userViewModel() {
        userViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPref.getInstance(dataStore))
        )[UserViewModel::class.java]
    }

    override fun onMapReady(googleMaps: GoogleMap) {
        gMaps = googleMaps

        gMaps.uiSettings.isZoomControlsEnabled = true
        gMaps.uiSettings.isIndoorLevelPickerEnabled = true
        gMaps.uiSettings.isCompassEnabled = true
        gMaps.uiSettings.isMapToolbarEnabled = true

        getMyLocation()

        val pku = LatLng(0.5172239114406824, 101.44906963147596)


        LocationMaps.observe(this) {
            for(i in LocationMaps.value?.indices!!) {
                val locationMaps = LatLng(LocationMaps.value?.get(i)?.lat!!, LocationMaps.value?.get(i)?.lon!!)
                gMaps.addMarker(MarkerOptions().position(locationMaps).title(getString(R.string.story_uploaded_by) + LocationMaps.value?.get(i)?.name))
            }
            gMaps.animateCamera(CameraUpdateFactory.newLatLngZoom(pku, 5f))
        }
    }

    companion object {
        var lat = 0.0
        var lon = 0.0
    }
    }

