package com.example.zadaniemobv.database

import android.location.Location
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.zadaniemobv.datas.MyLocationCoordinates
import com.example.zadaniemobv.datas.PubUser

@Entity(tableName = "pubs")
data class PubUsersEntity (
    @PrimaryKey
    val id: String,
    val name: String,
    val type: String,
    val lat: String,
    val lon: String,
    val users: Int,
    var distance: Double?
){
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
fun List<PubUsersEntity>.fromDatabaseToNormalInstance(): List<PubUser> {
    return map {
        PubUser(
            id = it.id,
            name = it.name,
            type = it.type,
            lat = it.lat,
            lon = it.lon,
            users=it.users,
            distance=it.distance?:0.0
        )
    }
}