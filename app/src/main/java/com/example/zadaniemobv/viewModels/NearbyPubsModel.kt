package com.example.zadaniemobv.viewModels

import androidx.lifecycle.*
import com.example.zadaniemobv.datas.MyLocationCoordinates
import com.example.zadaniemobv.datas.Pub
import com.example.zadaniemobv.datas.Repository
import com.example.zadaniemobv.others.LiveDataEvents
import kotlinx.coroutines.launch

class NearbyPubsModel(
    private val repository: Repository
    ) : ViewModel() {
    val loading: MutableLiveData<Boolean> = MutableLiveData(false)
    val loadingCheckPub : MutableLiveData<Boolean> = MutableLiveData(false)
    private val _message = MutableLiveData<LiveDataEvents<String>>()
    val message: LiveData<LiveDataEvents<String>>
        get() = _message
    val myLocation = MutableLiveData<MyLocationCoordinates>(null)
    val pub= MutableLiveData<Pub>(null)
    private val _checkedIn = MutableLiveData<LiveDataEvents<Boolean>>()
    val checkedIn: LiveData<LiveDataEvents<Boolean>>
        get() = _checkedIn
    val pubs : LiveData<List<Pub>> = myLocation.switchMap { location ->
        liveData {
            loading.postValue(true)
            location?.let {
                val b = repository.WebFindNearbyPubs(location.lat,location.lon,{_message.postValue(LiveDataEvents(it))})
                emit(b)
                if (pub.value==null){
                    pub.postValue(b.firstOrNull())
                }
            } ?: emit(listOf())
            loading.postValue(false)
        }
    }

    fun checkIntoPub(){
        viewModelScope.launch {
            loadingCheckPub.postValue(true)
            pub.value?.let {
                repository.WebCheckIntoPub(
                    it,
                    {_message.postValue(LiveDataEvents(it))},
                    {_checkedIn.postValue(LiveDataEvents(it))})
            }
            loadingCheckPub.postValue(false)
        }
    }

    fun show(msg: String){ _message.postValue(LiveDataEvents(msg))}
}