package com.example.zadaniemobv.viewModels

import androidx.lifecycle.*
import com.example.zadaniemobv.datas.*
import com.example.zadaniemobv.others.LiveDataEvents
import kotlinx.coroutines.launch

class FriendsModel(
    private val repository: Repository,
) : ViewModel() {
    val loading: MutableLiveData<Boolean> = MutableLiveData(false)
    private var sortByName: MutableLiveData<Boolean> = MutableLiveData(true)
    private val _message = MutableLiveData<LiveDataEvents<String>>()
    val message: LiveData<LiveDataEvents<String>>
        get() = _message
    lateinit var friendsList: LiveData<List<Friend>>
    fun loadFriends (userId:String) {
            friendsList=Transformations.switchMap(sortByName) { sort ->
                if (sort) repository.getAllFriendsByNameAsc(userId) else repository.getAllFriendsByNameDesc(userId)
            }
        }
    fun sortFriendList(id:String){
        sortByName.value = (sortByName.value)?.not()
        loadFriends(id)
    }
    fun reloadFriendsFromWebsite(userId:String) {
        viewModelScope.launch {
            loading.postValue(true)
            repository.updateFriends({_message.postValue(LiveDataEvents(it))},userId)
            loading.postValue(false)
        }

    }
    fun addFriend(friend_name: String){
        viewModelScope.launch {
            loading.postValue(true)
            repository.WebAddFriend(
                friend_name,
                { _message.postValue(LiveDataEvents(it))}
            )
            loading.postValue(false)
        }
    }
    fun deleteFriend(friend_name: String){
        viewModelScope.launch {
            loading.postValue(true)
            repository.WebDeleteFriend(
                friend_name,
                { _message.postValue(LiveDataEvents(it)) },
            )
            loading.postValue(false)
        }
    }
}