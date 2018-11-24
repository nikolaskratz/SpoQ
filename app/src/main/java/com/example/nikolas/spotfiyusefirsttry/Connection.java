package com.example.nikolas.spotfiyusefirsttry;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class Connection {

    private Quiz quiz;
    private int randomNumber;


    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("quiz");

    public Connection(Quiz quiz, int randomNumber) {

        this.quiz = quiz;
        this.randomNumber = randomNumber;
        sendData();
    }

    void sendData (){
        Log.e("connectionLog", "sendDataStart, database: "+myRef);
        myRef.child("quiz_id").child("q1").setValue(quiz);
        myRef.child("quiz_id").child("q2").setValue(quiz);
        Log.e("connectionLog", "sendDataEnd, database: "+myRef);


    }
}
