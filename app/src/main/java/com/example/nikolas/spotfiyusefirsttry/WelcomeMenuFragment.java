package com.example.nikolas.spotfiyusefirsttry;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;


public class WelcomeMenuFragment extends Fragment implements View.OnClickListener {


    private FirebaseAuth userAuth = FirebaseAuth.getInstance();
    private static final String TAG = "WelcomeMenu";



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_welcome_menu, container, false);

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
}
