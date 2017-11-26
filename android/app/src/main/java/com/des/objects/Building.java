package com.des.objects;

import android.content.Context;
import android.location.Location;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.des.util.UserPref;
import com.des.util.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class Building {

    public interface OnBuildingLoadedListener {
        void onLoaded(List<Building> buildings);
    }

    private int id;
    private String name;
    private String address;
    private String profileImage;

    public Building(JSONObject data) {
        try {
            id = data.getInt("building_id");
            name = data.getString("building_name");
            address = data.getString("building_address");
            profileImage = Utils.ADDRESS + String.format("images/%s.jpg", name);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public static void getBuildingsNear(Location location, Context context,
                                        final OnBuildingLoadedListener completion){
        AsyncHttpClient client = new AsyncHttpClient();

        RequestParams params = new RequestParams();
        params.put("facebook_id", new UserPref(context)
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
                        ArrayList<Building> buildings = new ArrayList<>();
                        JSONArray data = response.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++){
                            JSONObject buildingData = data.getJSONObject(i);
                            buildings.add(new Building(buildingData));
                        }
                        completion.onLoaded(buildings);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
