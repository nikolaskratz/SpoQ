package com.example.nikolas.spotfiyusefirsttry;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class UserManager extends Application implements Subject {
    private static final UserManager ourInstance = new UserManager();

    private static final String TAG = "UserManager_debug";


    private List<Observer> observers;
    private boolean statusChanged;
    private boolean friendsListLoaded = false;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    SharedPreferences sharedPref ;
    //private UserInfo userInfo;
    String jsonInString;
    Gson gson = new Gson();

    UserInfo userInfo = new UserInfo();

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

    private UserManager(){
        observers = new ArrayList<>();
    }

    // returns all information about currently logged in user
    public void databaseQuery() {

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userInfo = dataSnapshot.getValue(UserInfo.class);
                notifyObservers();
                Log.d(TAG, "onDataChange: CHANGHE!");
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


    private void initUserInfoSharedPrefObject() {
        
       // jsonInString = gson.toJson(userInfo);
        SharedPreferences sharedPref = getSharedPreferences("PREFERENCE_1", Context.MODE_PRIVATE);
        sharedPref.edit().putString("UserInfoObjectJson", jsonInString).apply();
        Log.d(TAG, "initUserInfoSharedPrefObject: OK");

        /*SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("abc", Context.MODE_PRIVATE);
        String msg = sharedPref.getString("abc","x");*/
    }

    public boolean isFriendsListLoaded() {
        return friendsListLoaded;
    }

    public void setFriendsListLoaded(boolean friendsListLoaded) {
        this.friendsListLoaded = friendsListLoaded;
    }

    @Override
    public void register(final Observer observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
        Log.d(TAG, "register: " + observers.size());
    }
    @Override
    public void unregister(final Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        for (final Observer observer : observers) {
            observer.update(statusChanged = true);
        }
    }
}
