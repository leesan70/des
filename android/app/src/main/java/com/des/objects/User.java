package com.des.objects;

import org.json.JSONObject;

public class User {

    private String username;
    private String facebook_id;
    private String gender;
    public User(JSONObject data) {
        try {
            username = data.getString("name");
            facebook_id = data.getString("facebook_id");
            gender = data.getString("gender");

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public String getUsername() {
        return username;
    }
    public String getFacebookID() { return facebook_id; }
    public String getGender() { return gender; }

    @Override
    public String toString() { return this.username; }
}
