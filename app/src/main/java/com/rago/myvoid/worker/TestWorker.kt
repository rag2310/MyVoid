package com.rago.myvoid.worker

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.util.*

class TestWorker(appContext: Context, workerParameters: WorkerParameters) :
    CoroutineWorker(appContext, workerParameters) {

    private lateinit var mNotifyBuilder: NotificationCompat.Builder

    override suspend fun doWork(): Result {
        val id = Calendar.getInstance().time.time.toInt()
        var result = 0
        withContext(Dispatchers.IO) {
            println("--------------------------------------------------------")
            println("|INI TestWorker : Prueba ${Calendar.getInstance().time}|")
            println("--------------------------------------------------------")
            setForeground(createForegroundInfo(id))
            delay(2000)
            for (i in 1..101) {
                delay(200)
                mNotifyBuilder.setContentText("Actualizacion de $i de 100")
                mNotifyBuilder.setNumber(i)
                setForeground(ForegroundInfo(id, mNotifyBuilder.build()))
            }
            println("--------------------------------------------------------")
            println("|FIN TestWorker : Prueba ${Calendar.getInstance().time}|")
            println("--------------------------------------------------------")
            result = 0
        }


        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as
                    NotificationManager
        val builder = NotificationCompat.Builder(applicationContext, "finishUpload")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setDefaults(Notification.DEFAULT_VIBRATE)
            val channel = NotificationChannel(
                "finishUpload",
                "MyVoidApp",
                NotificationManager.IMPORTANCE_LOW
            )
            channel.description = "WorkManagerApp Notifications"
            notificationManager.createNotificationChannel(channel)
        }
        return when (result) {
            0 -> {
                println("---------------------")
                println("|Result.failure()   |")
                println("---------------------")
                notificationManager.notify(
                    id + 1,
                    builder.setContentText("Failure!")
                        .setAutoCancel(true)
                        .setSmallIcon(android.R.drawable.stat_sys_warning)
                        .setOngoing(false)
                        .build()
                )
                Result.failure()
            }
            1 -> {
                println("---------------------")
                println("|Result.success()   |")
                println("---------------------")
                notificationManager.notify(
                    id + 1,
                    builder.setContentText("Done!")
                        .setAutoCancel(true)
                        .setSmallIcon(android.R.drawable.stat_sys_upload_done)
                        .setOngoing(false)
                        .build()
                )
                Result.success()
            }
            else -> {
                Result.retry()
            }
        }
    }

    private fun createForegroundInfo(idChannel: Int): ForegroundInfo {
        val intent = WorkManager.getInstance(applicationContext)
            .createCancelPendingIntent(id)

        mNotifyBuilder = NotificationCompat.Builder(
            applicationContext, "workUpload"
        )
            .setContentIntent(intent)
            .setContentTitle("Uploading Supplies")
            .setTicker("Downloading Your Image")
            .setSmallIcon(android.R.drawable.stat_sys_upload)
            .setOngoing(false)
            .addAction(android.R.drawable.ic_delete, "Cancel Download", intent)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            println("--------------------------------------------------------")
            println("|SDK_INT                                               |")
            println("--------------------------------------------------------")
            createChannel(mNotifyBuilder, "workUpload")
        }
        return ForegroundInfo(idChannel, mNotifyBuilder.build())
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