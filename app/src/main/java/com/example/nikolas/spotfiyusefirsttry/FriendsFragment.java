package com.example.nikolas.spotfiyusefirsttry;

import android.content.Context;
import android.graphics.Bitmap;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;


public class FriendsFragment extends Fragment implements  Observer {

    private static final String TAG = "FriendsFragment_debug";
    private String profileString;
    public RecyclerViewFriendsAdapter friendsListAdapter;
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
                    InputMethodManager in = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (in != null) {
                        in.hideSoftInputFromWindow(searchEt.getApplicationWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                    valueEt = searchEt.getText().toString();
                    if(valueEt.length() == 0) {
                        return false;
                    }

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

                                             FirebaseOperator.getInstance().readData(database.getReference().child("Users")
                                                     .child(queryResult.toString()).child("profileImg"), new OnGetDataListener() {
                                                 @Override
                                                 public void onSuccess(DataSnapshot dataSnapshot) {

                                                     profileString = dataSnapshot.getValue().toString();

                                                     UserManager.getInstance().writeNewFriend(database.getReference().
                                                             child("Users").child(UserManager.getInstance().getCurrentUid().getUid()).
                                                             child("friends").child(valueEt), valueEt, queryResult.toString(), profileString);
                                                     //clear edit text after adding a friend
                                                     searchEt.getText().clear();
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

    public void initRecyclerView() {
        recyclerView = getView().findViewById(R.id.friends_recycler_view);
        friendsListAdapter = new RecyclerViewFriendsAdapter();
        recyclerView.setAdapter(friendsListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //listener for delete button in recycler viewer
        friendsListAdapter.setOnItemDeleteListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //get the friends based on position in recycler viewer
                String[] friendsStringArray = UserManager.getInstance().getUserInfo().getFriends().keySet().toArray(new String[0]);

                String myUID = UserManager.getInstance().getUserID();

                //deleting user
                database.getReference().child("Users").child(myUID).child("friends").child(friendsStringArray[position]).removeValue();
            }
        });

        friendsListAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.d(TAG, "onItemClick -> clicked pos:" + position);
                if(RecyclerViewFriendsAdapter.changeViewType(position)) {
                    friendsListAdapter.notifyItemChanged(position);
                }
            }
        });
     }

    @Override
    public void update(boolean checked) {
        updatedView = checked;
        if(updatedView && getView() != null){
            updatedView = false;
            initRecyclerView();
        }
    }

    private boolean haveFriend(String nickname) {
        return UserManager.getInstance().userInfo.getFriends().keySet().contains(nickname);
    }

}