package com.parkingoracle.parkingoracle;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

    private static final String Tag= "RecyclerView Adapter";

    private List<String> mGarageNames = new ArrayList<>();
    private List<String> mMaxCapacties = new ArrayList<>();
    private List<String> mSpotsOpen = new ArrayList<>();
    private Context mContext;

    //Constructor
    public RecyclerViewAdapter(Context context, List<String> GarageNames, List<String> MaxCapacties, List<String> SpotsOpen){
        mContext = context;
        mGarageNames = GarageNames;
        mMaxCapacties = MaxCapacties;
        mSpotsOpen = SpotsOpen;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        Log.d( Tag, "onCreateViewHolder: called.");

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.garageitem, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        Log.d( Tag, "onBindViewHolder: called.");

        viewHolder.garageName.setText(mGarageNames.get(i));
        viewHolder.maxCapacity.setText(mMaxCapacties.get(i));
        if(mSpotsOpen!=null && mSpotsOpen.size() > i)
            viewHolder.spotsOpen.setText(mSpotsOpen.get(i));

    }

    @Override
    public int getItemCount() {
        return mGarageNames.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView garageName, maxCapacity, spotsOpen;
        RelativeLayout parentLayout;

        public ViewHolder(View itemView){
            super(itemView);
            garageName = itemView.findViewById(R.id.GarageName);
            maxCapacity = itemView.findViewById(R.id.maxcapacity);
            spotsOpen = itemView.findViewById(R.id.spotsopen);
            parentLayout = itemView.findViewById(R.id.parent);
        }
    }
}