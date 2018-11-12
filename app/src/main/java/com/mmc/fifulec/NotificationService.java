package com.mmc.fifulec;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.Context;
import android.util.Log;

import com.mmc.fifulec.model.Challenge;


public class NotificationService extends JobService {

    private MyASyncTask myASyncTask;

    public NotificationService() {
    }

    @Override
    public boolean onStartJob(JobParameters params) {

        Log.e("SERVICY", "onStartJob");
        myASyncTask = new MyASyncTask(this);
        myASyncTask.execute();

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.e("SERVICY", "onStopJob");
        myASyncTask.cancel(true);
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("SERVICY", "onDestroy");

    }





}
