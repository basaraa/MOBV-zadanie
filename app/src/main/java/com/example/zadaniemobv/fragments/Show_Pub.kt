package com.example.zadaniemobv.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.zadaniemobv.R
import com.example.zadaniemobv.databinding.ShowPubBinding

import com.example.zadaniemobv.datas.*
import com.example.zadaniemobv.others.PreferenceData
import com.example.zadaniemobv.others.provideViewModelFactory
import com.example.zadaniemobv.viewModels.PubsModel


class Show_Pub : Fragment() {
    private val args : Show_PubArgs by navArgs()
    private var _binding: ShowPubBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    private val pubViewModel: PubsModel by lazy{
        ViewModelProvider(
            this,
            provideViewModelFactory(requireContext())
        ).get(PubsModel::class.java)}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ShowPubBinding.inflate(inflater,container,false)
        return binding.root
    }
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val user = PreferenceData.getInstance().getUserItem(requireContext())
        if ((user?.id ?: "").isBlank()) {
            Navigation.findNavController(view).navigate(R.id.action_addFriend_fragment_to_login_fragment)
            return
        }
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = pubViewModel
        }
        pubViewModel.getPub(args.pubId)
        pubViewModel.pub.observe(viewLifecycleOwner) { pub ->
            pub?.apply {
                    binding.podnik.text = pub.name
                    setPhone(pub.phone)
                    setWebsite(pub.website)
                    setOpeningHours(pub.opening_hours)
                    binding.pubUsers.text="Počet používateľov: "+pub.users
                    setMapLatLon(pub.lat, pub.lon)
            }
        }
        binding.logout.setOnClickListener{ listen->
            PreferenceData.getInstance().clearData(requireContext())
            listen.findNavController().navigate (R.id.action_show_Pub_to_login_fragment)
        }

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    @SuppressLint("SetTextI18n")
    fun setPhone (phoneString:String?){
        val phone = binding.phone
        val call_icon = binding.callIcon
        if (phoneString!="") {
            phone.text = phoneString
            call_icon.setVisibility(View.VISIBLE)
            binding.callIcon.setOnClickListener {
                val myUri = Uri.parse("tel:${phoneString}")
                val call_intent = Intent(Intent.ACTION_DIAL, myUri)
                startActivity(call_intent)
            }
        }
        else {
            phone.text="Podnik nemá zverejnené telefónne číslo"
        }
    }
    @SuppressLint("SetTextI18n")
    fun setWebsite (websiteString: String?){
        val website = binding.website
        val web_icon = binding.webIcon
        if (!websiteString.isNullOrBlank()) {
            website.text = websiteString
            web_icon.setVisibility(View.VISIBLE)
            web_icon.setOnClickListener {
                val myUri = Uri.parse(websiteString)
                val url_intent = Intent(Intent.ACTION_VIEW, myUri)
                startActivity(url_intent)
            }
        }
        else {
            website.text="Podnik nemá zverejnenú webovú stránku"
        }
    }
    @SuppressLint("SetTextI18n")
    fun setOpeningHours (openingHoursString: String?){
        val opening_hours = binding.openingHours
        if (!openingHoursString.isNullOrBlank())
            opening_hours.text=openingHoursString
        else opening_hours.text="Podnik nemá zverejnené otváracie hodiny"
    }

    fun setMapLatLon (latString: String?,lonString:String?){
        if (!(latString.isNullOrBlank() && lonString.isNullOrBlank()))
            binding.showMap.setOnClickListener {
                val myUri =
                Uri.parse("geo:${latString},${lonString}")
                val mapIntent = Intent(Intent.ACTION_VIEW, myUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                startActivity(mapIntent)
        }
    }

}