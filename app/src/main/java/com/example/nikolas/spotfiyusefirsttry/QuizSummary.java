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
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class QuizSummary extends AppCompatActivity {

    String vs;
    String me;
    String quizID;
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
        quizID = me+"-"+vs+"-"+playlistID;

        setSummary();
        setButtons();
        if(!getIntent().getExtras().getBoolean("invite")){
            sendQuiz();
            sendInvitation();
            quizResult = new QuizResult(points);
        }
//        sendResult();
    }

    void setSummary(){
        int minutes = getIntent().getExtras().getInt("Minutes");
        int seconds = getIntent().getExtras().getInt("Seconds");
        int millis = getIntent().getExtras().getInt("Millis")%100;
        int correct = getIntent().getExtras().getInt("Correct Answers");
        int wrong = getIntent().getExtras().getInt("Wrong Answers");
        points = getIntent().getExtras().getInt("Points");

//        Log.e("abc132",""+points);

        TextView time = findViewById(R.id.time);
        time.setText(minutes+":"+seconds+":"+millis);

        TextView answers = findViewById(R.id.answerCount);
        answers.setText("Correct Answers: "+correct+"\nWrong Answers: "+wrong);

        TextView vs = findViewById(R.id.vs);
        vs.setText("You played vs: "+getIntent().getExtras().getString("vs"));

        TextView pointsView = findViewById(R.id.pointSummary);
        pointsView.setText(""+points);
    }

    void setButtons(){
        final Button playAgainButton = (Button) findViewById(R.id.playAgainButton);
        playAgainButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
               startActivity(new Intent(QuizSummary.this,PlayQuiz.class));
               finish();
            }
        });
        final Button differentPlaylist = (Button) findViewById(R.id.differentPlaylistButton);
        differentPlaylist.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(QuizSummary.this,PlaylistSelect.class));
                finish();
            }
        });
    }

    void sendQuiz(){
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Quiz");
        myRef.child(quizID).setValue(quizGame);
    }

    void sendInvitation(){
        Log.e("quizzing","sendInvite");

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Users").child(vs)
                .child("games");

        myRef.child(quizID).setValue(quizID);
        Log.e("quizzing",vs);

    }

    void sendResult(){
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Users").child(vs)
                .child("games");

        myRef.child(quizID).setValue(quizID);

    }
}
