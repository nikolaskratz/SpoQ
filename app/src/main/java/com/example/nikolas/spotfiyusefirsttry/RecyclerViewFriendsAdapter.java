package com.example.nikolas.spotfiyusefirsttry;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

//import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerViewFriendsAdapter extends RecyclerView.Adapter<RecyclerViewFriendsAdapter.ViewHolder>{
    private static final String TAG = "RecyclerViewFriendsAdap";
    private HashMap<String,Friend> friends;
    private ArrayList<String> profilePictures;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private Iterator<Map.Entry<String, Friend>> iter;


    Bitmap profileBmp;
    View.OnClickListener onClickListener;


    public RecyclerViewFriendsAdapter(View.OnClickListener onClickListener) {
        //Log.d(TAG, "RecyclerViewFriendsAdapter: TEXT" + UserManager.getInstance().getUserInfo().getFriends().keySet().iterator().next());
        this.onClickListener = onClickListener;
        profilePictures = new ArrayList<>();
        this.friends = UserManager.getInstance().getUserInfo().getFriends();
        iter = friends.entrySet().iterator();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_list_item_friends, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {

        if(iter.hasNext()){
            Map.Entry<String, Friend> pair = (Map.Entry<String, Friend>) iter.next();
            Log.d(TAG, "onBindViewHolder:  " + pair.getKey() + " = " + pair.getValue() );
            viewHolder.userName.setText(pair.getKey());
        }

        viewHolder.elementLayout.setOnClickListener(onClickListener);
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    public class  ViewHolder extends RecyclerView.ViewHolder {

        //CircleImageView profilePicture;
        TextView userName;
        ConstraintLayout elementLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.recyclerFriends_userName);
            //profilePicture = itemView.findViewById(R.id.recyclerFriends_profilePicture);
            elementLayout = itemView.findViewById(R.id.recyclerFriends_layout);
        }
    }

    private Bitmap toBitmap (byte[] ProfileInByteArray ) {
        Bitmap bmp = BitmapFactory.decodeByteArray(ProfileInByteArray, 0, ProfileInByteArray.length);
        //ImageView image = (ImageView) findViewById(R.id.imageView1);

        //image.setImageBitmap(Bitmap.createScaledBitmap(bmp, image.getWidth(), image.getHeight(), false));
        return bmp;
    }

}
