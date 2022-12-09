package com.example.zadaniemobv.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface DatabaseDAO{
    //Pubs
    @Insert(onConflict= OnConflictStrategy.REPLACE)
    fun insertAllPubUsers (pubusers:List<PubUsersEntity>)
    @Query("SELECT * from pubs where id = :id limit 1")
    fun getPub(id:String): PubUsersEntity
    @Query("delete from pubs")
    fun clearPubUsers()
    @Query("SELECT * from pubs ORDER BY name ASC")
    fun getAllPubsByNameAsc(): LiveData<List<PubUsersEntity>>
    @Query("SELECT * from pubs ORDER BY name DESC")
    fun getAllPubsByNameDesc(): LiveData<List<PubUsersEntity>>
    @Query("SELECT * from pubs ORDER BY users ASC")
    fun getAllPubsByUsersAsc(): LiveData<List<PubUsersEntity>>
    @Query("SELECT * from pubs ORDER BY users DESC")
    fun getAllPubsByUsersDesc(): LiveData<List<PubUsersEntity>>
    @Query("SELECT *from pubs ORDER BY distance ASC")
    fun getAllPubsByDistanceAsc(): LiveData<List<PubUsersEntity>>
    @Query("SELECT * from pubs ORDER BY distance DESC ")
    fun getAllPubsByDistanceDesc(): LiveData<List<PubUsersEntity>>

    //Friends
    @Insert (onConflict = OnConflictStrategy.REPLACE)
    fun insertAllFriends (friends:List<FriendsEntity>)
    @Query("delete from friends where your_id= :id")
    fun clearFriends(id: String)
    @Query("SELECT * from friends where your_id=:id ORDER BY friend_username ASC")
    fun getAllFriendsByNameAsc(id: String): LiveData<List<FriendsEntity>>
    @Query("SELECT * from friends where your_id=:id ORDER BY friend_username DESC")
    fun getAllFriendsByNameDesc(id :String): LiveData<List<FriendsEntity>>
}