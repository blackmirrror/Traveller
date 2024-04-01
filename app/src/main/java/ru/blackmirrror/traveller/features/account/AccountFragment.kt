package ru.blackmirrror.traveller.features.account

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.blackmirrror.traveller.R
import ru.blackmirrror.traveller.databinding.FragmentAccountBinding

class AccountFragment : Fragment() {

    private lateinit var binding: FragmentAccountBinding
    private val viewModel by viewModel<AccountViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setNavigation()
        observeData()
    }

    private fun setNavigation() {
        binding.btnAccClose.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.btnLogout.setOnClickListener {
            viewModel.logout()
            findNavController().navigate(AccountFragmentDirections.actionAccountFragmentToLoginFragment())
        }
    }

    private fun observeData() {
        viewModel.isGuest.observe(viewLifecycleOwner) {
            if (it) {
                binding.btnLogout.text = "Войти"
                binding.tvAccName.text = "Гость"
            }
        }
        viewModel.currentUser.observe(viewLifecycleOwner) {
            binding.tvAccName.text = it?.username ?: "Гость"
        }
    }
}