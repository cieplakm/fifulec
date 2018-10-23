package com.mmc.fifulec.service;

import com.mmc.fifulec.di.AppScope;
import com.mmc.fifulec.model.Challenge;
import com.mmc.fifulec.model.ChallengeMapping;
import com.mmc.fifulec.repository.ChallengeMappingRepository;

import java.util.UUID;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@AppScope
public class ChallengeMappingService {

    private ChallengeMappingRepository challengeMappingRepository;

    @Inject
    public ChallengeMappingService(ChallengeMappingRepository challengeMappingRepository) {
        this.challengeMappingRepository = challengeMappingRepository;
    }

    public void create(Challenge challenge) {
        UUID uuid = UUID.randomUUID();

        ChallengeMapping mapping = ChallengeMapping.builder()
                .uuid(uuid.toString())
                .challengeUuid(challenge.getUuid())
                .fromUuid(challenge.getFromUserUuid())
                .toUuid(challenge.getToUserUuid())
                .build();

        challengeMappingRepository.mapping(challenge.getFromUserUuid(), mapping);
        challengeMappingRepository.mapping(challenge.getToUserUuid(), mapping);
    }

    public void delete(Challenge challenge){
        challengeMappingRepository.delete(challenge.getUuid(), challenge.getFromUserUuid(), challenge.getToUserUuid());
    }

    public Observable<ChallengeMapping> mapping4User(String userUuid){
        return challengeMappingRepository.maping(userUuid);
    }

    public Observable<String> observeMappingChanges(String userUuid){
        return challengeMappingRepository.observeChanges(userUuid)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

}
