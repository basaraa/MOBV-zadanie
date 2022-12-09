package com.example.zadaniemobv.fragments

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_MUTABLE
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.zadaniemobv.R
import com.example.zadaniemobv.adapter.NearbyPubs_adapter
import com.example.zadaniemobv.databinding.NearbyPubsListBinding
import com.example.zadaniemobv.datas.MyLocationCoordinates
import com.example.zadaniemobv.geofence.GeofenceReceiver
import com.example.zadaniemobv.others.PreferenceData
import com.example.zadaniemobv.others.provideViewModelFactory
import com.example.zadaniemobv.viewModels.NearbyPubsModel
import com.google.android.gms.location.*


class NearbyPubsList_fragment : Fragment() {
    private lateinit var recycler_view: RecyclerView
    private var _binding: NearbyPubsListBinding? = null
    private val binding get() = _binding!!
    private lateinit var locationClient: FusedLocationProviderClient
    private lateinit var geofencingClient: GeofencingClient
    @RequiresApi(Build.VERSION_CODES.Q)
    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_BACKGROUND_LOCATION, false) -> {
                nearbyPubViewModel.checkIntoPub()
            }
            else -> {
                nearbyPubViewModel.show("Prístup k background polohe zamietnutý, nie je možné vás označiť do podniku")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        geofencingClient = LocationServices.getGeofencingClient(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NearbyPubsListBinding.inflate(inflater,container,false)
        return binding.root
    }

    private val nearbyPubViewModel: NearbyPubsModel by lazy{
        ViewModelProvider(
            this,
            provideViewModelFactory(requireContext())
        ).get(NearbyPubsModel::class.java)}



    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val user = PreferenceData.getInstance().getUserItem(requireContext())
        if ((user?.id ?: "").isBlank()) {
            Navigation.findNavController(view).navigate(R.id.action_nearbyPubsList_fragment_to_login_fragment)
            return
        }
        else if (user != null) {
            loadNearbyPubs()
        }
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = nearbyPubViewModel
        }

        recycler_view=binding.recyclerNearbyPubs
        recycler_view.adapter = NearbyPubs_adapter(nearbyPubViewModel)
        nearbyPubViewModel.pubs.observe(viewLifecycleOwner){ pubs ->
            pubs?.apply {
                (recycler_view.adapter!! as NearbyPubs_adapter).dataset = pubs
            }
        }

        nearbyPubViewModel.checkedIn.observe(viewLifecycleOwner) {
            it?.getContentIfNotHandled()?.let {
                if (it) {
                    nearbyPubViewModel.show("Úspešne si sa zapísal do podniku.")
                    nearbyPubViewModel.myLocation.value?.let {
                        createGeofenceInstance(it.lat, it.lon)
                    }
                }
            }
        }

        nearbyPubViewModel.pub.observe(viewLifecycleOwner){ pubInfo ->
            pubInfo?.apply {
                binding.itemName.text=pubInfo.name
                binding.distance.text= "%.3f m".format(pubInfo.distance)
                showSelectPubAnimation()
            }
        }

        binding.checkMeToPub.setOnClickListener {
            if (checkBackgroundLocationPermissions()){
                binding.lottieCheckedIntoPub.playAnimation()
                nearbyPubViewModel.checkIntoPub()
            }
            else
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    locationPermissionInfo()
                }
        }

        binding.reloadPubs.setOnClickListener{
            if (user != null) {
                loadNearbyPubs()
            }
        }
        binding.logout.setOnClickListener{ listen->
            PreferenceData.getInstance().clearData(requireContext())
            listen.findNavController().navigate (R.id.action_nearbyPubsList_fragment_to_login_fragment)
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    @SuppressLint("MissingPermission")
    private fun createGeofenceInstance(lat: Double, lon: Double) {
        if (!checkLocationPermissions()) {
            nearbyPubViewModel.show("Geofence zlyhalo, nepovolili ste informácie o vašej polohe.")
        }
        val geofenceIntent = PendingIntent.getBroadcast(
            requireContext(), 0,
            Intent(requireContext(), GeofenceReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or FLAG_MUTABLE
        )

        val request = GeofencingRequest.Builder().apply {
            addGeofence(
                Geofence.Builder()
                    .setRequestId("mygeofence")
                    .setCircularRegion(lat, lon, 300F)
                    .setExpirationDuration(1000L * 60 * 60 * 24)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build()
            )
        }.build()

        geofencingClient.addGeofences(request, geofenceIntent).run {
            addOnSuccessListener {
                Navigation.findNavController(requireView()).navigate(R.id.action_nearbyPubsList_fragment_to_pubList_fragment)
            }
            addOnFailureListener {
                nearbyPubViewModel.show("Zlyhanie vytvorenia Geofence.")
                it.printStackTrace()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun loadNearbyPubs() {
        if (checkLocationPermissions()) {
            nearbyPubViewModel.loading.postValue(true)

            locationClient.getCurrentLocation(
                CurrentLocationRequest.Builder().setDurationMillis(30000)
                    .setMaxUpdateAgeMillis(60000).build(), null
            ).addOnSuccessListener {
                it?.let {
                    showFoundLocationAnimation()
                    nearbyPubViewModel.myLocation.postValue(MyLocationCoordinates(it.latitude, it.longitude))
                } ?:
                nearbyPubViewModel.loading.postValue(false)
            }
        }
        else {
            nearbyPubViewModel.show("Nedali ste povolenie ku polohe a preto nie je možné zobraziť info o blizkych podnikoch")
            Navigation.findNavController(requireView()).navigate (R.id.action_nearbyPubsList_fragment_to_pubList_fragment)
        }
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    private fun locationPermissionInfo() {
        val alertDialog: AlertDialog = requireActivity().let {
            val builder = AlertDialog.Builder(it)
            builder.apply {
                setTitle("Informácia o povolení background polohy")
                setMessage("Je nutné povoliť background polohu na detekciu o opustení podniku. V nasledujúcej notifikácii je nutné vybrať: \"Vždy povoliť informácie o polohe\"")
                setPositiveButton("Ok"
                ) { _, _ ->
                    locationPermissionRequest.launch(
                        arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION))
                }
                setNegativeButton("Nepovoliť"
                ) { _, _ ->
                    nearbyPubViewModel.show("Prístup k background polohe zamietnutý, nie je možné vás označiť do podniku")
                }
            }
            builder.create()
        }
        alertDialog.show()
    }

    private fun checkBackgroundLocationPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        } else
            return true
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

    private fun showSelectPubAnimation() {
        binding.lottieSelectPub.setMinAndMaxFrame(0,90)
        binding.lottieSelectPub.visibility=View.VISIBLE
        binding.lottieSelectPub.playAnimation()
        binding.lottieSelectPub.addAnimatorListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                binding.lottieSelectPub.visibility = View.INVISIBLE
            }
        })
    }

    private fun showFoundLocationAnimation() {
        binding.lottieFoundLocation.visibility=View.VISIBLE
        binding.lottieFoundLocation.playAnimation()
        binding.lottieFoundLocation.addAnimatorListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                binding.lottieFoundLocation.visibility = View.INVISIBLE
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}