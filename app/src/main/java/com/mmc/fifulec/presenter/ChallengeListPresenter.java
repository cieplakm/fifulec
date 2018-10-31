package com.mmc.fifulec.presenter;

import android.content.Context;

import com.mmc.fifulec.Notification;
import com.mmc.fifulec.contract.ChallengeListContract;
import com.mmc.fifulec.di.AppScope;
import com.mmc.fifulec.model.Challenge;
import com.mmc.fifulec.model.ChallengeStatus;
import com.mmc.fifulec.model.OnChallengeClickedListener;
import com.mmc.fifulec.model.OnChallengeConfirm;
import com.mmc.fifulec.model.OnUserClickedListener;
import com.mmc.fifulec.model.OpponentSelected;
import com.mmc.fifulec.model.User;
import com.mmc.fifulec.repository.UserRepository;
import com.mmc.fifulec.service.ChallengeService;
import com.mmc.fifulec.utils.AppContext;
import com.mmc.fifulec.utils.Preferences;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.SingleSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

@AppScope
public class ChallengeListPresenter {
    private ChallengeListContract.View view;

    private final ChallengeService challengeService;

    private final AppContext appContext;
    private Preferences preferences;
    private UserRepository userRepository;

    private Observer<List<Challenge>> forUpdateObserver;


    @Inject
    public ChallengeListPresenter(ChallengeService challengeService,
                                  AppContext appContext,
                                  Preferences preferences,
                                  UserRepository userRepository) {
        this.challengeService = challengeService;
        this.appContext = appContext;
        this.preferences = preferences;
        this.userRepository = userRepository;
    }

    public void onCreate(final ChallengeListContract.View view) {
        this.view = view;

        view.setOnChallenge4MeClickListener(new OnChallengeClickedListener() {
            @Override
            public void onChallengeSelect(Challenge challenge) {
                onChallengeClickedAction(challenge);
            }
        });

        forUpdateObserver = new ForUpdateObserver();

        Observable.just(appContext.getUser() != null)
                .flatMap(new Function<Boolean, ObservableSource<User>>() {
                    @Override
                    public ObservableSource<User> apply(Boolean isUserInAppContext) {
                        if (isUserInAppContext) {
                            return Observable.just(appContext.getUser());
                        } else {
                            return userRepository.userByUuidObservable(preferences.getUuid());
                        }
                    }
                })
                .flatMap(new Function<User, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(User user) {
                        return challengeService.observeChallengeChanges(user);
                    }
                })
                .flatMap(new Function<String, ObservableSource<Challenge>>() {
                    @Override
                    public ObservableSource<Challenge> apply(String challengeId) {
                        return challengeService.challengePerUuid(challengeId)
                                .filter(new Predicate<Challenge>() {
                                    @Override
                                    public boolean test(Challenge challenge) {
                                        ChallengeStatus challengeStatus = challenge.getChallengeStatus();
                                        return challengeStatus != ChallengeStatus.FINISHED &&
                                                challengeStatus != ChallengeStatus.REJECTED;
                                    }
                                });
                    }
                })
                .map(new Function<Challenge, Challenge>() {
                    @Override
                    public Challenge apply(Challenge challenge) throws Exception {
                        return challenge;
                    }
                })
                .toList()
                .toObservable()
                .subscribe(forUpdateObserver);
    }

    public void onAddChallengeClicked() {
        appContext.setOnUserClickedListener(new OnUserClickedListener() {
            @Override
            public void onUserSelect(final OpponentSelected opponentSelected) {
                Observable.merge(challengeService.getNotAcceptedChallenge(appContext.getUser(), opponentSelected.getUser()),
                        challengeService.getNotAcceptedChallenge(opponentSelected.getUser(), appContext.getUser()))
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

    public void onAcceptedClicked(Challenge challenge) {
        challengeService.acceptChallenge(challenge);
        new Notification((Context) view).cancel();
    }

    public void onRejectClicked(Challenge challenge) {
        rejectChallenge(challenge);
    }

    public void onCancelClicked(Challenge challenge) {
        challengeService.delete(challenge);
    }

    private void onChallengeClickedAction(Challenge challenge) {
        switch (challenge.getChallengeStatus()) {
            case ACCEPTED:
                if (appContext.getUser().getUuid().equals(challenge.getFromUserUuid())) {
                    resolveChallenge(challenge);
                }
                break;
            case NOT_ACCEPTED:
                if (appContext.getUser().getUuid().equals(challenge.getToUserUuid())) {
                    showQuestionAboutAcceptance(challenge);
                } else {
                    showQuestionCancelAcceptance(challenge);
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
    }

    private void showQuestionCancelAcceptance(Challenge challenge) {
        view.showDaialogToCancelChallenge(challenge);
    }

    private void rejectChallenge(Challenge challenge) {
        challengeService.rejectChallenge(challenge);
    }

    private void confirm(final Challenge challenge) {
        view.showConfirmDialog(new OnChallengeConfirm() {
            @Override
            public void confirm() {
                challengeService.confirmChallenge(challenge);
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

    private class ForUpdateObserver implements Observer<List<Challenge>> {

        @Override
        public void onSubscribe(Disposable d) {

        }

        @Override
        public void onNext(List<Challenge> challenges) {
            view.setChallenges4Me(challenges);
        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onComplete() {

        }
    }
}
