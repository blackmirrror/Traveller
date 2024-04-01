package ru.blackmirrror.traveller.features.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.blackmirrror.traveller.domain.models.UserRequest
import ru.blackmirrror.traveller.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val viewModel by viewModel<LoginViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setNavigation()
        observeData()
    }

    private fun setNavigation() {
        binding.btnLoginNext.setOnClickListener {
            viewModel.login(
                UserRequest(
                    username = binding.etLoginEmail.text.toString(),
                    password = binding.etLoginPassword.text.toString()
                )
            )
        }
        binding.tvLoginAuth.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToRegisterFragment())
        }
        binding.tvLoginGuest.setOnClickListener {
            viewModel.rememberAsGuest()
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToMapFragment())
        }
    }

    private fun observeData() {
        viewModel.isLogin.observe(viewLifecycleOwner) {
            if (it)
                findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToMapFragment())
        }
        viewModel.error.observe(viewLifecycleOwner){
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }
    }
}