package ru.blackmirrror.traveller.features.register

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.blackmirrror.traveller.domain.models.UserRequest
import ru.blackmirrror.traveller.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private val viewModel by viewModel<RegisterViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setNavigation()
        observeData()
    }

    private fun setNavigation() {
        binding.btnRegNext.setOnClickListener {
            viewModel.register(
                UserRequest(
                    username = binding.etRegEmail.text.toString(),
                    password = binding.etRegPassword.text.toString()
                )
            )
        }
        binding.tvRegLogin.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.tvRegQuest.setOnClickListener {
            viewModel.rememberAsGuest()
            findNavController().navigate(RegisterFragmentDirections.actionRegisterFragmentToMapFragment())
        }
    }

    private fun observeData() {
        viewModel.isRegister.observe(viewLifecycleOwner) {
            if (it)
                findNavController().navigate(RegisterFragmentDirections.actionRegisterFragmentToMapFragment())
        }
        viewModel.error.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }
    }
}