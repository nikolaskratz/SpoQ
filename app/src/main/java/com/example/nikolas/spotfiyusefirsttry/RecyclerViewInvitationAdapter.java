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

        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_invitations, null);

                ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.inviteButton.setText(inviteData.get(position));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public Button inviteButton;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            inviteButton = (Button) itemLayoutView.findViewById(R.id.inviteButton);
            inviteButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            clickListener.onItemClick(getAdapterPosition(), view);
        }

    }

    @Override
    public int getItemCount() {
        return inviteData.size();
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        RecyclerViewInvitationAdapter.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
        //void onItemLongClick(int position, View v);
    }
}
