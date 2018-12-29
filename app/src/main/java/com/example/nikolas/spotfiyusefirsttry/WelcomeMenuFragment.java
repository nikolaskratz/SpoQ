package com.example.nikolas.spotfiyusefirsttry;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class WelcomeMenuFragment extends Fragment implements View.OnClickListener {


    private FirebaseAuth userAuth = FirebaseAuth.getInstance();
    private static final String TAG = "WelcomeMenu";



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_welcome_menu, container, false);

        view.findViewById(R.id.welcomeMenu_playWithFriend_bt).setOnClickListener(this);
        view.findViewById(R.id.signout_button).setOnClickListener(this);
        view.findViewById(R.id.playQuizDebug).setOnClickListener(this);
        view.findViewById(R.id.joinQuizDebug).setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
            if(i == R.id.welcomeMenu_playWithFriend_bt) {
                startActivity( new Intent(getActivity(), QuizLobbyActivity.class));
            }
            else if (i == R.id.signout_button)  {
                userAuth.signOut();
                startActivity(new Intent(getActivity(), SignInActivity.class));
            }
            //DEBUG only (to reach the playlistselecter)
            else if (i == R.id.playQuizDebug){
                Intent intent = new Intent(getActivity(), PlaylistSelect.class);
                String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                intent.putExtra("me",userID);
                intent.putExtra("vs","Hc4Xpv88wYQWG3QoSqo0qjpov4r2");
                Log.d(TAG, "current user id: "+userID);
                startActivity(intent);
            }
            //DEBUG only (to join a quiz invite by A1; only as A2!)
            else if(i == R.id.joinQuizDebug){
                Log.d(TAG, "join");
                getQuizID();
            }
    }

    public void getQuizID(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child
                ("games");

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snap) {
                String quizID = snap.getChildren().iterator().next().getValue(String.class);
                String vs = quizID.split("-")[0];
                Intent intent = new Intent(getActivity(), PlayQuiz.class);
                intent.putExtra("vs",vs);
                intent.putExtra("me",FirebaseAuth.getInstance().getCurrentUser().getUid());
                intent.putExtra("invite",true);
                intent.putExtra("quizID",quizID);
                Log.d(TAG, "quizID: "+quizID);
                startActivity(intent);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("myTag", "Failed to read value.");
            }
        });
    }
}
