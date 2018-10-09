package mmc.com.fifulec;

import android.app.Application;

import mmc.com.fifulec.di.DaggerMainComponent;
import mmc.com.fifulec.di.MainComponent;
import mmc.com.fifulec.di.MainModule;

public class Fifulec extends Application {

    private static MainComponent daggerMainComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        daggerMainComponent = DaggerMainComponent.builder()
                .mainModule(new MainModule())
                .build();
    }
}
