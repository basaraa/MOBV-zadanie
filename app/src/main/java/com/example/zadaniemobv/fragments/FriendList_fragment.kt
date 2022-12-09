package com.example.zadaniemobv.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.zadaniemobv.R
import com.example.zadaniemobv.adapter.FriendList_adapter
import com.example.zadaniemobv.databinding.FriendListBinding
import com.example.zadaniemobv.others.PreferenceData
import com.example.zadaniemobv.others.provideViewModelFactory
import com.example.zadaniemobv.viewModels.FriendsModel

class FriendList_fragment : Fragment() {
    private lateinit var recycler_view: RecyclerView
    private var _binding: FriendListBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FriendListBinding.inflate(inflater,container,false)
        return binding.root
    }

    private val friendViewModel: FriendsModel by lazy{
        ViewModelProvider(
            this,
            provideViewModelFactory(requireContext())
        ).get(FriendsModel::class.java)}



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val user = PreferenceData.getInstance().getUserItem(requireContext())
        if ((user?.id ?: "").isBlank()) {
            Navigation.findNavController(view).navigate(R.id.action_friendList_fragment_to_login_fragment)
            return
        }
        else if (user != null) {
            friendViewModel.reloadFriendsFromWebsite(user.id)
            friendViewModel.loadFriends(user.id)
            }

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = friendViewModel
        }
        recycler_view=binding.recyclerFriendList
        recycler_view.adapter = FriendList_adapter()
        friendViewModel.friendsList.observe(viewLifecycleOwner){ friends ->
            friends?.apply {
                (recycler_view.adapter!! as FriendList_adapter).dataset = friends
            }
        }
        binding.enterFriend.setOnClickListener { listen ->
            listen.findNavController().navigate(R.id.action_friendList_fragment_to_addFriend_fragment)
        }
        binding.deleteFriend.setOnClickListener { listen ->
            listen.findNavController().navigate(R.id.action_friendList_fragment_to_deleteFriend_fragment)
        }
        binding.sortFriends.setOnClickListener {
            if (user != null) {
                friendViewModel.sortFriendList(user.id)
            }
        }
        binding.reloadFriends.setOnClickListener{
            if (user != null) {
                friendViewModel.reloadFriendsFromWebsite(user.id)
                friendViewModel.loadFriends(user.id)
            }
        }
        binding.logout.setOnClickListener{ listen->
            PreferenceData.getInstance().clearData(requireContext())
            listen.findNavController().navigate (R.id.action_friendList_fragment_to_login_fragment)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}