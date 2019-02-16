package com.example.nikolas.spotfiyusefirsttry;

import android.graphics.Bitmap;
import android.util.Log;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

public class Friend {

    private static final String TAG = "FriendDebug";
    private String userID;
    private String nickname;
    private Bitmap ProfilePicture;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    public Friend(String userID, String nickname) {
        this.userID = userID;
        this.nickname = nickname;

        Log.d(TAG, "Constructor called");
    }
    private void assignProfilePicture() {
        FirebaseOperator.getInstance().readData(database.getReference().child("Users").child(userID).child("profileImg"), new OnGetDataListener() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                if( dataSnapshot.getValue() != null){
                    Log.d(TAG, "RecyclerViewFriendsAdapter: 2 " + dataSnapshot.getValue());
                    //profilePictures.add(dataSnapshot.getValue().toString());
                }
            }

            @Override
            public void onStart() {

            }

            @Override
            public void onFailure() {

            }
        });
    }

    public Friend(){
    }

    public String getUserID() {
        return userID;
    }

    public String getNickname() {
        return nickname;
    }
}
