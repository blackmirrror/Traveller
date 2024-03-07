package ru.blackmirrror.traveller.features.map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.runtime.image.ImageProvider
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import ru.blackmirrror.traveller.R
import ru.blackmirrror.traveller.databinding.FragmentMapBinding
import ru.blackmirrror.traveller.domain.models.MarkResponse
import ru.blackmirrror.traveller.domain.models.SortType
import ru.blackmirrror.traveller.features.utils.TextFormatter

class MapFragment : Fragment() {

    private lateinit var binding: FragmentMapBinding
    private val viewModel by sharedViewModel<MapViewModel>()

    private lateinit var pinsCollection: MapObjectCollection
    private val placemarkTapListener = MapObjectTapListener { mapObject, _ ->
        (mapObject.userData as? String)?.let { onMarkClick(it) }
        true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initCollection()
        setNavigation()
        initMapLocation()
        observeData()
        setFilter()
    }

    private fun initCollection() {
        pinsCollection = binding.mapView.mapWindow.map.mapObjects.addCollection()
    }

    private fun setNavigation() {
        binding.btnAccount.setOnClickListener {
            findNavController().navigate(MapFragmentDirections.actionMapFragmentToAccountFragment())
        }
        binding.btnList.setOnClickListener {
            findNavController().navigate(MapFragmentDirections.actionMapFragmentToListFragment())
        }
    }

    private fun initMapLocation() {
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
        viewModel.loading.observe(viewLifecycleOwner) {
            binding.pbLoading.visibility = if (it) View.VISIBLE else View.GONE
        }
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
        pinsCollection.clear()
        val imageProvider = ImageProvider.fromResource(requireContext(), R.drawable.ic_mark)

        marks.forEach { mark ->
            val markObject = pinsCollection.addPlacemark()
            markObject.geometry = Point(mark.latitude, mark.longitude)
            markObject.setIcon(imageProvider)
            markObject.userData = mark.id
            markObject.addTapListener(placemarkTapListener)
        }
    }

    private fun onMarkClick(markId: String) {
        val mark = viewModel.getMarkById(markId)
        if (mark != null) {
            binding.flMapMoreMark.visibility = View.VISIBLE

            with(binding.llMapMoreMark) {
                tvMoreDescription.text = mark.description
                tvMoreCoordinates.text = TextFormatter.coordinatesToText(mark.latitude, mark.longitude)
                tvMoreLikesAndAuthor.text = TextFormatter.likesAndAuthorToText(mark.likes, mark.user)

                btnMoreClose.setOnClickListener {
                    binding.flMapMoreMark.visibility = View.GONE
                }
            }
        }
    }

    private fun setFilter() {
        binding.btnFilter.setOnClickListener {
            binding.flFilterMap.visibility = if (binding.flFilterMap.visibility == View.VISIBLE)
                View.GONE
            else
                View.VISIBLE
        }
        binding.llFilterMap.spSortList.setSelection(0)

        binding.btnMapSearch.setOnClickListener {
            searchMarksByParameters()
        }
        binding.llFilterMap.btnFilterList.setOnClickListener {
            searchMarksByParameters()
        }
    }

    private fun searchMarksByParameters() {
        val sortType = when (binding.llFilterMap.spSortList.selectedItemPosition) {
            1 -> SortType.BY_AUTHOR
            2 -> SortType.BY_COUNT_LIKES
            else -> SortType.NONE
        }
        viewModel.getMarks(sortType, binding.etMapSearch.text.toString().trim())
        cleanFields()
    }

    private fun cleanFields() {
        binding.etMapSearch.text.clear()
        binding.llFilterMap.spSortList.setSelection(0)
        binding.flFilterMap.visibility = View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        binding.mapView.onStart()
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