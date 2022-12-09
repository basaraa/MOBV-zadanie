package com.example.zadaniemobv.database


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.zadaniemobv.datas.Friend


@Entity(tableName = "friends")
data class FriendsEntity(
    @PrimaryKey(autoGenerate=true)
    val id: Long?,
    val your_id:String,
    val friend_id: String,
    val friend_username: String,
    val pub_id: String,
    val pub_name: String,
    val lat: String,
    val lon: String
)
fun List<FriendsEntity>.fromDatabaseToNormalInstance(): List<Friend> {
    return map {
        Friend(
            id=it.your_id,
            friend_id = it.friend_id,
            friend_username = it.friend_username,
            pub_id = it.pub_id,
            pub_name = it.pub_name,
            lat = it.lat,
            lon = it.lon,
        )
    }
}