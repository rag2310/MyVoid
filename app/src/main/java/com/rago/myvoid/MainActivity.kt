package com.rago.myvoid

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.rago.myvoid.worker.TestWorker

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val workManagerButton = findViewById<Button>(R.id.workManagerBtn)
        val logTextView = findViewById<TextView>(R.id.logTv)

        val testWorkRequest: WorkRequest = OneTimeWorkRequestBuilder<TestWorker>().build()
        workManagerButton.setOnClickListener {
            WorkManager.getInstance(applicationContext).enqueue(testWorkRequest)
        }

        WorkManager.getInstance(applicationContext).getWorkInfoByIdLiveData(testWorkRequest.id).observe(this,{
            it?.let {
                logTextView.append("testWorkRequest: ${it.state} \n")
            }
        })

    }
}