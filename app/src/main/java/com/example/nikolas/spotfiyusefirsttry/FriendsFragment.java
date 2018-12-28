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

import java.util.ArrayList;


public class FriendsFragment extends Fragment implements View.OnClickListener, Observer {

    private static final String TAG = "FriendsFragment_debug";
    private RecyclerViewFriendsAdapter friendsListAdapter;
    private boolean updatedView = false;

    RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
                // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        UserManager.getInstance().register(this);

        return view;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
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
                        Log.d(TAG, "onEditorAction: " + searchEt.getText().toString());
                        UserManager.getInstance().getFriend(searchEt.getText().toString());
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