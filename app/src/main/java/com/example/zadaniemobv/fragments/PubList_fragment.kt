package com.example.zadaniemobv.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.zadaniemobv.R
import com.example.zadaniemobv.adapter.PubList_adapter
import com.example.zadaniemobv.databinding.PubListBinding
import com.example.zadaniemobv.datas.MyLocationCoordinates
import com.example.zadaniemobv.others.PreferenceData
import com.example.zadaniemobv.others.provideViewModelFactory
import com.example.zadaniemobv.viewModels.PubsModel
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class PubList_fragment : Fragment() {

    private lateinit var recycler_view: RecyclerView
    private var _binding: PubListBinding? = null
    private val binding get() = _binding!!
    private lateinit var locationClient: FusedLocationProviderClient
    private var type=0
    @RequiresApi(Build.VERSION_CODES.Q)
    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                if (type==0)
                    loadPubList()
                else
                    Navigation.findNavController(requireView()).navigate(R.id.action_pubList_fragment_to_nearbyPubsList_fragment)
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                if (type==0)
                    loadPubList()
                else
                    Navigation.findNavController(requireView()).navigate(R.id.action_pubList_fragment_to_nearbyPubsList_fragment)
            } else -> {
                pubViewModel.show("Prístup k polohe zamietnutý")
                if (type==0)
                    pubViewModel.reloadPubUsersFromWebsite()
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PubListBinding.inflate(inflater,container,false)
        return binding.root
        }

    private val pubViewModel: PubsModel by lazy{
        ViewModelProvider(
            this,
            provideViewModelFactory(requireContext())
        ).get(PubsModel::class.java)}



    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val user = PreferenceData.getInstance().getUserItem(requireContext())
        if ((user?.id ?: "").isBlank()) {
            Navigation.findNavController(view).navigate(R.id.action_pubList_fragment_to_login_fragment)
            return
        }
        else if (user != null) {
            loadPubList()
        }
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = pubViewModel
        }
        recycler_view=binding.recyclerPubList
        recycler_view.adapter = PubList_adapter()
        pubViewModel.pubsList.observe(viewLifecycleOwner){ pubs ->
            pubs?.apply {
                (recycler_view.adapter!! as PubList_adapter).dataset = pubs
            }
        }
        binding.friendList.setOnClickListener { listen ->
            listen.findNavController().navigate(R.id.action_pubList_fragment_to_friendList_fragment)
        }
        binding.nearbyPubs.setOnClickListener { listen ->
            type=1
            if (checkLocationPermissions())
                listen.findNavController().navigate(R.id.action_pubList_fragment_to_nearbyPubsList_fragment)
            else
                locationPermissionRequest()
        }
        binding.sortPubs.setOnClickListener {
            pubViewModel.sortPubList()
        }
        binding.sortByUsers.setOnClickListener {
            pubViewModel.sortPubListByUsers()
        }
        binding.sortByDistance.setOnClickListener {
            pubViewModel.sortPubListByDistance()
        }

        binding.reloadPubs.setOnClickListener{
            if (user != null) {
                loadPubList()
            }
        }
        binding.logout.setOnClickListener{ listen->
            PreferenceData.getInstance().clearData(requireContext())
            listen.findNavController().navigate(R.id.action_pubList_fragment_to_login_fragment)
        }

    }

    private fun checkLocationPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun locationPermissionRequest() {
        locationPermissionRequest.launch(
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
    }
    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("MissingPermission")
    private fun loadPubList() {
        if (checkLocationPermissions()) {
            Log.i("xx","xxx")
            locationClient.getCurrentLocation(
                CurrentLocationRequest.Builder().setDurationMillis(30000)
                    .setMaxUpdateAgeMillis(60000).build(), null
            ).addOnSuccessListener {
                it?.let {
                    pubViewModel.myLocation= MyLocationCoordinates(
                            it.latitude,
                            it.longitude
                        )
                    pubViewModel.reloadPubUsersFromWebsite()
                } ?: run {
                    pubViewModel.reloadPubUsersFromWebsite()
                }
            }
        }
        else
            locationPermissionRequest()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}