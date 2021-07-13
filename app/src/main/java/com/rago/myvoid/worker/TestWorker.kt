package com.rago.myvoid.worker

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.*
import com.rago.myvoid.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.util.*

class TestWorker(appContext: Context, workerParameters: WorkerParameters) :
    CoroutineWorker(appContext, workerParameters) {


    private lateinit var mNotifyBuilder: NotificationCompat.Builder

    override suspend fun doWork(): Result {
        var result = 0
        withContext(Dispatchers.IO) {
            println("--------------------------------------------------------")
            println("|INI TestWorker : Prueba ${Calendar.getInstance().time}|")
            println("--------------------------------------------------------")
            setForeground(createForegroundInfo())
            //delay(20000)
            for (i in 1..101) {
                delay(200)
                mNotifyBuilder.setContentText("Actualizacion de $i de 100")
                mNotifyBuilder.setNumber(i)
                setForeground(ForegroundInfo(1, mNotifyBuilder.build()))
            }
            println("--------------------------------------------------------")
            println("|FIN TestWorker : Prueba ${Calendar.getInstance().time}|")
            println("--------------------------------------------------------")
            result = 1
        }
        println("---------------------")
        println("|Result.success()   |")
        println("---------------------")


        return when (result) {
            0 -> {
                Result.failure()
            }
            1 -> {
                Result.success()
            }
            else -> {
                Result.retry()
            }
        }
    }

    private fun createForegroundInfo(): ForegroundInfo {
        val intent = WorkManager.getInstance(applicationContext)
            .createCancelPendingIntent(id)

        mNotifyBuilder = NotificationCompat.Builder(
            applicationContext, "workUpload"
        )
            .setContentIntent(intent)
            .setContentTitle("Uploading Supplies")
            .setTicker("Downloading Your Image")
            .setSmallIcon(android.R.drawable.stat_sys_upload)
            .setOngoing(true)
            .addAction(android.R.drawable.ic_delete, "Cancel Download", intent)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            println("--------------------------------------------------------")
            println("|SDK_INT                                               |")
            println("--------------------------------------------------------")
            createChannel(mNotifyBuilder, "workUpload")
        }
        return ForegroundInfo(1, mNotifyBuilder.build())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel(notification: NotificationCompat.Builder, id: String) {

        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as
                    NotificationManager
        notification.setDefaults(Notification.DEFAULT_VIBRATE)
        val channel = NotificationChannel(
            id,
            "MyVoidApp",
            NotificationManager.IMPORTANCE_LOW
        )
        channel.description = "WorkManagerApp Notifications"
        notificationManager.createNotificationChannel(channel)
    }
}