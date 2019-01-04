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

public class QuizLobbyActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "QuizLobbyActivity_debug";
    RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_lobby);

        initRecyclerView();
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

        //debug > this way you can gat user nickname in playlist select
        //String f = UserManager.getInstance().getUserInfo().getFriends().get(itemPosition).getNickname();

        Intent intent = new Intent(this, PlaylistSelect.class);
        intent.putExtra("againstUser", Integer.toString(itemPosition));
        startActivity(intent);
    }
}
