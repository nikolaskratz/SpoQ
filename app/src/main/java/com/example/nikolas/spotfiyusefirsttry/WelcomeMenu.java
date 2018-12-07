package com.example.nikolas.spotfiyusefirsttry;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class WelcomeMenu extends Fragment{


    private FirebaseAuth userAuth = FirebaseAuth.getInstance();
    private static final String TAG = "WelcomeMenu";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //userAuth.signOut();
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_welcome_menu, container, false);

        final Button signout = (Button) view.findViewById(R.id.signout_button);
        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userAuth.signOut();
                startActivity(new Intent(getActivity(), SignInActivity.class));
            }
        });
        return view;
    }



}
