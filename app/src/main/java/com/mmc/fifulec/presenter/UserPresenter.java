package com.mmc.fifulec.presenter;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
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


import java.util.concurrent.TimeUnit;

import javax.inject.Inject;


import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

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


        challengeService.challengesPerUser(user).doOnComplete(new Action() {
            @Override
            public void run() throws Exception {
                updaeScore();
            }
        }).subscribe();

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
    }

}
