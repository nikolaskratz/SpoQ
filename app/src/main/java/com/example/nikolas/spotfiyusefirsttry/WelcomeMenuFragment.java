package com.example.nikolas.spotfiyusefirsttry;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

//import com.github.amlcurran.showcaseview.ShowcaseView;
//import com.github.amlcurran.showcaseview.SimpleShowcaseEventListener;
//import com.github.amlcurran.showcaseview.targets.ActionViewTarget;
//import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

import static android.content.Context.MODE_PRIVATE;


public class WelcomeMenuFragment extends Fragment implements View.OnClickListener {

    private FirebaseAuth userAuth = FirebaseAuth.getInstance();
    private static final String TAG = "WelcomeMenu";
    View view;
    private ArrayList<QuizResult> results;
    private ArrayList<String> p1IDs;
    private ArrayList<String> p2IDs;
    private String userID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_welcome_menu, container, false);


//        if(isFirstTime()){
           firstTimeAction();
//        }

        getInvites();
        getResults();

        view.findViewById(R.id.welcomeMenu_playWithFriend_bt).setOnClickListener(this);
        view.findViewById(R.id.signout_button).setOnClickListener(this);

        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        return view;
    }

//    private void firstTimeAction(){
//
//        Button button = (Button) view.findViewById(R.id.welcomeMenu_playWithFriend_bt);
//        TextView srt = (TextView) view.findViewById(R.id.swipeRightTarget);
//        TextView slt = (TextView) view.findViewById(R.id.swipeLeftTarget);
//
//        ViewTarget target = new ViewTarget(R.id.swipeRightTarget, getActivity());
//        ShowcaseView sv = new ShowcaseView.Builder(getActivity())
//                .setTarget(new ViewTarget(srt))
//                .setContentText("Swipe Right to View and Add Friends")
//                .setStyle(R.style.CustomShowCaseStyle)
//                .setShowcaseEventListener(
//                        new SimpleShowcaseEventListener() {
//                            @Override
//                            public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
//                                ShowcaseView sv1 = new ShowcaseView.Builder(getActivity())
//                                        .setTarget(new ViewTarget(slt))
//                                        .setContentTitle("Tutorial")
//                                        .setContentText("Swipe Left to see your Statistics")
//                                        .build();
//                            }
//                        }
//                )
//                .build();
////        sv.forceTextPosition(ShowcaseView.BELOW_SHOWCASE);
//    }

    private void firstTimeAction(){
        TextView srt = (TextView) view.findViewById(R.id.swipeRightTarget);
        TextView slt = (TextView) view.findViewById(R.id.swipeLeftTarget);

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(getActivity(), "");

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(getActivity())
                        .setTarget(srt)
                        .setDismissText("GOT IT")
                        .setShapePadding(120)
                        .setContentText("Swipe Right to View and Add Friends")
                        .setDelay(400)
                        .build()
        );
        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(getActivity())
                        .setTarget(slt)
                        .setDismissText("GOT IT")
                        .setShapePadding(120)
                        .setContentText("Swipe Left to see your Statistics")
                        .build()
        );
        sequence.start();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.welcomeMenu_playWithFriend_bt) {
            startActivity(new Intent(getActivity(), QuizLobbyActivity.class));
        } else if (i == R.id.signout_button) {
            userAuth.signOut();

            // this method must be always invoked when the user is signing out <-- to clear singleton
            UserManager.getInstance().clearInstance();
            startActivity(new Intent(getActivity(), SignInActivity.class));
        }

    }

    //checking if app is opend for the first time
    private boolean isFirstTime() {
        Log.e("firsttimetest","isfirsttime is checked");

        SharedPreferences preferences = getActivity().getPreferences(MODE_PRIVATE);
        boolean ranBefore = preferences.getBoolean("RanBefore", false);
        if (!ranBefore) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("RanBefore", true);
            editor.commit();
        }
        return !ranBefore;
    }

    public void getInvites() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child
                ("games");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snap) {

                ArrayList<String> invites = new ArrayList<>();
                ArrayList<String> iDs = new ArrayList<>();
                Iterator i = snap.getChildren().iterator();
                while (i.hasNext()) {
                    DataSnapshot d = (DataSnapshot) i.next();
                    String inv = (String) d.getValue();
                    iDs.add(inv);
                    String vs = inv.split("-")[0];
                    invites.add(vs);
                    Log.e("Iterating", "invite: " + inv);
                }
                getNicknames(invites, iDs);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("myTag", "Failed to read value.");
            }
        });
    }

    public void getNicknames(ArrayList<String> invites, ArrayList<String> iDs) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("IdentitiesREV");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snap) {
                ArrayList<String> inviteNames = new ArrayList<>();
                for (String i : invites) {
                    inviteNames.add((String) snap.child(i).getValue());
                }

                setUpRVInvite(inviteNames, iDs);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("myTag", "Failed to read value.");
            }
        });
    }

    public void setUpRVInvite(ArrayList<String> invites, ArrayList<String> iDs) {
        RecyclerView inviteRecyclerView = (RecyclerView) view.findViewById(R.id.rvInvites);

        inviteRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        RecyclerViewInvitationAdapter recyclerViewInvitationAdapter = new
                RecyclerViewInvitationAdapter(invites);
        inviteRecyclerView.setAdapter(recyclerViewInvitationAdapter);

        recyclerViewInvitationAdapter.setOnItemClickListener(new RecyclerViewInvitationAdapter
                .ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                if(!iDs.isEmpty()) {
                    String quizID = iDs.get(position);
                    String vs = quizID.split("-")[0];
                    Intent intent = new Intent(getActivity(), PlayQuiz.class);
                    intent.putExtra("vs", vs);
                    intent.putExtra("me", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    intent.putExtra("invite", true);
                    intent.putExtra("quizID", quizID);
                    startActivity(intent);
                }



            }
        });
    }

    public void getResults() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference myRef =
//                database.getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child
//                ("results");
        Query myRefQuery =
                database.getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child
                ("results").orderByChild("timestamp");
        myRefQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snap) {

                ArrayList<String> p1ID = new ArrayList<>();
                ArrayList<String> p2ID = new ArrayList<>();
                ArrayList<Integer> p1Points = new ArrayList<>();
                ArrayList<Integer> p2Points = new ArrayList<>();
                ArrayList<String> iDs = new ArrayList<>();
                Iterator i = snap.getChildren().iterator();
                while (i.hasNext()) {
                    DataSnapshot d = (DataSnapshot) i.next();
                    String quizID = d.getValue(QuizResult.class).getQuizID();
                    Integer p1P = d.getValue(QuizResult.class).getPointsP1();
                    Integer p2P = d.getValue(QuizResult.class).getPointsP2();
                    String p1 = quizID.split("-")[0];
                    String p2 = quizID.split("-")[1];

                    iDs.add(quizID);
                    p1Points.add(p1P);
                    p2Points.add(p2P);
                    p1ID.add(p1);
                    p2ID.add(p2);
                }

//                setUpRVResult(p1Points,p2Points,p1Names,p2Names);
                getPlayernames(iDs, p1ID, p2ID, p1Points, p2Points);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("myTag", "Failed to read value.");
            }
        });
    }

    public void getPlayernames (ArrayList<String> iDs, ArrayList<String> p1ID, ArrayList<String>
            p2ID, ArrayList<Integer> p1Points, ArrayList<Integer> p2Points) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("IdentitiesREV");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snap) {
                ArrayList<String> p1Names = new ArrayList<>();
                ArrayList<String> p2Names = new ArrayList<>();
                for (String i : p1ID) {
                    p1Names.add((String) snap.child(i).getValue());
                }
                for (String i : p2ID) {
                    p2Names.add((String) snap.child(i).getValue());
                }

                Collections.reverse(p1Points);
                Collections.reverse(p2Points);
                Collections.reverse(p1Names);
                Collections.reverse(p2Names);
                Collections.reverse(iDs);
                Collections.reverse(p1ID);
                Collections.reverse(p2ID);


                setUpRVResult(p1Points, p2Points, p1Names, p2Names,p1ID,p2ID);
                saveResults(p1Names, p2Names, p1ID, p2ID, p1Points, p2Points, iDs);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("myTag", "Failed to read value.");
            }
        });
    }


    private void saveResults(ArrayList<String> p1Names, ArrayList<String> p2Names, ArrayList<String> p1ID, ArrayList<String> p2ID, ArrayList<Integer> p1Points, ArrayList<Integer> p2Points, ArrayList<String> iDs) {
        results= new ArrayList<>();
        p1IDs= new ArrayList<>();
        p2IDs= new ArrayList<>();
        for(int i=0; i<p1Names.size(); i++){
            QuizResult result = new QuizResult(p1Points.get(i),iDs.get(i),p1Names.get(i),
                    p2Names.get(i));
            result.setPointsP2(p2Points.get(i));
            results.add(result);

            p1IDs=p1ID;
            p2IDs=p2ID;
        }

    }


    public void setUpRVResult(ArrayList<Integer> pointsP1,ArrayList<Integer> pointsP2,
                              ArrayList<String> p1Names, ArrayList<String> p2Names,
                              ArrayList<String> p1IDs, ArrayList<String> p2IDs){
        RecyclerView resultRecyclerView = (RecyclerView) view.findViewById(R.id.rvResults);

        resultRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        RecyclerViewResultAdapter recyclerViewResultAdapter = new
                RecyclerViewResultAdapter(pointsP1,pointsP2,p1Names,p2Names,p1IDs,p2IDs);
        resultRecyclerView.setAdapter(recyclerViewResultAdapter);

        recyclerViewResultAdapter.setOnItemClickListener(new RecyclerViewResultAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                String[] content = getContent(position);
                FragmentManager fm = getFragmentManager();
                QuizResultDialog quizResultDialog = QuizResultDialog.newInstance(content);
                quizResultDialog.show(fm, "title");
            }
        });

    }

    public String[] getContent (int position) {
        String[] content = new String[3];
        if (results.get(position).getPointsP2()==310) {
            content[0] = ""+results.get(position).getP2Name()+" hasn't played yet";
            content[1] = "You scored: "+results.get(position).getPointsP1()+" points";
            content[2] = ""+results.get(position).getP2Name()+" scored: ?? points";
        } else if(userID.equals(p1IDs.get(position))){
            content[0] = "You played vs. "+results.get(position).getP2Name();
            content[1] = "You scored: "+results.get(position).getPointsP1()+" points";
            content[2] = ""+results.get(position).getP2Name()+" scored: "+results.get(position).getPointsP2()+" points";
        } else {
            content[0] = "You played vs. "+results.get(position).getP1Name();
            content[1] = "You scored: "+results.get(position).getPointsP2()+" points";
            content[2] = results.get(position).getP1Name()+" scored: "+results.get(position).getPointsP1()+" points";
        }
        return content;
    }
}
