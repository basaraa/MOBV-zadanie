package com.example.zadaniemobv.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.zadaniemobv.datas.Repository

class ViewModelFactory(private val repository: Repository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PubsModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PubsModel(repository) as T
        }
        if (modelClass.isAssignableFrom(LoginRegisterModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginRegisterModel(repository) as T
        }
        if (modelClass.isAssignableFrom(FriendsModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FriendsModel(repository) as T
        }
        if (modelClass.isAssignableFrom(NearbyPubsModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NearbyPubsModel(repository) as T
        }
            throw IllegalArgumentException("Nie je možné vytvoriť viewModel")
    }
}