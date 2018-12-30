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

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerViewInvitationAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_invitations, null);

        // create ViewHolder

        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        // - get data from your itemsData at this position
        // - replace the contents of the view with that itemsData

        viewHolder.inviteButton.setText(inviteData.get(position));


    }

    // inner class to hold a reference to each item of RecyclerView
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


    // Return the size of your itemsData (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return inviteData.size();
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        RecyclerViewInvitationAdapter.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
//        void onItemLongClick(int position, View v);
    }
}
