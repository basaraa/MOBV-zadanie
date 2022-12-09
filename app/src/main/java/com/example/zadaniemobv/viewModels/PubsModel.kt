package com.example.zadaniemobv.viewModels


import androidx.lifecycle.*
import com.example.zadaniemobv.datas.MyLocationCoordinates
import com.example.zadaniemobv.datas.Pub
import com.example.zadaniemobv.datas.PubUser
import com.example.zadaniemobv.datas.Repository
import com.example.zadaniemobv.others.LiveDataEvents
import kotlinx.coroutines.launch


class PubsModel(
    private val repository: Repository
    ) : ViewModel() {
        val loading: MutableLiveData<Boolean> = MutableLiveData(false)
        private var sortByName: MutableLiveData<Boolean?> = MutableLiveData(true)
        private var sortByUsers: MutableLiveData<Boolean?> = MutableLiveData(true)
        private var sortByDistance: MutableLiveData<Boolean?> = MutableLiveData(true)
        private val _message = MutableLiveData<LiveDataEvents<String>>()
        val message: LiveData<LiveDataEvents<String>>
        get() = _message
        var myLocation : MyLocationCoordinates?=null
        val pubsList: LiveData<List<PubUser>> =
                Transformations.switchMap(sortByName) { sort ->
                    if (sort != null){
                        if (sort==true) repository.getAllPubsByNameAsc()
                        else repository.getAllPubsByNameDesc()
                    }
                    else {
                        Transformations.switchMap(sortByUsers) { sort2 ->
                            if (sort2 != null) {
                                if (sort2 == true) repository.getAllPubsByUsersAsc()
                                else repository.getAllPubsByUsersDesc()
                            }
                            else Transformations.switchMap(sortByDistance) { sort3 ->
                                if (sort3 == true) repository.getAllPubsByDistanceAsc()
                                else repository.getAllPubsByDistanceDesc()
                            }
                        }

                    }
                }
        var pub: MutableLiveData<Pub> = MutableLiveData(null)
        fun sortPubList(){
            sortByUsers.value=null
            sortByDistance.value=null
            if (sortByName.value==null)
                sortByName.value= true
            else
                sortByName.value = (sortByName.value)?.not()
        }
        fun sortPubListByUsers(){
            sortByName.value=null
            sortByDistance.value=null
            if (sortByUsers.value==null)
                sortByUsers.value=true
            else
                sortByUsers.value=(sortByUsers.value)?.not()
        }
        fun sortPubListByDistance(){
            sortByName.value=null
            sortByUsers.value=null
            if (sortByDistance.value==null)
                sortByDistance.value=true
            else
                sortByDistance.value=(sortByDistance.value)?.not()

        }
        fun reloadPubUsersFromWebsite() {
            viewModelScope.launch {
                loading.postValue(true)
                myLocation?.let { location->
                    repository.updatePubUsers(
                        { _message.postValue(LiveDataEvents(it)) },
                        location.lat,
                        location.lon
                    )
                } ?:  repository.updatePubUsers(
                    { _message.postValue(LiveDataEvents(it)) },
                    null,
                    null
                )
                loading.postValue(false)
            }
        }
        fun getPub(id: String) {
            if (id.isBlank())
                return
            viewModelScope.launch {
                loading.postValue(true)
                pub.postValue(repository.WebgetPub(id,{ _message.postValue(LiveDataEvents(it))}))
                loading.postValue(false)
            }

        }
        fun show(msg: String){ _message.postValue(LiveDataEvents(msg))}
}


