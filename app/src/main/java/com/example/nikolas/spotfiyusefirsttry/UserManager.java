package com.example.nikolas.spotfiyusefirsttry;

import android.app.Application;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;

public class UserManager extends Application {
    private static final UserManager ourInstance = new UserManager();

    private static final String TAG = "UserManager_debug";

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private UserInfo userInfo;

    public static UserManager getInstance() {
        return ourInstance;
    }

    private UserManager() {
        databaseQuery();
    }



    // returns all information about currently logged in user
    public void databaseQuery() {
        FirebaseAuth userAuth = FirebaseAuth.getInstance();
        String userID = userAuth.getCurrentUser().getUid();
        DatabaseReference myRef = database.getReference("Users").child(userID);


        // listening for changes in db
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "Data is loaded" );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userInfo = dataSnapshot.getValue(UserInfo.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        myRef.addValueEventListener(postListener);

    }

    public UserInfo getUserInfo() {
        return userInfo;
    }
}
