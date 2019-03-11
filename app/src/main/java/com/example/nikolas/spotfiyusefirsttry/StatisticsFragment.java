package com.example.nikolas.spotfiyusefirsttry;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;


public class StatisticsFragment extends Fragment {

    private static final String TAG = "stats_fragment" ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_statistics, container, false);

        //set user nickname
        TextView userNickname = (TextView) view.findViewById(R.id.nickname_stats);
        userNickname.setText(UserManager.getInstance().userInfo.getNickname());

        Log.d(TAG, "onCreateView: " + UserManager.getInstance().userInfo.getNickname());

        CircleImageView profilePicture = (CircleImageView) view.findViewById(R.id.profile_image_stats);

        //byte[] byteArray = Base64.decode(UserManager.getInstance().userInfo.getProfileImg(),0);
        //Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        //profilePicture.setImageBitmap(bmp);

        TextView totalPoints = (TextView) view.findViewById(R.id.total_points_stats);
        totalPoints.setText(String.valueOf(UserManager.getInstance().userInfo.getPoints()));
        Log.d(TAG, "onCreateView: " + UserManager.getInstance().userInfo.getPoints());

        return view;
    }

}
