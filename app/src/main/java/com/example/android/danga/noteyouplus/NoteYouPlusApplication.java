package com.example.android.danga.noteyouplus;

import android.app.Application;

import com.firebase.client.Firebase;

/**
 * Created by An on 6/5/2016.
 */
public class NoteYouPlusApplication extends Application{

    private static NoteYouPlusApplication appManager;
    private final String LOG_TAG = NoteYouPlusApplication.class.getSimpleName();

    public NoteYouPlusApplication getAppManager(){
        return appManager;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
    }
}
