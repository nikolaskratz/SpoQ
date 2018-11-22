package com.example.nikolas.spotfiyusefirsttry;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class Connection {

    private Quiz quiz;
    private int randomNumber;

    DatabaseReference database = FirebaseDatabase.getInstance().getReference("question4");

    public Connection(Quiz quiz, int randomNumber) {
//        DatabaseReference database = FirebaseDatabase.getInstance().getReference("question4");
//        Log.e("connectionLog", "database: "+database);
        this.quiz = quiz;
        this.randomNumber = randomNumber;
//        sendData();
    }

//    void sendData (){
//        database.child("quiz_id").child("quiz3").child("question1").setValue
//                ("dddddd");
//    }
}
