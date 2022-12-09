package com.example.zadaniemobv.geofence

import android.app.Notification
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.example.zadaniemobv.R
import com.example.zadaniemobv.webservice.CheckIntoPubPostBody
import com.example.zadaniemobv.webservice.WebService

class GeofenceCheckInOutBuilder (private val context: Context, workerParams: WorkerParameters) :
CoroutineWorker(context, workerParams) {

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return ForegroundInfo(1, setCheckoutInfo())
    }
    override suspend fun doWork(): Result {
        val response = WebService.MpageWebApi(context).checkIntoPub(
            CheckIntoPubPostBody(
                "",
                "",
                "",
                0.0,
                0.0
            )
        )
        if (response.isSuccessful)
            return Result.success()
        else
            return Result.failure()
    }
    private fun setCheckoutInfo(): Notification {
        val builder =
            NotificationCompat.Builder(context, "CheckOutPubInfo").apply {
                setContentTitle("Info o opustení podniku")
                setContentText("Odchádzaš z podniku ...")
                //setSmallIcon(R.drawable.ic_baseline_location_on_24)
                priority = NotificationCompat.PRIORITY_DEFAULT
            }
        return builder.build()
    }
}