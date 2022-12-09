package com.example.zadaniemobv.datas

import android.location.Location


data class Pub (
    val id: String,
    val name: String,
    val type: String,
    val lat:String,
    val lon:String,
    val opening_hours: String,
    val phone : String,
    val website : String,
    var distance: Double = 0.0,
    var users: Int = 0
    ) {
        fun distanceTo(myLocationCoordinates: MyLocationCoordinates): Double{
            return Location("").apply {
                latitude=lat.toDouble()
                longitude=lon.toDouble()
            }.distanceTo(Location("").apply {
                latitude=myLocationCoordinates.lat
                longitude=myLocationCoordinates.lon
            }).toDouble()
        }
    }


    var pubs : MutableList<Pub> = ArrayList()

