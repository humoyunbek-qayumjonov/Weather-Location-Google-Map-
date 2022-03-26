package com.humoyunbek.weatherlocation

import android.Manifest
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.IntentSender
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.PendingResult
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.humoyunbek.weatherlocation.databinding.ActivityMainBinding
import com.humoyunbek.weatherlocation.model.Data
import com.humoyunbek.weatherlocation.model.IntentData
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMapClickListener,
    GoogleMap.OnMarkerDragListener {
    lateinit var binding:ActivityMainBinding
    lateinit var googleApiClient: GoogleApiClient
    lateinit var marker: Marker
    private lateinit var map :GoogleMap
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Toast.makeText(this, "Markerni bir necha soniya bosib turing so`ng uni siljiting", Toast.LENGTH_LONG).show()

        window.setFlags(
            WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
            WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

        window.clearFlags(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)


        googleApiClient = getAPIClientInstance()
        if (googleApiClient != null) {
            googleApiClient.connect()
        }

        Dexter.withContext(this)
            .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    requestGPSSettings()
                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {

                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: PermissionRequest?,
                    p1: PermissionToken?
                ) {

                }

            }).check()
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(40.365260,  71.775344), 4f)
        )
       marker = map.addMarker(
           MarkerOptions()
               .position(LatLng(40.365260,  71.775344))
               .draggable(true)
       )!!
        googleMap.setOnMarkerDragListener(this)

    }

    private fun getAPIClientInstance(): GoogleApiClient {
        return GoogleApiClient.Builder(this)
            .addApi(LocationServices.API).build()
    }

    private fun requestGPSSettings() {
        val locationRequest: LocationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        locationRequest.interval = 2000
        locationRequest.fastestInterval = 500
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)
        val result:PendingResult<LocationSettingsResult> =
            LocationServices.SettingsApi.checkLocationSettings(googleApiClient,builder.build())
        result.setResultCallback {
            val status:Status = it.status
            when(status.statusCode){
                LocationSettingsStatusCodes.SUCCESS ->{
                    Toast.makeText(application, "GPS is already enable", Toast.LENGTH_SHORT)
                        .show()
                }
                LocationSettingsStatusCodes.RESOLUTION_REQUIRED ->{
                    try {
                        status.startResolutionForResult(this,0x1)
                    }catch (e:IntentSender.SendIntentException){
                        Log.d(TAG, "requestGPSSettings: ${e.toString()}")
                    }
                }
                LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                    Toast.makeText(
                        application,
                        "Location settings are inadequate, and cannot be fixed here",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        }
    }





    override fun onMapClick(p0: LatLng) {

    }

    override fun onMarkerDrag(p0: Marker) {

    }

    override fun onMarkerDragEnd(marker: Marker) {
        binding.button.visibility = View.VISIBLE

        binding.button.setOnClickListener {
            var intent = Intent(this,SecondActivity::class.java)
            intent.putExtra("data",
            IntentData(
                marker.position.latitude.toString(),
                marker.position.longitude.toString()
            )
                )
            startActivity(intent)

        }
    }

    override fun onMarkerDragStart(p0: Marker) {
    }
}