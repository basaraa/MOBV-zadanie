package com.example.zadaniemobv.webservice

import android.content.Context

import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.*
import retrofit2.converter.gson.GsonConverterFactory


private const val base_url = "https://zadanie.mpage.sk/"
interface WebService{

    //registrácia
    @POST("user/create.php")
    suspend fun registerUser(@Body userRegisterLoginPostBody: UserRegisterLoginPostBody) : Response<WebUser>

    //login
    @POST("user/login.php")
    suspend fun loginUser(@Body userRegisterLoginPostBody: UserRegisterLoginPostBody) : Response<WebUser>

    //refresh access
    @POST("user/refresh.php")
    fun refreshAccess(@Body refreshToken: RefreshTokenPostBody) : Call<WebUser>

    //zoznam podnikov s aspoň 1 používateľom
    @Headers("needAuth: yes")
    @GET("bar/list.php")
    suspend fun findPubUsers() : Response<List<WebPubUser>>

    //získanie informácii o podniku
    @GET("https://overpass-api.de/api/interpreter?")
    suspend fun getPubDetail(@Query("data") query:String): Response <WebPubs>

    //zoznam blízkych podnikov
    @GET("https://overpass-api.de/api/interpreter?")
    suspend fun findNearbyPubs (@Query("data") data: String) : Response <WebPubs>

    //zapísanie a odpísanie sa do podniku
    @Headers("needAuth: yes")
    @POST("bar/message.php")
    suspend fun checkIntoPub (@Body checkIntoPubPostBody: CheckIntoPubPostBody) : Response <Any>

    //zoznam kamarátov
    @Headers("needAuth: yes")
    @GET("contact/list.php")
    suspend fun findFriends() : Response<List<WebFriend>>

    //pridanie kamaráta
    @Headers("needAuth: yes")
    @POST("contact/message.php")
    suspend fun addFriend(@Body addFriendPostBody: AddFriendPostBody) : Response<Void>

    //odstránenie kamaráta
    @Headers("needAuth: yes")
    @POST("contact/delete.php")
    suspend fun deleteFriend(@Body addFriendPostBody: AddFriendPostBody) : Response<Void>


    companion object {
        fun MpageWebApi(context: Context): WebService{
            val client = OkHttpClient.Builder().addInterceptor(Interceptor(context))
                .authenticator(AuthAccessToken(context)).build()
            val retrofit = Retrofit.Builder()
                .baseUrl(base_url).client(client).addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(WebService::class.java)
        }
    }
}









