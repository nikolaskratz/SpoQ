package com.example.nikolas.spotfiyusefirsttry;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

        CircleImageView profilePicture = (CircleImageView) view.findViewById(R.id.profile_image_stats);

        //byte[] byteArray = Base64.decode(UserManager.getInstance().userInfo.getProfileImg(),0);
        //Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        //profilePicture.setImageBitmap(bmp);

//        TextView totalPoints = (TextView) view.findViewById(R.id.total_points_stats);
//        totalPoints.setText(String.valueOf(UserManager.getInstance().userInfo.getPoints()));
//        Log.d(TAG, "onCreateView: " + UserManager.getInstance().userInfo.getPoints());

        putStats(view);
        feedback(view);

        return view;
    }


    void feedback(View view){
        Button feedbackBtn = (Button) view.findViewById(R.id.feedback_bt_stats);
        feedbackBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Feedback");

                final EditText input = new EditText(getContext());
                builder.setView(input);

                builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendFeedback(input.getText().toString());
                    }
                });

                final AlertDialog dialog = builder.create();
                dialog.show();
                Button b = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                Drawable buttonDrwbl = getResources().getDrawable(R.drawable.feedback_dialog_button);
                b.setBackground(buttonDrwbl);
                b.setTextColor(getResources().getColor(R.color.colorPrimaryDark));

                int textViewId = dialog.getContext().getResources().getIdentifier("android:id/alertTitle", null, null);
                TextView tv = (TextView) dialog.findViewById(textViewId);
                tv.setTextColor(getResources().getColor(R.color.colorPrimaryDark));

            }
        });
    }

    void sendFeedback(String feedbackTxt){
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Feedback");
        myRef.child(userID).setValue(feedbackTxt);
    }

    void putStats(View view) {
        TextView totalPointsView = (TextView) view.findViewById(R.id.total_points_stats);
        TextView totalGamesView = (TextView) view.findViewById(R.id.total_games_stats);
        TextView averageGamesView = (TextView) view.findViewById(R.id.avg_points_stats);

        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        final long[] tP = {0};
        final long[] tG = {0};
        final long[] aP = {0};

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users").child(userID).child("Stats");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snap) {
                Object v = snap.child("totalPoints").getValue();
                if(v!=null) {
                    tP[0] = (Long) snap.child("totalPoints").getValue();
                    tG[0] = (Long) snap.child("totalGames").getValue();
                    if(tG[0]!=0){
                        aP[0] = tP[0] / tG[0];
                    } else aP[0]=0;

                    totalPointsView.setText(""+ tP[0]);
                    totalGamesView.setText(""+ tG[0]);
                    averageGamesView.setText(""+ aP[0]);
                } else {
                    totalPointsView.setText("0");
                    totalGamesView.setText("0");
                    averageGamesView.setText("0");
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("myTag", "Failed to read value.");
            }
        });
    }
}
