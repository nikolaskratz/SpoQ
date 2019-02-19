package com.example.nikolas.spotfiyusefirsttry;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class QuizLobbyActivity extends AppCompatActivity implements View.OnClickListener, Observer{

    private static final String TAG = "QuizLobbyActivity_debug";
    RecyclerView recyclerView;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private HashMap<String,Friend> friends;
    private  ArrayList keys;
    private boolean updatedView = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        UserManager.getInstance().register(this);
        this.friends = UserManager.getInstance().getUserInfo().getFriends();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_lobby);
        keys = new ArrayList<String>(friends.keySet());

        initRecyclerView();

    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        UserManager.getInstance().unregister(this);
    }

    private void initRecyclerView() {
        recyclerView = findViewById(R.id.lobby_recycler_view);
        RecyclerViewFriendsAdapter adapter = new RecyclerViewFriendsAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onClick(View v) {
        int itemPosition = recyclerView.getChildAdapterPosition(v);
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        String friend = keys.get(itemPosition).toString() ;

        Intent intent = new Intent(this, PlaylistSelect.class);


        FirebaseOperator.getInstance().readData(database.getReference(), new OnGetDataListener() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                for (DataSnapshot messageSnapshot: dataSnapshot.getChildren()) {
                    String userIdentity = (String) messageSnapshot.child(friend).getValue();
                    if(userIdentity != null) {
                        intent.putExtra("me", userID);
                        intent.putExtra("vs", userIdentity);
                        Log.d(TAG, "onSuccess: " + userID);
                        Log.d(TAG, "onSuccess: " + userIdentity);
                        startActivity(intent);
                    }
            }}

            @Override
            public void onStart() {

            }

            @Override
            public void onFailure() {

            }
        });

    }

    @Override
    public void update(boolean checked) {
        updatedView = checked;
        if(updatedView){
            updatedView = false;
            initRecyclerView();
        }
    }
}
