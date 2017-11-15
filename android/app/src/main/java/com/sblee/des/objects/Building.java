package com.sblee.des.objects;

import com.sblee.des.util.Utils;

import org.json.JSONObject;

/**
 * Created by minsoo on 2017. 11. 7..
 */

public class Building {

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
}
