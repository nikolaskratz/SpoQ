package com.example.nikolas.spotfiyusefirsttry;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class Connection {

    private Quiz quiz;
    private int randomNumber;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("test");

    public Connection(Quiz quiz, int randomNumber) {

        this.quiz = quiz;
        this.randomNumber = randomNumber;
        sendData();
    }

    void sendData (){
        Log.e("connectionLog", "sendDataStart, database: "+database);
//        database.child("quiz_id").child("quiz3").child("question1").setValue
//                ("dddddd");
        myRef.setValue("aaaaaaaaaa");
        Log.e("connectionLog", "sendDataEnd, database: "+database);
    }
}
