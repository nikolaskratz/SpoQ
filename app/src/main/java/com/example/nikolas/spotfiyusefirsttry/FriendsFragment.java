package com.example.nikolas.spotfiyusefirsttry;

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
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;


public class FriendsFragment extends Fragment implements View.OnClickListener, Observer {

    private static final String TAG = "FriendsFragment_debug";
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

    // TODO: 29/12/18 To fix: refresh friends list while switching account, prevent adding yoursel to the list

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
                        Log.d(TAG, "onEditorAction: " + valueEt);
                        UserManager.getInstance().readData(database.getReference(), new OnGetDataListener() {
                            @Override
                            public void onSuccess(DataSnapshot dataSnapshot) {
                                for (DataSnapshot messageSnapshot: dataSnapshot.getChildren()) {
                                    String userIdentity = (String) messageSnapshot.child(valueEt).getValue();
                                    if(userIdentity != null) {
                                        Log.d(TAG, "onSuccess: " + userIdentity);
                                        UserManager.getInstance().writeNewFriend(database.getReference().
                                                child("Users").child(UserManager.getInstance().getCurrentUid().getUid()).
                                                child("friends").child(String.valueOf(UserManager.getInstance().
                                                userInfo.getFriends().size())), valueEt, userIdentity);
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
}