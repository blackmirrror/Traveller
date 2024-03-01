package ru.blackmirrror.traveller.features.map

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.blackmirrror.traveller.domain.models.MarkResponse
import ru.blackmirrror.traveller.databinding.FragmentListBinding

class ListFragment : Fragment() {

    private lateinit var binding: FragmentListBinding
    private val viewModel by viewModel<MapViewModel>()
    private lateinit var marksAdapter: MarksAdapter

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
            binding.llListMoreMark.tvMoreDescription.text = it.description
            binding.llListMoreMark.tvMoreCoordinates.text = "${it.latitude} ${it.longitude}"

            binding.llListMoreMark.tvMoreLikesAndAuthor.text =
                "${it.likes} людям понравилось, от ${it.user ?: "гостя"}"
        }
        binding.llListMoreMark.btnMoreClose.setOnClickListener {
            binding.flListMoreMark.visibility = View.GONE
        }

        binding.rvList.adapter = marksAdapter
    }

    private fun observeData() {
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
    }

    private fun createMark() {
        viewModel.createMark(
            latitude = binding.llAddMark.etAddLatitude.text.toString(),
            longitude = binding.llAddMark.etAddLongitude.text.toString(),
            description = binding.llAddMark.etAddDescription.text.toString()
        )
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}