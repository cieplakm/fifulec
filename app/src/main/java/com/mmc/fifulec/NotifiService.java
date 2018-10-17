package com.mmc.fifulec;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import io.reactivex.MaybeObserver;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

import com.google.firebase.database.FirebaseDatabase;
import com.mmc.fifulec.model.Challenge;
import com.mmc.fifulec.model.User;
import com.mmc.fifulec.repository.ChallengeRepository;
import com.mmc.fifulec.repository.FirebaseChallengeRepository;
import com.mmc.fifulec.repository.FirebaseSecurityRepository;
import com.mmc.fifulec.repository.FirebaseUserRepository;
import com.mmc.fifulec.repository.SecurityRepository;
import com.mmc.fifulec.repository.UserRepository;
import com.mmc.fifulec.utils.AppContext;
import com.mmc.fifulec.utils.Preferences;

import javax.inject.Inject;

public class NotifiService extends IntentService {

    ChallengeRepository challengeRepository;
    Preferences preferences;
    SecurityRepository securityRepository;


    public NotifiService(String name) {
        super(name);

    }

    public NotifiService() {
        super("namefgsdfgsdfg");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("TESTYY", "START SERVICE!");

         return super.onStartCommand(intent, flags, startId);
    }





    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}
