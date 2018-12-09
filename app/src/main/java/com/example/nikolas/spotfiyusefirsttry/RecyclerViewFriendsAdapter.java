package com.example.nikolas.spotfiyusefirsttry;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class RecyclerViewFriendsAdapter extends RecyclerView.Adapter<RecyclerViewFriendsAdapter.ViewHolder>{
    private static final String TAG = "RecyclerViewFriendsAdap";
    private ArrayList<Friend> friends;
    private Context mContex;
    View.OnClickListener onClickListener;

    public RecyclerViewFriendsAdapter(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
        this.friends = UserManager.getInstance().getUserInfo().getFriends();
        this.mContex = mContex;
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
        Log.d(TAG, "onBindViewHolder: called.");

        viewHolder.userName.setText(friends.get(i).getNickname());

        viewHolder.elementLayout.setOnClickListener(onClickListener);
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    public class  ViewHolder extends RecyclerView.ViewHolder {

        TextView userName;
        ConstraintLayout elementLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.recyclerFriends_userName);
            elementLayout = itemView.findViewById(R.id.recyclerFriends_layout);

        }
    }
}
