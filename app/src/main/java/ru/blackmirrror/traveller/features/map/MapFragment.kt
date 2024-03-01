package ru.blackmirrror.traveller.features.map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.blackmirrror.traveller.domain.models.MarkResponse
import ru.blackmirrror.traveller.databinding.FragmentMapBinding

class MapFragment : Fragment() {

    private lateinit var binding: FragmentMapBinding
    private val viewModel by viewModel<MapViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setNavigation()
        initMap()
        observeData()
    }

    private fun setNavigation() {
        binding.btnAccount.setOnClickListener {
            findNavController().navigate(MapFragmentDirections.actionMapFragmentToAccountFragment())
        }
        binding.btnList.setOnClickListener {
            findNavController().navigate(MapFragmentDirections.actionMapFragmentToListFragment())
        }
    }

    private fun initMap() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PERMISSION_GRANTED
        ) {
            val locationManager =
                requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val lastKnownLocation =
                locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (lastKnownLocation != null) {
                setLocation(Point(lastKnownLocation.latitude, lastKnownLocation.longitude))
            }
            else {
                setLocation(DEFAULT_POINT)
            }
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                REQUEST_LOCATION_PERMISSION
            )
            setLocation(DEFAULT_POINT)
        }
    }

    private fun setLocation(point: Point) {
        binding.mapView.mapWindow.map.move(
            CameraPosition(point, 15.0f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 3f), null
        )
    }

    private fun observeData() {
        viewModel.currentMarks.observe(viewLifecycleOwner) {
            if (it != null) setMarks(it)
        }
        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let { showToast(it) }
        }
        viewModel.isLoggingUser.observe(viewLifecycleOwner) {
            if (!it)
                findNavController().navigate(MapFragmentDirections.actionMapFragmentToLoginFragment())
        }
    }

    private fun setMarks(marks: List<MarkResponse>) {
        val pinsCollection = binding.mapView.mapWindow.map.mapObjects.addCollection()
        val points = marks.map { Point(it.latitude, it.longitude) }

        points.forEach { point ->
            pinsCollection.addPlacemark().apply {
                geometry = point
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onStart() {
        MapKitFactory.getInstance().onStart()
        binding.mapView.onStart()
        super.onStart()
    }

    override fun onStop() {
        binding.mapView.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    companion object {
        private const val REQUEST_LOCATION_PERMISSION = 1001
        private val DEFAULT_POINT = Point(55.751280, 37.629720)
    }
}