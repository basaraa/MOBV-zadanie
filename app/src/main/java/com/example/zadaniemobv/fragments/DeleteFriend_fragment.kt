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
import com.example.zadaniemobv.databinding.DeleteFriendBinding
import com.example.zadaniemobv.others.PreferenceData
import com.example.zadaniemobv.others.provideViewModelFactory
import com.example.zadaniemobv.viewModels.FriendsModel


class DeleteFriend_fragment : Fragment() {
    private var _binding: DeleteFriendBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    private val friendViewModel: FriendsModel by lazy{
        ViewModelProvider(
            this,
            provideViewModelFactory(requireContext())
        ).get(FriendsModel::class.java)}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DeleteFriendBinding.inflate(inflater,container,false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val user = PreferenceData.getInstance().getUserItem(requireContext())
        if ((user?.id ?: "").isBlank()) {
            Navigation.findNavController(view).navigate(R.id.action_deleteFriend_fragment_to_login_fragment)
            return
        }
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = friendViewModel
        }
        binding.submitInfo.setOnClickListener {
            if (user!=null)
                friendViewModel.deleteFriend(binding.enterFriendName.text.toString())
        }

        binding.logout.setOnClickListener{ listen->
            PreferenceData.getInstance().clearData(requireContext())
            listen.findNavController().navigate (R.id.action_deleteFriend_fragment_to_login_fragment)
        }

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}