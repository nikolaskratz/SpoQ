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
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

//import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerViewFriendsAdapter extends RecyclerView.Adapter<RecyclerViewFriendsAdapter.ViewHolder>{
    private static final String TAG = "RecyclerViewFriendsAdap";
    private HashMap<String,Friend> friends;
    private ArrayList<String> profilePictures;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();


    Bitmap profileBmp;
    View.OnClickListener onClickListener;

    public RecyclerViewFriendsAdapter(View.OnClickListener onClickListener) {
        Log.d(TAG, "RecyclerViewFriendsAdapter: TEXT" + UserManager.getInstance().getUserInfo().getFriends());
        this.onClickListener = onClickListener;
        profilePictures = new ArrayList<>();
        this.friends = UserManager.getInstance().getUserInfo().getFriends();

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

        for (String picture:profilePictures) {
            Log.d(TAG, "Profiles:" + picture);
        }


        /*
         LinkedHashMap<String,String> linkedHashMap = new LinkedHashMap<String,String>();

        linkedHashMap.put("key0","value0");
        linkedHashMap.put("key1","value1");
        linkedHashMap.put("key2","value2");

        int pos = 1;
        String value = (new ArrayList<String>(linkedHashMap.values())).get(pos);
         */


        // change data structure to LinkedHashMap
        viewHolder.userName.setText(friends.get("Andreas").getNickname());

        //viewHolder.profilePicture.setImageBitmap(toBitmap(profilePictures.get(i).getBytes()));



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
