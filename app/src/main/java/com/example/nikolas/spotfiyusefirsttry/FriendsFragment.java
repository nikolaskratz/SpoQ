package com.example.nikolas.spotfiyusefirsttry;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.util.Base64;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;


public class FriendsFragment extends Fragment implements View.OnClickListener, Observer {

    private static final String TAG = "FriendsFragment_debug";
    Bitmap bmp;
    String profileString;
    private RecyclerViewFriendsAdapter friendsListAdapter;
    private boolean updatedView = false;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private String valueEt;

    RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        // register observer
        UserManager.getInstance().register(this);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // unregister observer
        UserManager.getInstance().unregister(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // listener for search field
        EditText searchEt = (EditText) getView().findViewById(R.id.searchFriends_et);
        searchEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                     valueEt = searchEt.getText().toString();

                    FirebaseOperator.getInstance().readData(database.getReference().child("Identities").child(valueEt), new OnGetDataListener() {

                            @Override
                            public void onSuccess(DataSnapshot dataSnapshot) {

                                //queryResult stores userID of the user typed in the input box, null if the user doesn't exist
                               Object queryResult = dataSnapshot.getValue();

                                     if(queryResult != null) {
                                         if (UserManager.getInstance().getUserID().equals(queryResult))
                                             Log.d(TAG, "You can't add yourself.");

                                         else if (haveFriend(valueEt)) {
                                             Log.d(TAG, " You added already this friend.");
                                         } else {

                                             //getProfilePicture(queryResult.toString());
                                             //Log.d(TAG, "Bmp: " + profileString);

                                             FirebaseOperator.getInstance().readData(database.getReference().child("Users")
                                                     .child(queryResult.toString()).child("profileImg"), new OnGetDataListener() {
                                                 @Override
                                                 public void onSuccess(DataSnapshot dataSnapshot) {

                                                     profileString = dataSnapshot.getValue().toString();


                                                     UserManager.getInstance().writeNewFriend(database.getReference().
                                                             child("Users").child(UserManager.getInstance().getCurrentUid().getUid()).
                                                             child("friends").child(valueEt), valueEt, queryResult.toString(), profileString);

                                                 }

                                                 @Override
                                                 public void onStart() {

                                                 }

                                                 @Override
                                                 public void onFailure() {

                                                 }
                                             });


                                         }
                                     }
                            }
                            @Override
                            public void onStart() {
                                Log.d(TAG, "Started query");
                            }

                            @Override
                            public void onFailure() {
                                Log.d(TAG, "Failed query");
                            }
                        });
                    handled = true;
                }
                return handled;
            }
        });

        initRecyclerView();

    }

    private void getProfile() {

    }

    public void initRecyclerView() {
        recyclerView = getView().findViewById(R.id.friends_recycler_view);
        friendsListAdapter = new RecyclerViewFriendsAdapter(this);
        recyclerView.setAdapter(friendsListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
     }

    @Override
    public void onClick(View v) {
        // TODO: 09/12/2018 implement listeners for friends
    }

    @Override
    public void update(boolean checked) {
        updatedView = checked;
        if(updatedView && getView() != null){
            updatedView = false;
            initRecyclerView();
        }
    }

    // TODO: 13/2/19 after changing friend structure it has to be reimplemented
    // method checking if user has already friend in his list
    private boolean haveFriend(String nickname) {
//        for (Friend a : UserManager.getInstance().userInfo.getFriendsHash())) {
//            if(a.getNickname().equals(nickname))
//            return true;
//        }
        return false;
    }

    private void getProfilePicture(String userID) {

    }
}