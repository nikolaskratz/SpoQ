package com.example.nikolas.spotfiyusefirsttry;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class QuizLobbyActivity extends AppCompatActivity implements  Observer{

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
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
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
        RecyclerViewFriendsAdapter adapter = new RecyclerViewFriendsAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                int itemPosition = recyclerView.getChildAdapterPosition(view);
                String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                String friend = keys.get(itemPosition).toString() ;

                Intent intent = new Intent(getApplicationContext(), PlaylistSelectActivity.class);


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
