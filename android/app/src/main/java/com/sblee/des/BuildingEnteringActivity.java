package com.sblee.des;

import android.Manifest;
import android.content.DialogInterface;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sblee.des.objects.Building;
import com.sblee.des.util.UserPref;
import com.sblee.des.util.Utils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class BuildingEnteringActivity extends AppCompatActivity {

    @Bind(R.id.abe_main_list)
    RecyclerView mainListView;

    BuildingAdapter mAdapter;

    ArrayList<Building> buildings;

    LocationManager locationManager;
    LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building_entering);

        ButterKnife.bind(this);

        mAdapter = new BuildingAdapter();
        mainListView.setAdapter(mAdapter);

        buildings = new ArrayList<>();

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener();
        String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
        if (Utils.isPermissionsGranted(this, permissions)) {
            //noinspection MissingPermission
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000,
                    30, locationListener);
            //noinspection MissingPermission
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000,
                    30, locationListener);
        } else {
            if (Utils.shouldShowPermissionRationale(this,
                    permissions)) {
                showRequestPermissionRationale("");
            } else {
                requestLocationPermission();
            }
        }
    }

    private void showRequestPermissionRationale(String msg){
        AlertDialog.Builder builder = new AlertDialog.
                Builder(this).setMessage(msg).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                requestLocationPermission();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        final AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                0);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        if (requestCode == 0){
            if (grantResults.length == 1 && permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                //noinspection MissingPermission
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000,
                        30, locationListener);
                //noinspection MissingPermission
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000,
                        30, locationListener);
            }
        }
    }

    private void updateLocation(Location location) {
        AsyncHttpClient client = new AsyncHttpClient();

        RequestParams params = new RequestParams();
        params.put("facebook_id", new UserPref(this)
                .getFacebookId());
        params.put("lat", String.valueOf(location.getLatitude()));
        params.put("lon", String.valueOf(location.getLongitude()));
        params.put("accuracy", String.valueOf(location.getAccuracy()));

        client.put(Utils.ADDRESS + "updateLocation", params, new JsonHttpResponseHandler(){

        });
    }

    private void loadBuildings(Location location){
        AsyncHttpClient client = new AsyncHttpClient();

        RequestParams params = new RequestParams();
        params.put("facebook_id", new UserPref(this)
                .getFacebookId());
        params.put("lat", String.valueOf(location.getLatitude()));
        params.put("lon", String.valueOf(location.getLongitude()));
        params.put("accuracy", String.valueOf(location.getAccuracy()));

        client.get(Utils.ADDRESS + "getBuildings", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    String code = response.getString("code");
                    if (code.equals("00")) {
                        buildings.clear();
                        JSONArray data = response.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++){
                            JSONObject buildingData = data.getJSONObject(i);
                            buildings.add(new Building(buildingData));
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
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
            holder.setBuilding(buildings.get(position));
        }

        @Override
        public int getItemCount() {
            return buildings.size();
        }
    }

    class BuildingHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.cb_profile_image_view)
        ImageView profileIv;

        @Bind(R.id.cb_building_name_textview)
        TextView buildingNameTv;
        @Bind(R.id.cb_address_textview)
        TextView addressTv;

        @Bind(R.id.cb_enter_textview)
        TextView enterTv;

        public BuildingHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        public void setBuilding(Building building) {
            Picasso.with(BuildingEnteringActivity.this)
                    .load(building.getProfileImage()).into(profileIv);
            buildingNameTv.setText(building.getName());
            addressTv.setText(building.getAddress());
        }
    }

    class LocationListener implements android.location.LocationListener {

        @Override
        public void onLocationChanged(Location location) {
//            if (location.getAccuracy() < 150){
                updateLocation(location);
                loadBuildings(location);
//            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }
}
