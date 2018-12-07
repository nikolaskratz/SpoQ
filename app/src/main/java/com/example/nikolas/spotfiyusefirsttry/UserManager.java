package com.example.nikolas.spotfiyusefirsttry;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;

public class UserManager {
    private static final UserManager ourInstance = new UserManager();


    private static final String TAG = "UserManager_debug";

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
//    private FirebaseAuth userAuth = FirebaseAuth.getInstance();

    private String userNickname;
    private String userProfilePicture;
    private Integer userPoint;
    private ArrayList<String> usersFriendsList = new ArrayList<>();
    private Gson gsonOBJ = new Gson();

    UserInfo userInfo;

//    private final String userID = userAuth.getCurrentUser().getUid();

    public static UserManager getInstance() {
        return ourInstance;
    }

    private UserManager() {
        databaseQuerry();
    }

//    public String getUserNickname() {
//        return userNickname;
//    }
//
//    public String getUserProfilePicture() {
//        return userProfilePicture;
//    }
//
//    public Integer getUserPoint() {
//        return userPoint;
//    }
//
//    public ArrayList<String> getUsersFriendsList() {
//        return usersFriendsList;
//    }

    // returns all information about currently logged in user

    public void databaseQuerry() {
        FirebaseAuth userAuth = FirebaseAuth.getInstance();
        String userID = userAuth.getCurrentUser().getUid();
        DatabaseReference myRef = database.getReference("Users").child(userID);
        Log.e("getting friends","userID: "+userID);

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


//        ValueEventListener postListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                userInfo = dataSnapshot.getValue(UserInfo.class);
//
//                userNickname = userInfo.getNickname();
//                userProfilePicture = userInfo.getProfileImg();
//                // TODO: 04/12/2018 Add points
//                Log.d(TAG, "onDataChange: " + userInfo.getNickname());
//            }
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                // Getting Post failed, log a message
//                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
//                // ...
//            }
//        };
//        myRef.addValueEventListener(postListener);
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }
}
