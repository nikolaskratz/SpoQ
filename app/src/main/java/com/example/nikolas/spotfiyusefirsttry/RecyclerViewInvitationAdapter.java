package com.example.nikolas.spotfiyusefirsttry;

import android.support.v7.widget.RecyclerView;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
        import android.widget.TextView;

import java.util.ArrayList;

public class RecyclerViewInvitationAdapter extends RecyclerView.Adapter<RecyclerViewInvitationAdapter.ViewHolder> {
    private ArrayList<String> inviteData;
    private static ClickListener clickListener;

    public RecyclerViewInvitationAdapter(ArrayList<String> inviteData) {
        this.inviteData = inviteData;
    }

    @Override
    public RecyclerViewInvitationAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_result, null);
        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        if(inviteData.isEmpty()){
            viewHolder.vs.setText("You haven't been challenged yet!");
        } else viewHolder.vs.setText("You've been challenged by: "+inviteData.get(position)+"!");
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView vs;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            itemView.setOnClickListener(this);
//            inviteButton = (Button) itemLayoutView.findViewById(R.id.inviteButton);
//            inviteButton.setOnClickListener(this);
            vs = (TextView) itemLayoutView.findViewById(R.id.vs);
        }

        @Override
        public void onClick(View view) {
            clickListener.onItemClick(getAdapterPosition(), view);
        }

    }

    @Override
    public int getItemCount() {
        int itemCount= inviteData.size();
        if(itemCount>0) {
            return itemCount;
        } else return 1;
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        RecyclerViewInvitationAdapter.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
        //void onItemLongClick(int position, View v);
    }
}
