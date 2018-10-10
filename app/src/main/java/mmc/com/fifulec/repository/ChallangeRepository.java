package mmc.com.fifulec.repository;

import com.google.firebase.database.ValueEventListener;

import io.reactivex.Observable;
import mmc.com.fifulec.model.Challange;

public interface ChallangeRepository {

    void createChallange(Challange challange);
    void updateChallange(Challange challange);

    Observable<Challange> getChallangesFrom(String uuid, ValueEventListener listener);
}
