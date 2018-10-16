package com.mmc.fifulec.presenter;

import android.support.annotation.Nullable;

import com.mmc.fifulec.contract.ChallengeListContract;
import com.mmc.fifulec.di.AppScope;
import com.mmc.fifulec.model.Challenge;
import com.mmc.fifulec.model.OnChallengeClickedListener;
import com.mmc.fifulec.model.OnChallengeConfirm;
import com.mmc.fifulec.model.OnUserClickedListener;
import com.mmc.fifulec.model.User;
import com.mmc.fifulec.service.ChallengeService;
import com.mmc.fifulec.utils.AppContext;

import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observer;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;


@AppScope
public class ChallengeListPresenter {

    private ChallengeListContract.View view;

    private final ChallengeService challengeService;
    private AppContext appContext;

    @Inject
    public ChallengeListPresenter(ChallengeService challengeService, AppContext appContext) {
        this.challengeService = challengeService;
        this.appContext = appContext;
    }

    public void onCreate(final ChallengeListContract.View view) {
        this.view = view;
        updateChallengesList();

        view.setOnChallenge4MeClickListener(new OnChallengeClickedListener() {
            @Override
            public void onChallengeSelect(Challenge challenge) {
                view.showToast("ch status: " + challenge.getChallengeStatus().toString() + "------ moj UUID:" + appContext.getUser().getUuid() + "-------- uuid from: " + challenge.getFromUserUuid());
                switch (challenge.getChallengeStatus()) {
                    case ACCEPTED:
                        if (appContext.getUser().getUuid().equals(challenge.getFromUserUuid())){
                            resolveChallenge(challenge);
                        }
                        break;
                    case NOT_ACCEPTED:
                        showQuestionAboutAcceptance(challenge);
                        break;
                    case FINISHED:
                        break;
                    case REJECTED:
                        break;
                    case NOT_CONFIRMED:
                        if (appContext.getUser().getUuid().equals(challenge.getToUserUuid())){
                            confirm(challenge);
                        }
                        break;
                }
                updateChallengesList();
            }
        });
    }

    private void rejectChallenge(Challenge challenge) {
        challengeService.rejectChallenge(challenge);
    }

    private void confirm(final Challenge challenge) {
        view.showConfirmDialog(new OnChallengeConfirm() {
            @Override
            public void confirm() {
                challengeService.confirmChallange(challenge);
            }

            @Override
            public void notConfirmed() {

            }
        });
    }

    private void showQuestionAboutAcceptance(Challenge challenge) {
        view.showDaialogToAcceptChallenge(challenge);
    }

    private void resolveChallenge(Challenge challenge) {
        appContext.setChallenge(challenge);
        view.openResolveActivity();
    }

    private void updateChallengesList() {
        challengeService.challengeFromUser(appContext.getUser())
                .toList()
                .subscribe(new SingleObserver<List<Challenge>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(List<Challenge> challenges) {
                        view.setChallenges4They(challenges);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });

        challengeService.challengeToUser(appContext.getUser())
                .sorted(new Comparator<Challenge>() {
                    @Override
                    public int compare(Challenge o1, Challenge o2) {
                        int x = getChallengeStatusWeight(o1);
                        int y = getChallengeStatusWeight(o2);

                        return x>y ? -1 : x==y ? 0 : 1;
                    }
                })
                .toList()
                .subscribe(new SingleObserver<List<Challenge>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(List<Challenge> challenges) {
                        view.setChallenges4Me(challenges);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }

    private int getChallengeStatusWeight(Challenge o1) {
        switch (o1.getChallengeStatus()){
            case NOT_CONFIRMED: return 5;
            case ACCEPTED: return 4;
            case NOT_ACCEPTED: return 3;
            case FINISHED: return 2;
            case REJECTED: return 1;
        }
        return 0;
    }

    public void onAddChallengeClicked() {
        appContext.setOnUserClickedListener(new OnUserClickedListener() {
            @Override
            public void onUserSelect(final User user) {

                challengeService.getNotAcceptedChallenge(appContext.getUser(), user)
                        .count()
                        .subscribe(new SingleObserver<Long>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onSuccess(Long aLong) {


                                if (aLong == null || aLong == 0){
                                    view.showToast("Utworzenie wyzwania - null");
                                    challengeService.createChallenge(appContext.getUser(), user);
                                    updateChallengesList();
                                }else {
                                    view.showToast("Jest już wyzwanie dla tego uzytkownika.");
                                }
                            }

                            @Override
                            public void onError(Throwable e) {

                            }
                        });
            }
        });
        view.showUsersList();

    }

    public void onChallengeAccepted(Challenge challenge) {
        challengeService.acceptChallenge(challenge);
    }

    public void onResume() {
        updateChallengesList();
    }

    public void onRejectClicked(Challenge challenge) {
        rejectChallenge(challenge);
    }

    public void onPause() {

    }
}
