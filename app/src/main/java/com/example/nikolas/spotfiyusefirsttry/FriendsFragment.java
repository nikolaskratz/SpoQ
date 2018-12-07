package com.example.nikolas.spotfiyusefirsttry;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;



public class FriendsFragment extends Fragment {

    private static final String TAG = "FriendsFragment_debug";

    Gson gsonFriends = new Gson();
    ArrayList<Friend> friends = new ArrayList<>();
    private String currentPlayer="edo123";
    String friendsJSON;

    UserManager userManager = UserManager.getInstance();

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("Users").child(currentPlayer).child
            ("friends");


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friends, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        userManager.databaseQuerry();
        getFriends();
        initRecyclerView();

    }

    private void initRecyclerView() {
        RecyclerView recyclerView = getView().findViewById(R.id.friends_recycler_view);
        RecyclerViewFriendsAdapter adapter = new RecyclerViewFriendsAdapter(getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void getFriends() {
        friends=UserManager.getInstance().getUserInfo().getFriends();

//        ValueEventListener postListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                // Get Post object and use the values to update the UI
//                //Post post = dataSnapshot.getValue(Post.class);
//                String friendsJSON = gsonFriends.toJson(dataSnapshot.getValue());
//                Log.d(TAG, "onDataChange: " + friendsJSON);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                // Getting Post failed, log a message
//                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
//                // ...
//            }
//        };
//        myRef.addValueEventListener(postListener);
    }
}