package com.mmc.fifulec;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;

import android.util.Log;


import android.content.ComponentName;

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

    private void scheduleRefresh() {
        ComponentName componentName = new ComponentName(this, NotificationService.class);
        JobInfo jobInfo = new JobInfo.Builder(12, componentName)
                .setMinimumLatency(1000*5)
                .setOverrideDeadline(10 * 1000)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                .build();

        JobScheduler jobScheduler = (JobScheduler) this.getSystemService(JOB_SCHEDULER_SERVICE);

        int schedule = jobScheduler.schedule(jobInfo);

        if (schedule == JobScheduler.RESULT_SUCCESS) {
            Log.d("SERVICY", "Job scheduled form scheduleRefresh");
        } else {
            Log.d("SERVICY", "Job not scheduled from scheduleRefresh");
        }

    }

    private String getMessage(Challenge challenge) {
        String becouseOf;
        if (challenge.getFromUserUuid().equals(challenge.getLastChangedById())) {
            becouseOf = challenge.getFromUserNick();
        } else {
            becouseOf = challenge.getToUserNick();
        }

        String verb = "podjął akcję.";
        switch (challenge.getChallengeStatus()) {
            case NOT_ACCEPTED:
                verb = "wyzwał Cię!";
                break;
            case FINISHED:
                verb = "potwierdził wynik. Wyzwanie zostało zakończone.";
                break;
            case ACCEPTED:
                verb = "przyjął wyzwanie.";
                break;
            case NOT_CONFIRMED:
                verb = "wprowadził wynik i czeka na jego potwierdzenie.";
                break;
            case REJECTED:
                verb = "odrzucił wyzwanie.";
                break;
        }


        return becouseOf + " " + verb;
    }

}
