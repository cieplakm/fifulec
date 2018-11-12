package com.mmc.fifulec;

<<<<<<< HEAD
=======
import android.app.IntentService;
>>>>>>> 64c0ba63dcc1c1fa23a63b78523b033583b98443
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
<<<<<<< HEAD
import android.content.Context;
import android.util.Log;

=======
import android.content.ComponentName;
import android.content.Intent;
import android.util.Log;

import android.widget.Toast;
import com.google.firebase.database.FirebaseDatabase;
>>>>>>> 64c0ba63dcc1c1fa23a63b78523b033583b98443
import com.mmc.fifulec.model.Challenge;


public class NotificationService extends JobService {

    private MyASyncTask myASyncTask;

    public NotificationService() {
    }

    @Override
    public boolean onStartJob(JobParameters params) {
<<<<<<< HEAD
=======
        Log.e("SERVICY", "onStartJob");
        jobFinished(params, true);
//        FirebaseDatabase instance = FirebaseDatabase.getInstance();
//        userRepository = new FirebaseUserRepository(instance);
//        preferences = new Preferences(this.getSharedPreferences("com.mmc.fifulec_preferences", MODE_PRIVATE));
//
//        challengeService = new ChallengeService(new FirebaseChallengeRepository(instance), new ChallengeMappingService(new FirebaseMappingRepository(instance)));
//
//        Observable<User> userObservable = Observable.just(false)
//                .flatMap(new Function<Boolean, ObservableSource<User>>() {
//                    @Override
//                    public ObservableSource<User> apply(Boolean isUserInAppContext) {
//                        if (isUserInAppContext) {
//                            return null;
//                        } else {
//                            return userRepository.userByUuidObservable(preferences.getUuid());
//                        }
//                    }
//                });
//
//
//        Observable<Challenge> stringObservable = userObservable
//                .flatMap(new Function<User, ObservableSource<String>>() {
//
//                    @Override
//                    public ObservableSource<String> apply(User user) throws Exception {
//                        NotificationService.this.user = user;
//                        return challengeService.observeChallengeChangesOrAdded(user);
//                    }
//                })
//                .flatMap(new Function<String, ObservableSource<Challenge>>() {
//                    @Override
//                    public ObservableSource<Challenge> apply(String s) throws Exception {
//                        return challengeService.challengePerUuid(s);
//                    }
//                })
//                .filter(new Predicate<Challenge>() {
//                    @Override
//                    public boolean test(Challenge challenge) throws Exception {
//                        if (challenge.getLastChangedById() == null) {
//                            return false;
//                        }
//                        return !challenge.getLastChangedById().equals(user.getUuid());
//                    }
//                });
//
//
//        stringObservable.subscribe(new Observer<Challenge>() {
//            @Override
//            public void onSubscribe(Disposable d) {
//
//            }
//
//            @Override
//            public void onNext(Challenge s) {
//                if (preferences.isNotificationActive()) {
//                    FifulecNotification fifulecNotification = new FifulecNotification(NotificationService.this);
//                    if(s.getChallengeStatus()== ChallengeStatus.NOT_ACCEPTED){
//                        fifulecNotification.showNewChallengeNotification(Collections.singletonList(s));
//                    }else{
//                        fifulecNotification.showChallengeChanged(getMessage(s));
//                    }
//                    Log.e("SERVICY", "Challenge");
//                }
//            }
//
//            @Override
//            public void onError(Throwable e) {
//
//            }
//
//            @Override
//            public void onComplete() {
//
//            }
//        });
>>>>>>> 64c0ba63dcc1c1fa23a63b78523b033583b98443

        Log.e("SERVICY", "onStartJob");
        myASyncTask = new MyASyncTask(this);
        myASyncTask.execute();

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.e("SERVICY", "onStopJob");
<<<<<<< HEAD
        myASyncTask.cancel(true);
        return false;
    }

=======
        scheduleRefresh();
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

>>>>>>> 64c0ba63dcc1c1fa23a63b78523b033583b98443
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("SERVICY", "onDestroy");

    }





}
