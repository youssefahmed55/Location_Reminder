package com.udacity.project4.locationreminders.savereminder.selectreminderlocation


import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.content.IntentSender
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest.*
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.databinding.FragmentSelectLocationBinding
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import org.koin.android.ext.android.inject
import java.util.*


class SelectLocationFragment : BaseFragment() , OnMapReadyCallback{

    override val _viewModel: SaveReminderViewModel by inject()
    private lateinit var binding: FragmentSelectLocationBinding
    private lateinit var myMap: GoogleMap
    private var lat : Double = 0.0
    var long : Double = 0.0
    private var isLocationSelect = false
    private var marker : Marker ?= null
    var title = ""
    var reminderSelectedLocation = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        //Initialization binding
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_select_location, container, false)

        binding.lifecycleOwner = this
        binding.viewModel = _viewModel

        //Initialization mapFragment
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this) //getMapAsync for mapFragment

        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(true)

        setOnClickOnSaveButton()   //Set On Click On Save Button



        return binding.root
    }

    private fun setOnClickOnSaveButton() {
        binding.saveLocation.setOnClickListener{
            if(marker != null) { //If Marker not Equal Null
                onSelectedLocation()  //Fun to Save title,lat,lon to ViewModel then Navigate Back
            }else{
                Toast.makeText(context,getString(R.string.Select_a_location_First_Please),Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        myMap = googleMap      //initialization myMap
        setPoiClick(myMap)     //Set Poi Click
        setMapStyle(myMap)     //Set Map Style
        setMapLongClick(myMap) //Set LongClick on Map
        enableMyLocation()     //Enable My Location
    }
    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        if (isPermissionGranted()) { //if Permission is Granted
            myMap.isMyLocationEnabled = true //Appears My Location
            Toast.makeText(context, "Location permission is granted.", Toast.LENGTH_SHORT).show()
        } else { //Else request ACCESS_FINE_LOCATION And ACCESS_COARSE_LOCATION Permissions From User
            reqPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }
    //Initialization reqPermissionLauncher To Enable My Location Appears On Screen
    @RequiresApi(Build.VERSION_CODES.Q)
    val reqPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    enableMyLocation()
                }
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    enableMyLocation()
                }else -> {
                    Log.d("Permission: ", "Denied")
                    Toast.makeText(
                        context,
                        "Location permission was not granted.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }



    private fun onSelectedLocation() {
        _viewModel.reminderSelectedLocationStrMutableLiveData.value = title //Set reminderSelectedLocationStrMutableLiveData Value
        _viewModel.lat.value = lat                                          //Set lat Value
        _viewModel.long.value = long                                        //Set long Value
        _viewModel.navigationCommand.postValue(NavigationCommand.Back)      //Navigate Back
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.map_options, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        //Set Style OF Map
        R.id.normal_map -> {
            myMap.mapType = GoogleMap.MAP_TYPE_NORMAL
            true
        }
        R.id.hybrid_map -> {
            myMap.mapType = GoogleMap.MAP_TYPE_HYBRID
            true
        }
        R.id.satellite_map -> {
            myMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
            true
        }
        R.id.terrain_map -> {
            myMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
    private fun setMapLongClick(map: GoogleMap) {
        map.setOnMapLongClickListener { latLng ->
            // A Snippet is Additional text that's displayed below the title.
            val snippet = String.format(
                Locale.getDefault(),
                "Lat: %1$.5f, Long: %2$.5f",
                latLng.latitude,
                latLng.longitude
            )


           if (marker != null)   //if marker not equal null
               marker!!.remove() //Remove marker from screen

           //Initialization marker
           marker = map.addMarker(MarkerOptions()
                .position(latLng)
                .title(getString(R.string.dropped_pin))
                .snippet(snippet)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)))

            reminderSelectedLocation = snippet
            title = getString(R.string.Custom_Location)
            lat= latLng.latitude
            long = latLng.longitude
            isLocationSelect=true
        }
    }

    private fun setPoiClick(map: GoogleMap) {
        map.setOnPoiClickListener { poii ->
            if (marker != null)    //if marker not equal null
                marker!!.remove()  //Remove marker from screen

            //Initialization marker
            marker = map.addMarker(
                MarkerOptions()
                    .position(poii.latLng)
                    .title(poii.name)
            )
            val zoomLevel = 15f //Initialization zoomLevel value
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(poii.latLng, zoomLevel)) //Move and Zoom Camera to poi Location Selected
            marker?.showInfoWindow()     //Show info of poi location on Screen

            title = poii.name
            lat= poii.latLng.latitude
            long = poii.latLng.longitude
            val snippet = String.format(Locale.getDefault(), "Lat: %1$.5f, Long: %2$.5f", lat, long) // A Snippet is Additional text that's displayed below the title.
            reminderSelectedLocation = snippet
            isLocationSelect=true
        }

    }

    private fun setMapStyle(map: GoogleMap) {
        try {
            val success = map.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    context!!,
                    R.raw.map_style
                )
            )

            if (!success) {
                Toast.makeText(context,getString(R.string.Style_failed), Toast.LENGTH_LONG).show()
            }
        } catch (e: Resources.NotFoundException) {
            Toast.makeText(context, "error $e",Toast.LENGTH_LONG).show()
        }
    }


    private fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQ_CODE_BACKGROUND) {
            checkDeviceLocationSettings() //Check Device Location Setting
        }
    }
   /* @TargetApi(Build.VERSION_CODES.Q)
    private fun requestQVersionPermission() {
        val hasForegroundPermission = ActivityCompat.checkSelfPermission(
            activity!!,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (hasForegroundPermission) {
            val hasBackgroundPermission = ActivityCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            if (hasBackgroundPermission) {
                checkDeviceLocationSettings()
            } else {
                ActivityCompat.requestPermissions(
                    activity!!,
                    arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                    REQ_CODE_BACKGROUND
                )
            }
        }
    }*/
    private fun checkDeviceLocationSettings(resolve: Boolean = true) {
       // Initialization locationRequest
       val locationRequest = create().apply {
            priority = PRIORITY_LOW_POWER
        }
        val reqBuilder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)  // Initialization reqBuilder
        val settingsClient = LocationServices.getSettingsClient(activity!!)   // Initialization settingsClient
        val locationSettingsResponseTask = settingsClient.checkLocationSettings(reqBuilder.build())  // Initialization locationSettingsResponseTask
        locationSettingsResponseTask.addOnFailureListener { exception ->
            if (exception is ResolvableApiException && resolve) {
                try {
                    exception.startResolutionForResult(activity!!, REQ_TURN_DEVICE_LOCATION_ON)
                } catch (sendEx: IntentSender.SendIntentException) {
                    Log.d(TAG, "Error getting location settings: " + sendEx.message)
                }
            } else {
                //Show SnackBar
                Snackbar.make(
                    view!!,
                    R.string.location_required_error, Snackbar.LENGTH_INDEFINITE
                ).setAction(android.R.string.ok) {
                    checkDeviceLocationSettings()
                }.show()
            }

        }
    }





    companion object {
        private const val REQ_CODE_BACKGROUND = 125
        private const val REQ_TURN_DEVICE_LOCATION_ON = 124
        private const val TAG = "SelectLocationFrag"
    }

}
