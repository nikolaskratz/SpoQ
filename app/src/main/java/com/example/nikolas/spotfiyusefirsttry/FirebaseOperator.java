package com.example.nikolas.spotfiyusefirsttry;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class FirebaseOperator {
    private static final FirebaseOperator ourInstance = new FirebaseOperator();

    public static FirebaseOperator getInstance() {
        return ourInstance;
    }

    private FirebaseOperator() {
    }

    /**
     * Procedure which is fetching data under specific location.
     *
     * @param ref - reference to the Firebase, e.g. FirebaseDatabase.getInstance().getReference()
     * @param listener - data listener (Interface) e.g. new OnGetDataListener()
     */

    public void readData(DatabaseReference ref, final  OnGetDataListener listener) {
        listener.onStart();
        ref.addListenerForSingleValueEvent (new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listener.onSuccess(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onFailure();
            }
        });
    }

}
