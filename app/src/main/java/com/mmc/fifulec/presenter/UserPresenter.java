package com.mmc.fifulec.presenter;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.util.Pair;
import com.mmc.fifulec.NotificationService;
import com.mmc.fifulec.broadcastreciver.Alarm;
import com.mmc.fifulec.broadcastreciver.NotificationBroadcast;
import com.mmc.fifulec.contract.UserContract;
import com.mmc.fifulec.di.AppScope;
import com.mmc.fifulec.model.ChallengeScoreType;
import com.mmc.fifulec.model.User;
import com.mmc.fifulec.service.ChallengeService;
import com.mmc.fifulec.utils.AppContext;
import com.mmc.fifulec.utils.Preferences;
import com.mmc.fifulec.utils.StatsMaper;
<<<<<<< HEAD

import javax.inject.Inject;

=======
>>>>>>> 64c0ba63dcc1c1fa23a63b78523b033583b98443
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

import javax.inject.Inject;

import static android.content.Context.ACTIVITY_SERVICE;

@AppScope
public class UserPresenter {

    private UserContract.View view;

    private AppContext appContext;

    private ChallengeService challengeService;

    private Preferences preferences;

    @Inject
    public UserPresenter(AppContext appContext, ChallengeService challengeService, Preferences preferences) {
        this.appContext = appContext;
        this.challengeService = challengeService;
        this.preferences = preferences;
    }

    public void onCreate(final UserContract.View view) {
        this.view = view;
    }

    public void onResume() {
        view.setUserNickTitle(appContext.getUser().getNick());
        setupNotification();

        final User user = appContext.getUser();

        challengeService.observeChallengeChangesOrAdded(user)
                .doOnNext(new Consumer<String>() {
                    @Override
                    public void accept(String s) {
                        updaeScore();
                    }
                })
                .subscribe();
    }

    private void updaeScore() {
        User user = appContext.getUser();
        StatsMaper statsMaper = new StatsMaper(challengeService.challengesPerUser(user));

        statsMaper.amountChallenge()
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        view.setAmountChallenges(aLong.intValue());
                    }
                });


        statsMaper.goalBalance(user.getUuid())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        view.setGoolsBilance(s);
                    }
                });

<<<<<<< HEAD
=======
        statsMaper.goalBalance(user.getUuid())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String s) {
                        view.setGoolsBilance(s);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

>>>>>>> 64c0ba63dcc1c1fa23a63b78523b033583b98443
        statsMaper.winDrawLose(user)
                .subscribe(new Observer<Pair<ChallengeScoreType, Integer>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Pair<ChallengeScoreType, Integer> challengeScoreTypeIntegerPair) {
                        if (challengeScoreTypeIntegerPair.first == ChallengeScoreType.WIN) {
                            view.setWinsAmount(Long.toString(challengeScoreTypeIntegerPair.second));
                        } else if (challengeScoreTypeIntegerPair.first == ChallengeScoreType.DRAW) {
                            view.setDrawAmount(Long.toString(challengeScoreTypeIntegerPair.second));
                        } else {
                            view.setLoseAmount(Long.toString(challengeScoreTypeIntegerPair.second));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void setupNotification() {
        view.setNotiSwitchActive(preferences.isNotificationActive());

    }

    public void onNotiSwitchChanged(boolean isChecked) {
        preferences.putNotificationActive(isChecked);
        switchNotificationAlarm(isChecked);
<<<<<<< HEAD
        Context view = (Context) this.view;

        if (isChecked) {
            if (!isServiceRunning(view)) {
                view.startService(new Intent(view, NotificationService.class));
=======
        Context context = (Context) this.view;

        if (isChecked) {
            if (!isServiceRunning(context)) {
                context.startService(new Intent(context, NotificationService.class));
>>>>>>> 64c0ba63dcc1c1fa23a63b78523b033583b98443
            }
        }
    }

    private boolean isServiceRunning(Context view) {
        ActivityManager manager = (ActivityManager) view.getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.mmc.fifulec.NotificationService".equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void switchNotificationAlarm(boolean isChecked) {

<<<<<<< HEAD

=======
>>>>>>> 64c0ba63dcc1c1fa23a63b78523b033583b98443
        Alarm alarm = new Alarm((Context) view);
        if (isChecked) {
            alarm.on(NotificationBroadcast.class, 1000L * 60 * 1);
        } else {
            alarm.off(NotificationBroadcast.class);
        }
    }
}
