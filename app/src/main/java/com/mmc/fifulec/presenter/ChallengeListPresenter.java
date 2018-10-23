package com.mmc.fifulec.presenter;

import com.mmc.fifulec.contract.ChallengeListContract;
import com.mmc.fifulec.di.AppScope;
import com.mmc.fifulec.model.Challenge;
import com.mmc.fifulec.model.ChallengeStatus;
import com.mmc.fifulec.model.OnChallengeClickedListener;
import com.mmc.fifulec.model.OnChallengeConfirm;
import com.mmc.fifulec.model.OnUserClickedListener;
import com.mmc.fifulec.model.OpponentSelected;
import com.mmc.fifulec.model.User;
import com.mmc.fifulec.service.ChallengeMappingService;
import com.mmc.fifulec.service.ChallengeService;
import com.mmc.fifulec.utils.AppContext;

import java.util.Comparator;
import java.util.List;
import java.util.Observable;

import javax.inject.Inject;

import io.reactivex.Observer;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Predicate;


@AppScope
public class ChallengeListPresenter {

    private ChallengeListContract.View view;

    private final ChallengeService challengeService;
    private ChallengeMappingService challengeMappingService;
    private AppContext appContext;

    @Inject
    public ChallengeListPresenter(ChallengeService challengeService, ChallengeMappingService challengeMappingService,  AppContext appContext) {
        this.challengeService = challengeService;
        this.challengeMappingService = challengeMappingService;
        this.appContext = appContext;
    }

    public void onCreate(final ChallengeListContract.View view) {
        this.view = view;
        updateChallengesList();

        view.setOnChallenge4MeClickListener(new OnChallengeClickedListener() {
            @Override
            public void onChallengeSelect(Challenge challenge) {
                switch (challenge.getChallengeStatus()) {
                    case ACCEPTED:
                        if (appContext.getUser().getUuid().equals(challenge.getFromUserUuid())) {
                            resolveChallenge(challenge);
                        }
                        break;
                    case NOT_ACCEPTED:
                        if (appContext.getUser().getUuid().equals(challenge.getToUserUuid())) {
                            showQuestionAboutAcceptance(challenge);
                        }
                        break;
                    case FINISHED:
                        break;
                    case REJECTED:
                        break;
                    case NOT_CONFIRMED:
                        if (appContext.getUser().getUuid().equals(challenge.getToUserUuid())) {
                            confirm(challenge);
                        } else {
                            if (appContext.getUser().getUuid().equals(challenge.getFromUserUuid())) {
                                resolveChallenge(challenge);
                            }
                        }
                        break;
                }
                updateChallengesList();
            }
        });


//        io.reactivex.Observable.merge(challengeMappingService.observeMappingChanges(appContext.getUser().getUuid()),
        challengeService.loockingForUpdate(appContext.getUser())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String s) {
                        updateChallengesList();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

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
                challengeService.confirmChallenge(challenge);
                updateChallengesList();
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
        challengeService.challengesPerUser(appContext.getUser())
                .sorted(new Comparator<Challenge>() {
                    @Override
                    public int compare(Challenge o1, Challenge o2) {
                        int x = ChallengeHelper.getChallengeStatusWeight(o1);
                        int y = ChallengeHelper.getChallengeStatusWeight(o2);
                        if (x != y) {
                            return Integer.compare(x, y);
                        } else {
                            return Long.compare(o1.getTimestamp(), o2.getTimestamp());
                        }
                    }
                })
                .filter(new Predicate<Challenge>() {
                    @Override
                    public boolean test(Challenge challenge) throws Exception {
                        ChallengeStatus challengeStatus = challenge.getChallengeStatus();
                        return challengeStatus != ChallengeStatus.FINISHED &&
                                challengeStatus != ChallengeStatus.REJECTED;
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

    public void onAddChallengeClicked() {
        appContext.setOnUserClickedListener(new OnUserClickedListener() {
            @Override
            public void onUserSelect(final OpponentSelected opponentSelected) {
                challengeService.getNotAcceptedChallenge(appContext.getUser(), opponentSelected.getUser())
                        .count()
                        .subscribe(new SingleObserver<Long>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                            }

                            @Override
                            public void onSuccess(Long aLong) {
                                if (aLong == null || aLong == 0) {
                                    view.showToast("Wyzwanie wysłane do " + opponentSelected.getUser().getNick());
                                    challengeService.createChallenge(appContext.getUser(), opponentSelected.getUser(), opponentSelected.isTwoLeggedTie());
                                    updateChallengesList();
                                } else {
                                    view.showToast("Istnieje nie zaakceptowane wyzwanie z " + opponentSelected.getUser().getNick());
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
        updateChallengesList();
    }

    public void onResume() {
        updateChallengesList();
    }

    public void onRejectClicked(Challenge challenge) {
        rejectChallenge(challenge);
        updateChallengesList();
    }

    public void onPause() {

    }

    public void onSwipe() {
        updateChallengesList();
    }
}
