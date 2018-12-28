package com.example.nikolas.spotfiyusefirsttry;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.Preference;
import android.preference.PreferenceManager;
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
    SharedPreferences sharedPref ;
    //private UserInfo userInfo;
    String jsonInString;
    Gson gson = new Gson();
    UserInfo userInfo;
    RecyclerViewFriendsAdapter adapterA;

    FirebaseAuth userAuth = FirebaseAuth.getInstance();
    String userID = userAuth.getCurrentUser().getUid();
    DatabaseReference myRef = database.getReference("Users").child(userID);


    public void getAdapter(RecyclerViewFriendsAdapter adapter) {
        adapterA = adapter;
    }

    public static UserManager getInstance() {
        return ourInstance;
    }

    private UserManager() {
        databaseQuery();
    }

    // returns all information about currently logged in user
    public void databaseQuery() {
        Log.d(TAG, userID);

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userInfo = dataSnapshot.getValue(UserInfo.class);
                Log.d(TAG, "onDataChange: CHANGHE!");
                 //jsonInString = gson.toJson(userInfo);
                //adapterA.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onDataChange: CANCEL!");
            }
        };
        myRef.addValueEventListener(postListener);

    }

    public UserInfo getUserInfo() {
        if(userInfo==null) Log.d(TAG, "userInfo null");
        return userInfo;
    }




    private void initUserInfoSharedPrefObject() {
       // jsonInString = gson.toJson(userInfo);
        SharedPreferences sharedPref = getSharedPreferences("PREFERENCE_1", Context.MODE_PRIVATE);
        sharedPref.edit().putString("UserInfoObjectJson", jsonInString).apply();
        Log.d(TAG, "initUserInfoSharedPrefObject: OK");

        /*SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("abc", Context.MODE_PRIVATE);
        String msg = sharedPref.getString("abc","x");*/
}


    private class MyAsync extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... strings) {

                // listening for changes in db
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            return null;
        }
    }

    public String getUserID() {
        return userID;
    }
}
