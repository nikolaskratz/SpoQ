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

public class RecyclerViewFriendsAdapter extends RecyclerView.Adapter<RecyclerViewFriendsAdapter.ViewHolder>{
    private static final String TAG = "RecyclerViewFriendsAdap";
    private HashMap<String,Friend> friends;
    private ArrayList<String> profilePictures;
    private Iterator<Map.Entry<String, Friend>> iter;
    private Bitmap bmp;
    private static ArrayList<Boolean> viewStatusList;
    private View.OnClickListener onClickListener;

    // logic for changing view type
    public static Boolean changeViewType(int pos) {

        Boolean item = viewStatusList.get(pos);

        if (item) {
            viewStatusList.set(pos, false);
            return true;
        }
        else {
            //new implementation

            // check if  there is detailed view change it and to not and then change the clicked one

            //search for already detailed views
            for (int i = 0; i < viewStatusList.size(); i++) {

                if (viewStatusList.get(i)) {
                    return false;
                }
            }
            viewStatusList.set(pos, true);
            return true;
        }
    }

    public RecyclerViewFriendsAdapter(View.OnClickListener onClickListener) {

        //Log.d(TAG, "RecyclerViewFriendsAdapter: TEXT" + UserManager.getInstance().getUserInfo().getFriends().keySet().iterator().next());
        this.onClickListener = onClickListener;
        profilePictures = new ArrayList<>();
        this.friends = UserManager.getInstance().getUserInfo().getFriends();

        //set the iterator to read keys of the hashmap
        iter = friends.entrySet().iterator();

        viewStatusList = new ArrayList<>();
        for (int i = 0; i < friends.size(); i++) {
            viewStatusList.add(i, false);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        if (i == 2) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_list_item_friends_details, viewGroup, false);
            return new ViewHolder(view);

        } else {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_list_item_friends, viewGroup, false);
            return new ViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {


        if(viewHolder.getItemViewType() == 1){
            //Log.d(TAG, "normal: ");
        }

        else{
            //Log.d(TAG, "detailed");
        }


        // for view 1
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


    // this method sets view type to onBindViewHolder
    // for default view (false) set 2
    // for detailed view (true) set 1

    @Override
    public int getItemViewType(int position) {

        if(viewStatusList.get(position)){
            return 2;
        }
        else return 1;
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

    // ViewHolder for detailed view
    public class ViewHolderDetailed extends RecyclerView.ViewHolder {

        public ViewHolderDetailed(@NonNull View itemView) {
            super(itemView);
        }
    }

}
