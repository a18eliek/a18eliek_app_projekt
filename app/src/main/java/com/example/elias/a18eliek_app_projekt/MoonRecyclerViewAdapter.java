package com.example.elias.a18eliek_app_projekt;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.List;

public class MoonRecyclerViewAdapter extends RecyclerView.Adapter<MoonRecyclerViewAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<String> mMoonAuxData;
    private List<String> mMoonName;
    private List<String> mMoonDistance;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    public View view;

    // data is passed into the constructor
    MoonRecyclerViewAdapter(Context context, ArrayList<String> auxdata, List<String> name, List<String> distance) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mMoonAuxData = auxdata;
        this.mMoonName = name;
        this.mMoonDistance = distance;
    }


    // inflates the row layout from xml when needed
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.moon_recyclerview, parent, false);

        return new ViewHolder(view);
    }

    // binds the data to the view and textview in each row
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String moonImg = null;
        try {
            moonImg = SolarSystem.splitAuxdata(mMoonAuxData.get(position), "img");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String moonName = mMoonName.get(position);
        String moonDistance = mMoonDistance.get(position);

        Glide.with(mContext).load(moonImg).into(holder.moonImageView);

        holder.moonTextView.setText(moonName);
        holder.moonDistanceTextView.setText("Orbit Distance: " + moonDistance + "km.");
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mMoonName.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView moonImageView;
        TextView moonTextView;
        TextView moonDistanceTextView;

        ViewHolder(View itemView) {
            super(itemView);
            moonImageView = itemView.findViewById(R.id.moonImage);
            moonTextView = itemView.findViewById(R.id.moonName);
            moonDistanceTextView = itemView.findViewById(R.id.orbitDistance);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    String getMoonName(int id) {
        return mMoonName.get(id);
    }

    String getMoonDistance(int id) {
        return mMoonDistance.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }


}