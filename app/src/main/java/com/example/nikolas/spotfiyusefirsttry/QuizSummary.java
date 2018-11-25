package com.example.nikolas.spotfiyusefirsttry;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class QuizSummary extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_summary);
        setSummary();
    }

    void setSummary(){
        int minutes = getIntent().getExtras().getInt("Minutes");
        int seconds = getIntent().getExtras().getInt("Seconds");
        int millis = getIntent().getExtras().getInt("Millis")%100;
        int correct = getIntent().getExtras().getInt("Correct Answers");
        int wrong = getIntent().getExtras().getInt("Wrong Answers");

        TextView time = findViewById(R.id.time);
        time.setText(minutes+":"+seconds+":"+millis);

        TextView answers = findViewById(R.id.answerCount);
        answers.setText("Correct Answers: "+correct+"\nWrong Answers: "+wrong);
    }
}
