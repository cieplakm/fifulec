package com.mmc.fifulec.repository;

import com.mmc.fifulec.model.ChallengeMapping;

import io.reactivex.Observable;

public interface ChallengeMappingRepository {
    void mapping(String userUuid, ChallengeMapping mapping);

    Observable<ChallengeMapping> maping(String userUuid);
}
