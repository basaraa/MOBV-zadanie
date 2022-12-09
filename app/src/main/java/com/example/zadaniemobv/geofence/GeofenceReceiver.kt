package com.example.zadaniemobv.geofence

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent

class GeofenceReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val geofenceIntent = intent?.let { GeofencingEvent.fromIntent(it) }
        if (geofenceIntent != null) {
            if (geofenceIntent.hasError()) {
                val errorMessage = GeofenceStatusCodes
                    .getStatusCodeString(geofenceIntent.errorCode)
                return
            }
        }
        else return
        val geofenceTransition = geofenceIntent.geofenceTransition
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            val triggeringGeofences = geofenceIntent.triggeringGeofences
            triggeringGeofences?.forEach {
                if (it.requestId.compareTo("mygeofence") == 0) {
                    context?.let { context ->
                        val uploadWorkRequest: WorkRequest = OneTimeWorkRequestBuilder<GeofenceCheckInOutBuilder>()
                                .build()
                        WorkManager.getInstance(context).enqueue(uploadWorkRequest)
                    }
                }
                else return
            }
        }
        else return
    }
}