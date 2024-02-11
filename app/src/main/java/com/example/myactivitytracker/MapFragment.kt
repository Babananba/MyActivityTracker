package com.example.myactivitytracker

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.myactivitytracker.databinding.FragmentMapBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import org.osmdroid.config.Configuration.getInstance
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.ScaleBarOverlay
import org.osmdroid.views.overlay.compass.CompassOverlay
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider


class MapFragment : Fragment() {

    private lateinit var binding: FragmentMapBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private var userMarker: Marker? = null
    private var travelLine: Polyline? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 10000
        }

        getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context))

        val mapView = binding.mapView
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
        mapView.setMultiTouchControls(true)

        val scaleBar = ScaleBarOverlay(mapView)
        scaleBar.setScaleBarOffset(binding.root.resources.displayMetrics.widthPixels / 2, 10)
        scaleBar.setCentred(true)
        mapView.overlays.add(scaleBar)

        val compass = CompassOverlay(context, InternalCompassOrientationProvider(context), mapView)
        compass.enableCompass()
        mapView.overlays.add(compass)

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_LOCATION
            )
        } else {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                null /* Looper */
            )
        }
        return binding.root
    }

    private var didAlignMap = false

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            if(!this@MapFragment.isResumed){
                return
            }

            val lastLocation = locationResult.lastLocation
            if (lastLocation != null) {
                val userLocation = GeoPoint(lastLocation.latitude, lastLocation.longitude)

                if (userMarker == null) {
                    userMarker = Marker(binding.mapView)
                    userMarker?.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                    binding.mapView.overlays.add(userMarker)
                }
                userMarker?.position = userLocation

                if(travelLine == null){
                    travelLine = Polyline(binding.mapView)
                    binding.mapView.overlays.add(travelLine)
                }
                travelLine?.addPoint(userLocation)

                if(!didAlignMap){
                    binding.mapView.controller.animateTo(userLocation, 17.5, 2500)
                    didAlignMap = true
                }
            }
        }
    }

    companion object {
        private const val PERMISSIONS_REQUEST_ACCESS_LOCATION = 1
    }
}