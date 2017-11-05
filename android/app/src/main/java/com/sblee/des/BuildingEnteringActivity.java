package com.sblee.des;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BuildingEnteringActivity extends AppCompatActivity {

    @BindView(R.id.abe_main_list)
    RecyclerView mainListView;

    BuildingAdapter mAdapter;

    ArrayList buildings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building_entering);

        ButterKnife.bind(this);

        mAdapter = new BuildingAdapter();
        mainListView.setAdapter(mAdapter);

        buildings = new ArrayList();
    }

    class BuildingAdapter extends RecyclerView.Adapter<BuildingHolder> {

        @Override
        public BuildingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemvView = LayoutInflater.from(BuildingEnteringActivity.this)
                    .inflate(R.layout.container_building, parent, false);
            return new BuildingHolder(itemvView);
        }

        @Override
        public void onBindViewHolder(BuildingHolder holder, int position) {
            buildings.get(position);
            holder.setBuilding();
        }

        @Override
        public int getItemCount() {
            return buildings.size();
        }
    }

    class BuildingHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.cb_profile_image_view)
        ImageView profileIv;

        @BindView(R.id.cb_building_name_textview)
        TextView buildingNameTv;
        @BindView(R.id.cb_address_textview)
        TextView addressTv;

        @BindView(R.id.cb_enter_textview)
        TextView enterTv;

        public BuildingHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        public void setBuilding() {
        }
    }
}
