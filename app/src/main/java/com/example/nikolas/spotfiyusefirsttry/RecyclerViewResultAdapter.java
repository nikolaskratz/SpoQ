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

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class RecyclerViewResultAdapter extends RecyclerView.Adapter<RecyclerViewResultAdapter.ViewHolder> {
//    private ArrayList<String> vsData;
    private static ClickListener clickListener;
    private ArrayList<Integer> p1Points;
    private ArrayList<Integer> p2Points;
    private ArrayList<String> p1;
    private ArrayList<String> p2;
    private ArrayList<String> p1ID;
    private ArrayList<String> p2ID;

    private String userID;

    public RecyclerViewResultAdapter(ArrayList<Integer> p1Points, ArrayList<Integer> p2Points,
                                     ArrayList<String> p1, ArrayList<String> p2,
                                     ArrayList<String> p1ID, ArrayList<String> p2ID) {
        this.p1Points = p1Points;
        this.p2Points = p2Points;
        this.p1 = p1;
        this.p2 = p2;
        this.p1ID=p1ID;
        this.p2ID=p2ID;

        FirebaseAuth userAuth = FirebaseAuth.getInstance();
        userID = userAuth.getCurrentUser().getUid();
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
        if(p1Points.isEmpty()) {
            viewHolder.vs.setText("You haven't played any games yet!");
        } else if(p2Points.get(position)==310) {
            viewHolder.vs.setText("Waiting for "+p2.get(position)+" to play...");
        } else if(userID.equals(p1ID.get(position))) {
            if(p1Points.get(position)<p2Points.get(position)){
                viewHolder.vs.setText("Loss against "+p2.get(position)+"!");
                Log.d("losscheck", "p2posi; p2points:"+p2Points.get(position));

            } else if (p1Points.get(position)>p2Points.get(position)){
                viewHolder.vs.setText("Victory against "+p2.get(position)+"!");
            } else viewHolder.vs.setText("Draw against "+p2.get(position)+"!");
        } else {
            if(p1Points.get(position)<p2Points.get(position)){
                viewHolder.vs.setText("Victory against "+p1.get(position)+"!");
            } else if (p1Points.get(position)>p2Points.get(position)) {
                viewHolder.vs.setText("Loss" + " against "+p1.get(position)+"!");
            } else viewHolder.vs.setText("Draw" + " against "+p1.get(position)+"!");
        }

//        viewHolder.vsPlayer.setText(vsData.get(position));
//        viewHolder.nameP1.setText(p1.get(position));
//        viewHolder.nameP2.setText(p2.get(position));
//        viewHolder.pointsP1.setText(""+p1Points.get(position));
//        viewHolder.pointsP2.setText(""+p2Points.get(position));


    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView vs;
//        public TextView nameP1;
//        public TextView nameP2;
//        public TextView pointsP1;
//        public TextView pointsP2;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            itemView.setOnClickListener(this);
//            nameP1 = (TextView) itemLayoutView.findViewById(R.id.nameP1);
//            nameP2 = (TextView) itemLayoutView.findViewById(R.id.nameP2);
//            pointsP1 = (TextView) itemLayoutView.findViewById(R.id.pointsP1);
//            pointsP2 = (TextView) itemLayoutView.findViewById(R.id.pointsP2);
            vs = (TextView) itemLayoutView.findViewById(R.id.vs);
        }

        @Override
        public void onClick(View view) {
            clickListener.onItemClick(getAdapterPosition(), view);
        }

    }

    @Override
    public int getItemCount() {
        int itemCount= p1Points.size();
        if(itemCount>0) {
            return itemCount;
        } else return 1;
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        RecyclerViewResultAdapter.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
//        void onItemLongClick(int position, View v);
    }
}
