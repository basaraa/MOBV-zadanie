package com.example.zadaniemobv.others

import android.content.Context
import android.view.View
import androidx.databinding.BindingAdapter
import androidx.lifecycle.ViewModelProvider
import com.example.zadaniemobv.database.CreateDatabaseClass
import com.example.zadaniemobv.datas.Repository
import com.example.zadaniemobv.viewModels.ViewModelFactory
import com.example.zadaniemobv.webservice.WebService.Companion.MpageWebApi
import com.google.android.material.snackbar.Snackbar
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom

fun provideDataRepository(context: Context): Repository {
    return Repository.getInstance(
        MpageWebApi(context),CreateDatabaseClass.getDatabaseInstance(context).databaseDAO(),
    )
}

fun provideViewModelFactory(context: Context): ViewModelProvider.Factory {
    return ViewModelFactory(
        provideDataRepository(
            context
        )
    )
}


@BindingAdapter("showText")
fun useShowText(
    view: View,
    message: LiveDataEvents<String>?
) {
    message?.getContentIfNotHandled()?.let {
        Snackbar.make(view, it, Snackbar.LENGTH_SHORT).show()
    }
}



