package com.example.nikolas.spotfiyusefirsttry;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuizSummary extends AppCompatActivity {

    String vs;
    String me;
    String quizID;
    String quizIDrev;
    QuizGame quizGame;
    int points;
    QuizResult quizResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_summary);
        quizGame = new Gson().fromJson(getIntent().getExtras().getString("Quiz"), QuizGame
                .class);

        String playlistID= quizGame.getQuizList().get(0).getPlaylistID();
        me = getIntent().getExtras().getString("me");
        vs = getIntent().getExtras().getString("vs");
        points = getIntent().getExtras().getInt("Points");
        quizID = me+"-"+vs+"-"+playlistID;
        quizIDrev = vs+"-"+me+"-"+playlistID;
        getVsName();
        setStats();
        setButtons();
        if(!getIntent().getExtras().getBoolean("invite")){
            quizResult = new QuizResult(points,quizID,me,vs);
            quizResult.setPointsP2(310);
            sendQuiz();
            sendInvitation();
        } else {
            sendSecondResult();
            removeInvite();
        }
    }

    //save totalPoints & increase totalGamesPlayed
    public void setStats(){
        DatabaseReference writeRef = FirebaseDatabase.getInstance().getReference("Users").child(me).child("Stats");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users").child(me).child("Stats");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snap) {
                Object v = snap.child("totalPoints").getValue();
                if(v!=null){
                    Long totalPoints = (Long) snap.child("totalPoints").getValue()+points;
                    Long totalGames = (Long) snap.child("totalGames").getValue()+1;
                    writeRef.child("totalPoints").setValue(totalPoints);
                    writeRef.child("totalGames").setValue(totalGames);
                } else {
                    writeRef.child("totalPoints").setValue(points);
                    writeRef.child("totalGames").setValue(1);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("myTag", "Failed to read value.");
            }
        });
    }

    public void getVsName () {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("IdentitiesREV");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snap) {
                String vs;
                String vsID =getIntent().getExtras().getString("vs");
                vs= (String) snap.child(vsID).getValue();

                setSummary(vs);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("myTag", "Failed to read value.");
            }
        });
    }

    void setSummary(String vs){
        int minutes = getIntent().getExtras().getInt("Minutes");
        int seconds = getIntent().getExtras().getInt("Seconds");
        int millis = getIntent().getExtras().getInt("Millis")%100;
        int correct = getIntent().getExtras().getInt("Correct Answers");
        int wrong = getIntent().getExtras().getInt("Wrong Answers");

        TextView time = findViewById(R.id.time);
        time.setText(minutes+":"+seconds+":"+millis);

        TextView answers = findViewById(R.id.answerCount);
        answers.setText("Correct Answers: "+correct+"\nWrong Answers: "+wrong);

        TextView vsView = findViewById(R.id.vsInfo);
        vsView.setText(vs);

        TextView pointsView = findViewById(R.id.pointSummary);
        pointsView.setText(""+points);
    }

    void setButtons(){
        final Button differentPlaylist = (Button) findViewById(R.id.homeButton);
        differentPlaylist.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(QuizSummary.this,MainAppActivity.class));
                finish();
            }
        });
    }

    void sendQuiz(){
        quizGame.setQuizResult(quizResult);
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Quiz");
        myRef.child(quizID).setValue(quizGame);

        DatabaseReference myRef2 = FirebaseDatabase.getInstance().getReference("Users").child(me);
        myRef2.child("results").child(quizID).setValue(quizResult);
        myRef2.child("results").child(quizID).child("timestamp").setValue(ServerValue.TIMESTAMP);
        myRef2.child("results").child(quizID).child("timestamp").setValue(ServerValue.TIMESTAMP);

    }

    void sendInvitation(){
        Log.e("quizzing","sendInvite");

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Users").child(vs)
                .child("games");

        myRef.child(quizID).setValue(quizID);
        Log.e("quizzing",vs);

    }

    void sendSecondResult(){
        QuizResult quizResult=quizGame.getQuizResult();
        quizResult.setPointsP2(points);
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Quiz").child
                (quizIDrev);
        myRef.child("quizResult").setValue(quizResult);

        DatabaseReference myRef2 = FirebaseDatabase.getInstance().getReference("Users").child(me);
        myRef2.child("results").child(quizIDrev).setValue(quizResult);
        myRef2.child("results").child(quizIDrev).child("timestamp").setValue(ServerValue.TIMESTAMP);

        DatabaseReference myRef3 = FirebaseDatabase.getInstance().getReference("Users").child(vs);
        myRef3.child("results").child(quizIDrev).setValue(quizResult);
        myRef3.child("results").child(quizIDrev).child("timestamp").setValue(ServerValue.TIMESTAMP);
    }

    void removeInvite(){
        Log.e("removeInvite","removing, me: "+me+" quizID: "+quizIDrev);
        FirebaseDatabase.getInstance().getReference("Users").child(me)
                .child("games").child(quizIDrev).removeValue();

    }
}
