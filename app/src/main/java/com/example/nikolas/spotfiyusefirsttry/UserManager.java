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
import java.util.concurrent.CountDownLatch;

public class UserManager extends Application implements Subject {
    private static final UserManager ourInstance = new UserManager();

    private static final String TAG = "UserManager_debug";

    private List<Observer> observers;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    private boolean statusChanged;

    SharedPreferences sharedPref ;
    //private UserInfo userInfo;
    String jsonInString;


    UserInfo userInfo = new UserInfo();
    RecyclerViewFriendsAdapter adapterA;
    FirebaseAuth userAuth = FirebaseAuth.getInstance();
    String userID = userAuth.getCurrentUser().getUid();

    //reference to User object in firebase
    DatabaseReference myRef = database.getReference("Users").child(userID);


    private UserManager(){}

    public static UserManager getInstance() {
        return ourInstance;
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

    public void writeNewFriend(DatabaseReference ref, String userID , String nickname ) {
        ref.setValue(new Friend(nickname,userID));
    }

    // returns all information about currently logged in user
    public void databaseQuery() {
        observers = new ArrayList<>();

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userInfo = dataSnapshot.getValue(UserInfo.class);
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
