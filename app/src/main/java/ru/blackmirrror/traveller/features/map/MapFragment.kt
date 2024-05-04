package ru.blackmirrror.traveller.features.map

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.database.Cursor
import android.graphics.Bitmap
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.GeoObjectTapListener
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.runtime.image.ImageProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import ru.blackmirrror.traveller.R
import ru.blackmirrror.traveller.databinding.FragmentMapBinding
import ru.blackmirrror.traveller.domain.models.Mark
import ru.blackmirrror.traveller.domain.models.SortType
import ru.blackmirrror.traveller.features.utils.HideKeyboard
import ru.blackmirrror.traveller.features.utils.ImageLoader
import ru.blackmirrror.traveller.features.utils.ImageLoader.loadImage
import ru.blackmirrror.traveller.features.utils.TextFormatter
import kotlin.coroutines.suspendCoroutine

class MapFragment : Fragment() {

    private lateinit var binding: FragmentMapBinding
    private val viewModel by sharedViewModel<MapViewModel>()

    private var imagePath: String? = null

    private lateinit var pinsCollection: MapObjectCollection

    private val placemarkTapListener = MapObjectTapListener { mapObject, _ ->
        (mapObject.userData as? Long)?.let { onMarkClick(it) }
        true
    }
    private val geoObjectTapListener = GeoObjectTapListener { geoObject ->
        geoObject.geoObject.geometry[0].point?.let { showAddMark(it) }
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
        binding.mapView.mapWindow.map.addTapListener(geoObjectTapListener)

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

    private fun setMarks(marks: List<Mark>) {
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

    private fun showAddMark(point: Point) {
        binding.flMapMoreMark.visibility = View.GONE
        binding.flMapAddMark.visibility = View.VISIBLE
        with(binding.llMapAddMark) {
            etAddLatitude.setText(point.latitude.toString())
            etAddLongitude.setText(point.longitude.toString())
            btnAddClose.setOnClickListener {
                binding.flMapAddMark.visibility = View.GONE
            }
            btnAddLl.setOnClickListener {
                HideKeyboard.hideKeyboard(requireActivity())
                binding.flMapAddMark.visibility = View.GONE
                createMark()
            }
            btnImgLl.setOnClickListener {
                openGallery()
            }
        }
        HideKeyboard.hideKeyboard(requireActivity())
    }

    private fun createMark() {
        viewModel.createMark(
            latitude = binding.llMapAddMark.etAddLatitude.text.toString(),
            longitude = binding.llMapAddMark.etAddLongitude.text.toString(),
            description = binding.llMapAddMark.etAddDescription.text.toString(),
            image = imagePath
        )
    }

    private fun onMarkClick(markId: Long) {
        val mark = viewModel.getMarkById(markId)
        if (mark != null) {
            binding.flMapMoreMark.visibility = View.VISIBLE
            binding.flMapAddMark.visibility = View.GONE

            with(binding.llMapMoreMark) {
                tvMoreDescription.text = mark.description
                tvMoreCoordinates.text = TextFormatter.coordinatesToText(mark.latitude, mark.longitude)
                tvMoreLikesAndAuthor.text = TextFormatter.likesAndAuthorToText(mark.likes, mark.user)

                GlobalScope.launch(Dispatchers.Main) {
                    val imagePath = mark.imageUrl
                    val bitmap: Bitmap? = imagePath?.let { loadImage(it) }
                    if (bitmap != null) {
                        ivMoreImage.setImageBitmap(bitmap)
                    }
                }

                btnMoreClose.setOnClickListener {
                    binding.flMapMoreMark.visibility = View.GONE
                }
            }
        }
    }

    private fun setFilter() {
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

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, ListFragment.PICK_IMAGE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == ListFragment.PICK_IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri: Uri = data.data!!

            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
            val cursor: Cursor? = requireActivity().contentResolver.query(
                selectedImageUri,
                filePathColumn,
                null,
                null,
                null
            )
            cursor?.moveToFirst()
            val columnIndex: Int = cursor?.getColumnIndex(filePathColumn[0]) ?: 0
            imagePath = cursor?.getString(columnIndex)
            cursor?.close()
        }
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

        private const val PICK_IMAGE_REQUEST_CODE = 123
    }
}