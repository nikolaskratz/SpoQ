package com.example.nikolas.spotfiyusefirsttry;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
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

import de.hdodenhof.circleimageview.CircleImageView;

//import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerViewFriendsAdapter extends RecyclerView.Adapter<RecyclerViewFriendsAdapter.ViewHolder>{
    private static final String TAG = "RecyclerViewFriendsAdap";
    private HashMap<String,Friend> friends;
    private ArrayList<String> profilePictures;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private Iterator<Map.Entry<String, Friend>> iter;
    private Bitmap bmp;
    private static ArrayList<elementState> elementClickedState;
    private View.OnClickListener onClickListener;

    public enum elementState {
        DEFAULT,
        DETAILS
    }

    public static void modElementClickedState(int pos, elementState el) {

        //add change to iterator
        for (elementState elementState :elementClickedState){
            if(elementState == RecyclerViewFriendsAdapter.elementState.DETAILS)
                Log.d(TAG, "modElementClickedState: ok");
        }


        //check if this item is already detailed, if yes return to default
        if(elementClickedState.get(pos) == elementState.DETAILS)
            elementClickedState.set(pos, elementState.DEFAULT);
        else
            elementClickedState.set(pos, RecyclerViewFriendsAdapter.elementState.DETAILS);
        // first check if its already def, cannot be more detailed views than one

        Log.d(TAG, "RecyclerViewFriendsAdapter: " + elementClickedState);
    }




    public RecyclerViewFriendsAdapter(View.OnClickListener onClickListener) {

        //Log.d(TAG, "RecyclerViewFriendsAdapter: TEXT" + UserManager.getInstance().getUserInfo().getFriends().keySet().iterator().next());
        this.onClickListener = onClickListener;
        profilePictures = new ArrayList<>();
        this.friends = UserManager.getInstance().getUserInfo().getFriends();

        //set the iterator to read keys of the hashmap
        iter = friends.entrySet().iterator();

        elementClickedState = new ArrayList<>();
        for (int i = 0; i < friends.size(); i++) {
            elementClickedState.add(i, elementState.DEFAULT);
        }

        Log.d(TAG, "RecyclerViewFriendsAdapter: " + elementClickedState);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
//        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_list_item_friends, viewGroup, false);
//        ViewHolder holder = new ViewHolder(view);

        if (elementClickedState.get(i) == elementState.DETAILS ) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_list_item_friends_details, viewGroup, false);
            ViewHolder holder = new ViewHolder(view);

            return holder;

        } else {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_list_item_friends, viewGroup, false);
            ViewHolder holder = new ViewHolder(view);
            return holder;
        }


    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {

        if(iter.hasNext()){
            Map.Entry<String, Friend> pair = iter.next();
            viewHolder.userName.setText(pair.getKey());

            byte[] byteArray = Base64.decode(friends.get(pair.getKey()).getProfilePicture(), 0);
           // Log.d(TAG, "onSuccess: " + byteArray);

            bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            //Log.d(TAG, "onSuccess: " + bmp);

            viewHolder.profilePicture.setImageBitmap(bmp);
        }
        viewHolder.elementLayout.setOnClickListener(onClickListener);
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 1)return 1;
        else return 2;
        //return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    public class  ViewHolder extends RecyclerView.ViewHolder  {

        CircleImageView profilePicture;
        TextView userName;
        ConstraintLayout elementLayout;

        public ViewHolder(@NonNull View itemView)  {
            super(itemView);
            userName = itemView.findViewById(R.id.recyclerFriends_userName);
            profilePicture = itemView.findViewById(R.id.recyclerFriends_profilePicture);
            elementLayout = itemView.findViewById(R.id.recyclerFriends_layout);
        }


    }

}
