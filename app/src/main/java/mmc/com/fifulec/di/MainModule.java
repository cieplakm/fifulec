package mmc.com.fifulec.di;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.firebase.database.FirebaseDatabase;

import dagger.Module;
import dagger.Provides;
import mmc.com.fifulec.AppContext;
import mmc.com.fifulec.repository.ChallangeRepository;
import mmc.com.fifulec.repository.FirebaseChallangeRepository;
import mmc.com.fifulec.repository.FirebaseSecurityRepository;
import mmc.com.fifulec.repository.FirebaseUserRepository;
import mmc.com.fifulec.repository.SecurityRepository;
import mmc.com.fifulec.repository.UserRepository;

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
    public ChallangeRepository challangeRepository(FirebaseDatabase firebaseDatabase){
        return new FirebaseChallangeRepository(firebaseDatabase);
    }

}
