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
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerViewFriendsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static final String TAG = "RecyclerViewFriendsAdap";
    private HashMap<String,Friend> friends;
    private ArrayList<String> profilePictures;
    private Iterator<Map.Entry<String, Friend>> iter;
    private Bitmap bmp;
    private static ArrayList<Boolean> viewStatusList;
    private View.OnClickListener onClickListener;
    private OnItemClickListener changeViewListener;
    private OnItemClickListener deleteListener;

    //interface will forward our click from adapter to our main activity
    public void setOnItemClickListener(OnItemClickListener listener) {
        changeViewListener = listener;
    }

    public void setOnItemDeleteListener(OnItemClickListener listener) {
        deleteListener = listener;
    }
    // logic for changing view type
    public static Boolean changeViewType(int pos) {

        Boolean item = viewStatusList.get(pos);

        if (item) {
            viewStatusList.set(pos, false);
            return true;
        }
        else {
            //search for already detailed views
            for (int i = 0; i < viewStatusList.size(); i++) {

                if (viewStatusList.get(i)) {
                    viewStatusList.set(i,false);
                    viewStatusList.set(pos,true);
                    return true;
                }
            }
            viewStatusList.set(pos, true);
            return true;
        }
    }

    public RecyclerViewFriendsAdapter() {

        //Log.d(TAG, "RecyclerViewFriendsAdapter: TEXT" + UserManager.getInstance().getUserInfo().getFriends().keySet().iterator().next());

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
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        if (i == 2) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_list_item_friends_details, viewGroup, false);
            return new ViewHolderDetailed(view, deleteListener);

        } else {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_list_item_friends, viewGroup, false);
            return new ViewHolder(view, changeViewListener);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {

        // fill defaultView with data -> iterating through the fiends hashmap
        if (viewHolder.getItemViewType() == 1) {
            ViewHolder viewHolderDefault = (ViewHolder) viewHolder;

            if (iter.hasNext()) {
                Map.Entry<String, Friend> pair = iter.next();
                viewHolderDefault.userName.setText(pair.getKey());

                byte[] byteArray = Base64.decode(friends.get(pair.getKey()).getProfilePicture(), 0);
                bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

                viewHolderDefault.profilePicture.setImageBitmap(bmp);
            }
        }
        //fill detailed view with data -> after choosing a friend, showing respective details

        else {

            //get the detailed View position
            int position = 0;
            for (Boolean element: viewStatusList)
            {
                if(element == Boolean.TRUE){
                    break;
                }
                else{
                    position++;
                }
            }

            //user from friends list
            Object key =null;
            int count = 0;
            for(Object key1 : friends.keySet())
            {
                if(count == position)
                  key=key1;
                count++;
            }

            Log.d(TAG, "onBindViewHolder: " + key);

            ViewHolderDetailed viewHolderDetailed = (ViewHolderDetailed) viewHolder;

            viewHolderDetailed.userNameDet.setText(friends.get(key).getNickname());

            byte[] byteArray = Base64.decode(friends.get(key).getProfilePicture(),0);
            bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            viewHolderDetailed.profilePictureDet.setImageBitmap(bmp);
        }
    }

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

    // ViewHolder for default view
    public class ViewHolder extends RecyclerView.ViewHolder  {

        CircleImageView profilePicture;
        TextView userName;
        ConstraintLayout elementLayout;

        public ViewHolder(@NonNull View itemView, OnItemClickListener listener)  {
            super(itemView);
            userName = itemView.findViewById(R.id.recyclerFriends_userName);
            profilePicture = itemView.findViewById(R.id.recyclerFriends_profilePicture);
            elementLayout = itemView.findViewById(R.id.recyclerFriends_layout);
            elementLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(changeViewListener != null) {
                        int position =getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION) {
                            changeViewListener.onItemClick(v,position);
                        }
                    }
                }
            });
        }
    }
    // ViewHolder for detailed view
    public class ViewHolderDetailed extends RecyclerView.ViewHolder {

        CircleImageView profilePictureDet;
        TextView userNameDet;
        ConstraintLayout elementLayoutDet;
        ImageView deleteButton;

        public ViewHolderDetailed(@NonNull View itemView, OnItemClickListener deleteListener) {
            super(itemView);
            userNameDet = itemView.findViewById(R.id.recyclerFriends_userName_detailed);
            profilePictureDet = itemView.findViewById(R.id.recyclerFriends_profilePicture_detailed);
            elementLayoutDet = itemView.findViewById(R.id.recyclerFriends_layout_detailed);
            deleteButton = itemView.findViewById(R.id.recyclerFriends_delete_detailed);

            elementLayoutDet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(changeViewListener != null) {
                        int position =getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION) {
                            changeViewListener.onItemClick(v,position);
                        }
                    }
                }
            });

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(deleteListener != null) {
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION) {
                            deleteListener.onItemClick(v,position);
                        }
                    }
                }
            });
        }
    }

}
