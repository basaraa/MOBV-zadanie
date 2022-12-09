package com.example.zadaniemobv.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.example.zadaniemobv.R
import com.example.zadaniemobv.databinding.LoginBinding
import com.example.zadaniemobv.others.PreferenceData
import com.example.zadaniemobv.others.provideViewModelFactory
import com.example.zadaniemobv.viewModels.LoginRegisterModel


class Login_fragment : Fragment() {
    private var _binding: LoginBinding? = null
    private val binding get() = _binding!!
    private val loginRegisterViewModel: LoginRegisterModel by lazy{
        ViewModelProvider(
            this,
            provideViewModelFactory(requireContext())
        ).get(LoginRegisterModel::class.java)}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = LoginBinding.inflate(inflater,container,false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val user = PreferenceData.getInstance().getUserItem(requireContext())
        if ((user?.id ?: "").isNotBlank()) {
            Navigation.findNavController(view).navigate(R.id.action_login_fragment_to_pubList_fragment)
            return
        }
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = loginRegisterViewModel
        }
        binding.submitInfo.setOnClickListener {
            val nickname = binding.nickname.text.toString()
            val heslo = binding.heslo.text.toString()
            if (nickname.isEmpty() || heslo.isEmpty())
                loginRegisterViewModel.show("Vyplňte prosím všetky informácie")
           else {
                loginRegisterViewModel.userLogin(nickname, heslo)
            }

        }
        binding.registracia.setOnClickListener { listen ->
            listen.findNavController().navigate(R.id.action_login_fragment_to_registration_fragment)
        }
        loginRegisterViewModel.user.observe(viewLifecycleOwner){
            it?.let {
                PreferenceData.getInstance().putUserItem(requireContext(),it)
                Navigation.findNavController(requireView()).navigate(R.id.action_login_fragment_to_pubList_fragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}