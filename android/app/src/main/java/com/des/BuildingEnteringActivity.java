package com.des;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.des.objects.Building;
import com.des.util.UserPref;
import com.des.util.Utils;
import com.squareup.picasso.Picasso;
import cz.msebera.android.httpclient.Header;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class BuildingEnteringActivity extends AppCompatActivity {

    @Bind(R.id.abe_main_list)
    RecyclerView mainListView;

    BuildingAdapter mAdapter;

    ArrayList<Building> buildings;

    LocationManager locationManager;
    LocationListener locationListener;

    Location lastLocation;

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
            lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            locationListener.onLocationChanged(lastLocation);
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

        // Update PushKey
        updatePushKey();
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
                lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                locationListener.onLocationChanged(lastLocation);
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
        Building.getBuildingsNear(location, this, new Building.OnBuildingLoadedListener() {
            @Override
            public void onLoaded(List<Building> buildings) {
                BuildingEnteringActivity.this.buildings.clear();
                BuildingEnteringActivity.this.buildings.addAll(buildings);
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    class BuildingAdapter extends RecyclerView.Adapter<BuildingHolder> {

        @Override
        public BuildingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemvView = LayoutInflater.from(BuildingEnteringActivity.this)
                    .inflate(R.layout.container_building, parent, false);
//            TextView enter = (TextView) itemvView.findViewById(R.id.cb_enter_textview);
//            enter.
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
            enterTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), UsersActivity.class);
                    intent.putExtra("building", buildingNameTv.getText().toString());
                    startActivity(intent);
                }
            });
        }
    }

    class LocationListener implements android.location.LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            if (location.getAccuracy() <= lastLocation.getAccuracy() || (lastLocation.getTime() -
                    location.getTime()) > 10 * 60 * 1000){
                updateLocation(location);
                loadBuildings(location);
                lastLocation = location;
            }
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

    private void updatePushKey() {
        AsyncHttpClient client = new AsyncHttpClient();
        final UserPref pref = new UserPref(this);
        RequestParams params = new RequestParams();
        System.out.println("facebook_id: " + pref.getFacebookId());
        params.put("facebook_id", pref.getFacebookId());
        params.put("push_key", pref.getPushKey());
        System.out.println("UPDATING PUSH KEY");
        client.put(Utils.ADDRESS + "sendPushKey", params, new JsonHttpResponseHandler() {

        });
    }
}
