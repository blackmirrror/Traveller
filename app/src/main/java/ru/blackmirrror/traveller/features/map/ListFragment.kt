package ru.blackmirrror.traveller.features.map

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import ru.blackmirrror.traveller.databinding.FragmentListBinding
import ru.blackmirrror.traveller.domain.models.SortType
import ru.blackmirrror.traveller.features.utils.TextFormatter
import java.io.InputStream

class ListFragment : Fragment() {

    private lateinit var binding: FragmentListBinding
    private val viewModel by sharedViewModel<MapViewModel>()
    private lateinit var marksAdapter: MarksAdapter

    private var imagePath: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setNavigation()
        initRecycler()
        observeData()
        setButtons()
        setFilter()
    }

    private fun setNavigation() {
        binding.btnListClose.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun initRecycler() {
        marksAdapter = MarksAdapter()

        marksAdapter.onMarkItemClickListener = {
            binding.flListMoreMark.visibility = View.VISIBLE

            with(binding.llListMoreMark) {
                tvMoreDescription.text = it.description
                tvMoreCoordinates.text = TextFormatter.coordinatesToText(it.latitude, it.longitude)
                tvMoreLikesAndAuthor.text = TextFormatter.likesAndAuthorToText(it.likes, it.user)

                if (it.image != null) {
                    val decodedBytes: ByteArray = Base64.decode(it.image, Base64.DEFAULT)
                    val bitmap: Bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                    ivMoreImage.setImageBitmap(bitmap)
                }

                btnMoreClose.setOnClickListener {
                    binding.flListMoreMark.visibility = View.GONE
                }
            }
        }

        binding.rvList.adapter = marksAdapter
    }

    private fun observeData() {
        //viewModel.getMarks(sortType = SortType.NONE)
        viewModel.loading.observe(viewLifecycleOwner) {
            binding.pbLoading.visibility = if (it) View.VISIBLE else View.GONE
        }
        viewModel.currentMarks.observe(viewLifecycleOwner) {
            marksAdapter.submitList(it)
        }
        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let { showToast(it) }
        }
    }

    private fun setButtons() {
        binding.btnAdd.setOnClickListener {
            binding.flAddMark.visibility = View.VISIBLE
        }
        binding.llAddMark.btnAddLl.setOnClickListener {
            createMark()
        }
        binding.llAddMark.btnAddClose.setOnClickListener {
            binding.flAddMark.visibility = View.GONE
        }
        binding.llAddMark.btnImgLl.setOnClickListener {
            openGallery()
        }
    }

    private fun createMark() {
        viewModel.createMark(
            latitude = binding.llAddMark.etAddLatitude.text.toString(),
            longitude = binding.llAddMark.etAddLongitude.text.toString(),
            description = binding.llAddMark.etAddDescription.text.toString(),
            image = imagePath
        )
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
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

    private fun setFilter() {
        binding.btnListFilter.setOnClickListener {
            binding.flFilterList.visibility = if (binding.flFilterList.visibility == View.VISIBLE)
                View.GONE
            else
                View.VISIBLE
        }
        binding.llFilterList.spSortList.setSelection(0)

        binding.btnListSearch.setOnClickListener {
            searchMarksByParameters()
        }
        binding.llFilterList.btnFilterList.setOnClickListener {
            searchMarksByParameters()
        }
    }

    private fun searchMarksByParameters() {
        val sortType = when (binding.llFilterList.spSortList.selectedItemPosition) {
            1 -> SortType.BY_AUTHOR
            2 -> SortType.BY_COUNT_LIKES
            else -> SortType.NONE
        }
        viewModel.getMarks(sortType, binding.etListSearch.text.toString().trim())
        cleanFields()
    }

    private fun cleanFields() {
        binding.etListSearch.text.clear()
        binding.llFilterList.spSortList.setSelection(0)
        binding.flFilterList.visibility = View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val PICK_IMAGE_REQUEST_CODE = 123
    }
}