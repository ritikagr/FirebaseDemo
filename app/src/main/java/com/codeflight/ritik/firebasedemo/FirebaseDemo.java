package com.codeflight.ritik.firebasedemo;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by ritik on 1/18/2017.
 */

public class FirebaseDemo extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
