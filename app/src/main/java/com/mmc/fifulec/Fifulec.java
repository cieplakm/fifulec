package com.mmc.fifulec;

import android.app.Application;

import com.mmc.fifulec.di.DaggerMainComponent;
import com.mmc.fifulec.di.MainComponent;
import com.mmc.fifulec.di.MainModule;

public class Fifulec extends Application {

    private static MainComponent daggerMainComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        daggerMainComponent = DaggerMainComponent.builder()
                .mainModule(new MainModule(this))
                .build();
    }

    public static MainComponent component() {
        return daggerMainComponent;
    }
}
