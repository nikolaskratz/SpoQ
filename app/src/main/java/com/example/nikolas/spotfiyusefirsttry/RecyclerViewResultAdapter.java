package com.example.nikolas.spotfiyusefirsttry;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.PointerIcon;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class RecyclerViewResultAdapter extends RecyclerView.Adapter<RecyclerViewResultAdapter.ViewHolder> {
//    private ArrayList<String> vsData;
    private ArrayList<Integer> p1Points;
    private ArrayList<Integer> p2Points;
    private ArrayList<String> p1;
    private ArrayList<String> p2;

    public RecyclerViewResultAdapter(ArrayList<Integer> p1Points, ArrayList<Integer> p2Points,
                                     ArrayList<String> p1, ArrayList<String> p2) {
        this.p1Points = p1Points;
        this.p2Points = p2Points;
        this.p1 = p1;
        this.p2 = p2;
    }

    @Override
    public RecyclerViewResultAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                       int viewType) {

        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_result, null);

        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

//        viewHolder.vsPlayer.setText(vsData.get(position));
        Log.d("setup", "nameP1:"+p1.get(position)+" pointsP1:"+p1Points.get(position));
        viewHolder.nameP1.setText(p1.get(position));
        viewHolder.nameP2.setText(p2.get(position));
        viewHolder.pointsP1.setText(""+p1Points.get(position));
        viewHolder.pointsP2.setText(""+p2Points.get(position));


    }

//    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public static class ViewHolder extends RecyclerView.ViewHolder {

//        public TextView vsPlayer;
        public TextView nameP1;
        public TextView nameP2;
        public TextView pointsP1;
        public TextView pointsP2;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            nameP1 = (TextView) itemLayoutView.findViewById(R.id.nameP1);
            nameP2 = (TextView) itemLayoutView.findViewById(R.id.nameP2);
            pointsP1 = (TextView) itemLayoutView.findViewById(R.id.pointsP1);
            pointsP2 = (TextView) itemLayoutView.findViewById(R.id.pointsP2);
        }

//        @Override
//        public void onClick(View view) {
//            clickListener.onItemClick(getAdapterPosition(), view);
//        }

    }

    @Override
    public int getItemCount() {
        return p1Points.size();
    }

//    public void setOnItemClickListener(ClickListener clickListener) {
//        RecyclerViewInvitationAdapter.clickListener = clickListener;
//    }
//
//    public interface ClickListener {
//        void onItemClick(int position, View v);
////        void onItemLongClick(int position, View v);
//    }
}
