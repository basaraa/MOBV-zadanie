package com.example.zadaniemobv.webservice

import android.content.Context
import com.example.zadaniemobv.others.PreferenceData
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Route

class AuthAccessToken(val context: Context) : Authenticator {
    override fun authenticate(route: Route?, response: okhttp3.Response): Request? {
        synchronized(this) {
            if (response.request().header("needAuth")
                    ?.compareTo("yes") == 0 && response.code() == 401
            ) {
                val userItem = PreferenceData.getInstance().getUserItem(context)
                if (userItem == null) {
                    PreferenceData.getInstance().clearData(context)
                    return null
                }
                val tokenResponse = WebService.MpageWebApi(context).refreshAccess(
                    RefreshTokenPostBody(
                        refresh = userItem.refresh
                    )
                ).execute()
                if (tokenResponse.isSuccessful) {
                    tokenResponse.body()?.let {
                        PreferenceData.getInstance().putUserItem(context, it)
                        return response.request().newBuilder()
                            .header("authorization", "Bearer ${it.access}")
                            .build()
                    }
                }
                PreferenceData.getInstance().clearData(context)
                return null
            }
        }
        return null
    }
}