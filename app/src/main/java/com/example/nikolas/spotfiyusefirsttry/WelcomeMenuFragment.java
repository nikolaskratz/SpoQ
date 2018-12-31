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
        getResults();

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
        myRef.addValueEventListener(new ValueEventListener() {
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

                setUpRVInvite(inviteNames,iDs);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("myTag", "Failed to read value.");
            }
        });
    }

    public void setUpRVInvite(ArrayList<String> invites,ArrayList<String> iDs){
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

    public void getResults(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child
                ("games");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snap) {

                ArrayList<String> p1ID = new ArrayList<>();
                ArrayList<String> p2ID = new ArrayList<>();
                ArrayList<String> iDs = new ArrayList<>();
                Iterator i = snap.getChildren().iterator();
                while(i.hasNext()){
                    DataSnapshot d = (DataSnapshot) i.next();
                    String inv = (String) d.getValue();
                    iDs.add(inv);
                    String p1 = inv.split("-")[0];
                    p1ID.add(p1);
                    String p2 = inv.split("-")[1];
                    p2ID.add(p2);
                    Log.e("getResults", "p1: "+p1+"me: "+p2);
                    Log.e("getResults", "quizID "+inv);
                }
                getPlayernames(iDs,p1ID,p2ID);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("myTag", "Failed to read value.");
            }
        });
    }

    public void getPlayernames(ArrayList<String> iDs, ArrayList<String> p1ID, ArrayList<String>
            p2ID){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Identities");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snap) {
                ArrayList<String> p1Names = new ArrayList<>();
                ArrayList<String> p2Names = new ArrayList<>();
                for(String i : p1ID){
                    p1Names.add((String) snap.child(i).getValue());
                }
                for(String i : p2ID){
                    p2Names.add((String) snap.child(i).getValue());
                }
                getPoints(iDs,p1Names,p2Names);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("myTag", "Failed to read value.");
            }
        });
    }

    public void getPoints(ArrayList<String> iDs, ArrayList<String> p1Names, ArrayList<String>
            p2Names){

        ArrayList<Integer> pointsP1 = new ArrayList<>();
        ArrayList<Integer> pointsP2 = new ArrayList<>();
        for(String id : iDs) {
            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Quiz").child(id);
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snap) {
                    pointsP1.add(snap.getValue(QuizGame.class).getQuizResult().getPointsP1());
                    pointsP2.add(snap.getValue(QuizGame.class).getQuizResult().getPointsP2());
                    if(pointsP1.size()==iDs.size()){
                        setUpRVResult(pointsP1,pointsP2,p1Names,p2Names);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w("myTag", "Failed to read value.");
                }
            });
        }

    }

    public void setUpRVResult(ArrayList<Integer> pointsP1,ArrayList<Integer> pointsP2,
                              ArrayList<String> p1Names, ArrayList<String> p2Names){
        RecyclerView resultRecyclerView = (RecyclerView) view.findViewById(R.id.rvResults);

        resultRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        RecyclerViewResultAdapter recyclerViewResultAdapter = new
                RecyclerViewResultAdapter(pointsP1,pointsP2,p1Names,p2Names);
        resultRecyclerView.setAdapter(recyclerViewResultAdapter);

    }
}
