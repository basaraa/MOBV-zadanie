package com.example.zadaniemobv.webservice


import com.example.zadaniemobv.database.FriendsEntity
import com.example.zadaniemobv.database.PubUsersEntity
import com.example.zadaniemobv.datas.Pub
import com.google.gson.annotations.SerializedName


//Informácie o blízkych podnikov / info o podniku
data class WebPubs (@SerializedName("elements") val pubs: List<WebPub>)
data class WebPub(
    @SerializedName("id")
    val id: String,
    @SerializedName("type")
    val type: String?,
    @SerializedName("lat")
    val lat: String,
    @SerializedName("lon")
    val lon: String,
    @SerializedName("tags")
    val tags: Tags,
)
data class Tags(
    @SerializedName("name")
    val name: String ?,
    @SerializedName("opening_hours")
    val opening_hours: String?,
    @SerializedName("phone")
    val phone: String?,
    @SerializedName("website")
    val website: String?
)

fun WebPub.fromWebToNormalInstance(): Pub {
    return Pub(
        id = id,
        name = tags.name?:"",
        type = type?:"",
        lat = lat,
        lon = lon,
        opening_hours = tags.opening_hours?:"",
        phone = tags.phone?:"",
        website = tags.website?:"")
}

data class WebUser(
    @SerializedName("uid")
    val id: String,
    @SerializedName("access")
    val access: String,
    @SerializedName("refresh")
    val refresh: String
)
data class WebPubUser(
    @SerializedName("bar_id")
    val bar_id: String,
    @SerializedName("bar_name")
    val bar_name: String,
    @SerializedName("bar_type")
    val bar_type: String,
    @SerializedName("lat")
    val lat: String,
    @SerializedName("lon")
    val lon: String,
    @SerializedName("users")
    val users: Int,
)
fun WebPubUser.fromWebtoDatabaseInstance(): PubUsersEntity {
    return PubUsersEntity(
            id = bar_id,
            name = bar_name,
            type = bar_type,
            lat = lat,
            lon = lon,
            users=users,
            distance=null
        )
}


data class WebFriend(
    @SerializedName("user_id")
    val friend_id: String,
    @SerializedName("user_name")
    val friend_username: String,
    @SerializedName("bar_id")
    val pub_id: String?,
    @SerializedName("bar_name")
    val pub_name: String?,
    @SerializedName("bar_lat")
    val lat: String?,
    @SerializedName("bar_lon")
    val lon: String?
)
fun WebFriend.fromWebtoDatabaseInstance(id:String): FriendsEntity {
    return FriendsEntity(
        id =null,
        your_id =id,
        friend_id = friend_id,
        friend_username = friend_username,
        pub_id = pub_id?:"",
        pub_name = pub_name?:"",
        lat = lat?:"",
        lon = lon?:"",
    )
}

