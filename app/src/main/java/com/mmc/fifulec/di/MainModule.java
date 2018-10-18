package com.mmc.fifulec.di;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.firebase.database.FirebaseDatabase;

import dagger.Module;
import dagger.Provides;

import com.mmc.fifulec.repository.ChallengeMappingRepository;
import com.mmc.fifulec.repository.ChallengeRepository;
import com.mmc.fifulec.repository.FirebaseChallengeRepository;
import com.mmc.fifulec.repository.FirebaseMappingRepository;
import com.mmc.fifulec.repository.FirebaseSecurityRepository;
import com.mmc.fifulec.repository.FirebaseUserRepository;
import com.mmc.fifulec.repository.SecurityRepository;
import com.mmc.fifulec.repository.UserRepository;

@Module
public class MainModule {
    private Application application;

    public MainModule(Application application) {
        this.application = application;
    }

    @Provides
    @AppScope
    public Application application(){
        return application;
    }

    @Provides
    @AppScope
    public SecurityRepository loginRepository(){
        return new FirebaseSecurityRepository();
    }

    @Provides
    @AppScope
    public UserRepository userRepository(){
        return new FirebaseUserRepository();
    }

    @Provides
    @AppScope
    public SharedPreferences sharedPreferences(Application application){
        return PreferenceManager.getDefaultSharedPreferences(application);
    }

    @Provides
    @AppScope
    public FirebaseDatabase firebaseDatabase(){
        return FirebaseDatabase.getInstance();
    }

    @Provides
    @AppScope
    public ChallengeRepository challangeRepository(FirebaseDatabase firebaseDatabase){
        return new FirebaseChallengeRepository(firebaseDatabase);
    }

    @Provides
    @AppScope
    public ChallengeMappingRepository challengeMappingRepository(FirebaseDatabase firebaseDatabase){
        return new FirebaseMappingRepository(firebaseDatabase);
    }

}
