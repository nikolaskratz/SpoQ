package com.example.nikolas.spotfiyusefirsttry;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
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
import java.util.concurrent.CountDownLatch;
public  class UserManager extends Application implements Subject {

    //private static final UserManager ourInstance = new UserManager();
    private static  UserManager ourInstance;

    private static final String TAG = "UserManager_debug";

    private List<Observer> observers;

    private boolean statusChanged;

    SharedPreferences sharedPref ;
    //private UserInfo userInfo;
    String jsonInString;

    UserInfo userInfo = new UserInfo();
    RecyclerViewFriendsAdapter adapterA;
    FirebaseAuth userAuth = FirebaseAuth.getInstance();

    private String userID = userAuth.getCurrentUser().getUid();

    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    //reference to User object in firebase
    DatabaseReference myRef = database.getReference("Users").child(userID);

    private UserManager(){}

    public String getUserID() {
        return userID;
    }

    public static UserManager getInstance(){
            if (ourInstance  == null) {
                ourInstance = new UserManager();
            }
        return ourInstance;
    }

    public void clearInstance() {
        ourInstance = null;
    }

    public void getAdapter(RecyclerViewFriendsAdapter adapter) {
        adapterA = adapter;
    }

    public void readData(DatabaseReference ref, final  OnGetDataListener listener) {
        listener.onStart();
        ref.addListenerForSingleValueEvent (new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listener.onSuccess(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onFailure();
            }
        });
    }

    public void writeNewFriend(DatabaseReference ref, String userID , String nickname, String profile ) {
        ref.setValue(new Friend(nickname,userID,profile));
    }

    // returns all information about currently logged in user
    public void databaseQuery() {
        observers = new ArrayList<>();

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userInfo = dataSnapshot.getValue(UserInfo.class);
                 Log.d(TAG, "onDataChange: "+ userInfo.getFriends());
                notifyObservers();
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

    public FirebaseAuth getCurrentUid() {
        return userAuth;
    }

    // TODO: 28/12/18 IMPLEMENT SHARED PREFS
    private void initUserInfoSharedPrefObject() {

        // jsonInString = gson.toJson(userInfo);
        SharedPreferences sharedPref = getSharedPreferences("PREFERENCE_1", Context.MODE_PRIVATE);
        sharedPref.edit().putString("UserInfoObjectJson", jsonInString).apply();
        Log.d(TAG, "initUserInfoSharedPrefObject: OK");

        /*SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("abc", Context.MODE_PRIVATE);
        String msg = sharedPref.getString("abc","x");*/
    }

    // observer-subject methods
    @Override
    public void register(final Observer observer) {
        //creating list of observers to track
        //check if this array list can be initiated here

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