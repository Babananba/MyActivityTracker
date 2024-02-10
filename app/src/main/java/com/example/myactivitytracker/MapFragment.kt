package com.example.myactivitytracker

import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.myactivitytracker.databinding.FragmentMapBinding
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.overlay.ScaleBarOverlay
import org.osmdroid.views.overlay.compass.CompassOverlay
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider
import org.osmdroid.config.Configuration.getInstance


class MapFragment : Fragment() {

    private lateinit var binding: FragmentMapBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentMapBinding.inflate(inflater, container, false)

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

        mapView.controller.animateTo(GeoPoint(45.55111, 18.69389), 17.5, 2500)

        return binding.root
    }


}