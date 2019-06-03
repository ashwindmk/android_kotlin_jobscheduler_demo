package com.ashwin.android.example.jobschedulerdemo

import android.app.Activity
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View

class MainActivity : Activity() {
    private var jobId = 0
    lateinit private var serviceComponent: ComponentName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        serviceComponent = ComponentName(this, MyJobService::class.java)
    }

    fun scheduleJob(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val builder = JobInfo.Builder(jobId++, serviceComponent)

            builder.setMinimumLatency(5000L)

            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)

            val extras = PersistableBundle()
            extras.putLong("work_duration", 10000L)
            builder.setExtras(extras)

            val result = (getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler).schedule(builder.build())
            Log.w("debug-log", "scheduled job, result: $result")
        } else {
            Log.e("debug-log", "os below lollipop (21)")
        }
    }
}
