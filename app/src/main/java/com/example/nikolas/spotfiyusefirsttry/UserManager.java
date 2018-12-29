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
    String userIdentity = null;

    private List<Observer> observers;
    private boolean statusChanged;

    private boolean friendsListLoaded = false;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    SharedPreferences sharedPref ;
    //private UserInfo userInfo;
    String jsonInString;
    String dbResponse;
    Gson gson = new Gson();
    ArrayList<Friend> friends5 = new ArrayList<>();

    UserInfo userInfo = new UserInfo();

    RecyclerViewFriendsAdapter adapterA;

    FirebaseAuth userAuth = FirebaseAuth.getInstance();
    String userID = userAuth.getCurrentUser().getUid();
    DatabaseReference myRef = database.getReference("Users").child(userID);
    DatabaseReference myRefIdentities = database.getReference();



    public void getAdapter(RecyclerViewFriendsAdapter adapter) {
        adapterA = adapter;
    }

    public static UserManager getInstance() {
        return ourInstance;
    }

    private UserManager(){}

    public Boolean getFriend(String nickname) {

        //myRefIdentities.child(nickname);

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot messageSnapshot: dataSnapshot.getChildren()) {
                    userIdentity = (String) messageSnapshot.child(nickname).getValue();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());

            }
        };
        myRefIdentities.addListenerForSingleValueEvent(postListener);

        Log.d(TAG, "found: " + userIdentity);

        if (userIdentity != null){
            Log.d(TAG, "found: " + userIdentity);
            return true;
        }
        else return false;

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
