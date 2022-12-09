package com.example.zadaniemobv.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zadaniemobv.datas.Repository
import com.example.zadaniemobv.webservice.WebUser
import com.example.zadaniemobv.others.LiveDataEvents
import kotlinx.coroutines.launch

class LoginRegisterModel (val repository: Repository) : ViewModel() {
    val loading: MutableLiveData<Boolean> = MutableLiveData(false)
    private val _message = MutableLiveData<LiveDataEvents<String>>()
    val message: LiveData<LiveDataEvents<String>>
        get() = _message
    val user= MutableLiveData<WebUser>(null)
    fun userRegistration (name: String, password: String){
                viewModelScope.launch {
                    loading.postValue(true)
                    repository.WebUserRegistration(name,password,
                        {_message.postValue(LiveDataEvents(it))},
                        {user.postValue(it)}
                    )
                    loading.postValue(false)
                }
            }
    fun userLogin(name: String, password: String){
        viewModelScope.launch {
            loading.postValue(true)
            repository.WebUserLogin(
                name,password,
                { _message.postValue(LiveDataEvents(it)) },
                { user.postValue(it) }
            )
            loading.postValue(false)
        }
    }
    fun show(msg: String){
        _message.postValue(LiveDataEvents(msg))
    }
}
