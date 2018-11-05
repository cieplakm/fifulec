package com.mmc.fifulec.presenter;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.util.Pair;

import com.mmc.fifulec.MyIntentService;
import com.mmc.fifulec.utils.StatsMaper;
import com.mmc.fifulec.broadcastreciver.Alarm;
import com.mmc.fifulec.broadcastreciver.NotificationBroadcast;
import com.mmc.fifulec.contract.UserContract;
import com.mmc.fifulec.di.AppScope;
import com.mmc.fifulec.model.ChallengeScoreType;
import com.mmc.fifulec.model.User;
import com.mmc.fifulec.service.ChallengeService;
import com.mmc.fifulec.utils.AppContext;
import com.mmc.fifulec.utils.Preferences;

import javax.inject.Inject;

import io.reactivex.Observer;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;

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

    public void onChallengesClickedClicked() {
        view.openChallengesList();
    }

    public void onResume() {
        view.setUserNickTitle(appContext.getUser().getNick());
        setupNotification();

        User user = appContext.getUser();

        StatsMaper statsMaper = new StatsMaper(challengeService.challengesPerUser(user));

        statsMaper.amountChallenge()
                .subscribe(new SingleObserver<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(Long aLong) {
                        view.setAmountChallenges(aLong.intValue());
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });

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
        Context view = (Context) this.view;

        if (isChecked){
            if (!isServiceRunning(view)){
                view.startService(new Intent(view, MyIntentService.class));
            }
        }
    }

    private boolean isServiceRunning(Context view) {
        ActivityManager manager = (ActivityManager) view.getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.mmc.fifulec.MyIntentService".equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void switchNotificationAlarm(boolean isChecked) {
        Alarm alarm = new Alarm((Context) view);
        if (isChecked) {
            alarm.on(NotificationBroadcast.class, 1000L*60*5);
        } else {
            alarm.off(NotificationBroadcast.class);
        }
    }
}
