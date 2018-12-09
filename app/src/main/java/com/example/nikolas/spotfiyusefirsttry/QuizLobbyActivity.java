package com.example.nikolas.spotfiyusefirsttry;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

public class QuizLobbyActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "QuizLobbyActivity_debug";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_lobby);

        initRecyclerView();
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.lobby_recycler_view);
        RecyclerViewFriendsAdapter adapter = new RecyclerViewFriendsAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onClick(View v) {
        // TODO: 09/12/2018 implement listeners for lobby
    }
}
