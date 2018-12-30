package com.example.nikolas.spotfiyusefirsttry;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Iterator;


public class WelcomeMenuFragment extends Fragment implements View.OnClickListener {


    private FirebaseAuth userAuth = FirebaseAuth.getInstance();
    private static final String TAG = "WelcomeMenu";
    View view;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view =  inflater.inflate(R.layout.fragment_welcome_menu, container, false);

        getInvites();

        view.findViewById(R.id.welcomeMenu_playWithFriend_bt).setOnClickListener(this);
        view.findViewById(R.id.signout_button).setOnClickListener(this);
        view.findViewById(R.id.playQuizDebug).setOnClickListener(this);

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
    }

    public void getInvites(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child
                ("games");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snap) {

                ArrayList<String> invites = new ArrayList<>();
                ArrayList<String> iDs = new ArrayList<>();
                Iterator i = snap.getChildren().iterator();
                while(i.hasNext()){
                    DataSnapshot d = (DataSnapshot) i.next();
                    String inv = (String) d.getValue();
                    iDs.add(inv);
                    String vs = inv.split("-")[0];
                    invites.add(vs);
                    Log.e("Iterating", "invite: "+inv);
                }
                getNicknames(invites,iDs);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("myTag", "Failed to read value.");
            }
        });
    }

    public void getNicknames(ArrayList<String> invites,ArrayList<String> iDs){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("IdentitiesREV");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snap) {
                ArrayList<String> inviteNames = new ArrayList<>();
                for(String i : invites){
                    inviteNames.add((String) snap.child(i).getValue());
                }

                setUpRV(inviteNames,iDs);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("myTag", "Failed to read value.");
            }
        });
    }

    public void setUpRV(ArrayList<String> invites,ArrayList<String> iDs){
        RecyclerView inviteRecyclerView = (RecyclerView) view.findViewById(R.id.rvInvites);

        inviteRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        RecyclerViewInvitationAdapter recyclerViewInvitationAdapter = new
                RecyclerViewInvitationAdapter(invites);
        inviteRecyclerView.setAdapter(recyclerViewInvitationAdapter);

        recyclerViewInvitationAdapter.setOnItemClickListener(new RecyclerViewInvitationAdapter
                .ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                String quizID = iDs.get(position);
                String vs = quizID.split("-")[0];
                Log.d("click check", "onItemClick: " + invites.get(position));
                Log.d("click check", "onItemClick: " + quizID);
                Intent intent = new Intent(getActivity(), PlayQuiz.class);
                intent.putExtra("vs",vs);
                intent.putExtra("me",FirebaseAuth.getInstance().getCurrentUser().getUid());
                intent.putExtra("invite",true);
                intent.putExtra("quizID",quizID);
                startActivity(intent);


            }
        });
    }
}
