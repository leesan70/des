package com.des;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.des.objects.Building;
import com.des.objects.User;
import com.des.util.UserPref;
import com.des.util.Utils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class UsersActivity extends AppCompatActivity {

    @Bind(R.id.users_main_list)
    RecyclerView mainListView;

    UserAdapter adapter;

    List<User> users;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        Bundle buildingDataBundle = getIntent().getExtras();
        final UserPref pref = new UserPref(this);
        String thisUserFacebookID = pref.getFacebookId();
        if (buildingDataBundle == null) {
            return;
        }

        String selectedBuilding = buildingDataBundle.getString("building");

        ButterKnife.bind(this);
        users = new ArrayList<User>();
        adapter = new UserAdapter();

        mainListView.setAdapter(adapter);

        getUsersInSameBuilding(thisUserFacebookID, selectedBuilding);

    }

    class UserHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.user_gender)
        TextView genderTv;

        @Bind(R.id.user_name) TextView user_nameTv;
        @Bind(R.id.like_button)
        ImageView likeButton;

        public UserHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setUser(final User user) {
            String gender = user.getGender();
            if (gender.equals("male")) {
                genderTv.setText("M");
            } else {
                genderTv.setText("F");
            }

            user_nameTv.setText(user.getUsername());
            likeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AsyncHttpClient client = new AsyncHttpClient();
                    final UserPref pref = new UserPref(getApplicationContext());
                    RequestParams params = new RequestParams();
                    System.out.println("source_facebook_id: " + pref.getFacebookId());
                    params.put("source_facebook_id", pref.getFacebookId());
                    params.put("dest_facebook_id", user.getFacebookID());
                    System.out.println("SENDING NOTIFICATION");
                    client.put(Utils.ADDRESS + "sendNotification", params, new JsonHttpResponseHandler() {

                    });
                }
            });
            /* Not sure what to do with facebookID yet */
        }
    }

    class UserAdapter extends RecyclerView.Adapter<UserHolder> {
        @Override
        public UserHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(UsersActivity.this).inflate(R.layout.container_user, parent, false);
            return new UserHolder(itemView);
        }

        @Override
        public void onBindViewHolder(UserHolder holder, int pos) {
            holder.setUser(users.get(pos));
            System.out.println("BIND VIEW HOLDER" + users.get(pos).toString());
        }

        @Override
        public int getItemCount() {return users.size();}
    }

    public void getUsersInSameBuilding(String thisUserFacebookID, String selectedBuilding) {
        AsyncHttpClient client = new AsyncHttpClient();

        RequestParams params = new RequestParams();
        params.put("facebook_id", thisUserFacebookID);
        params.put("building_name", selectedBuilding);
        client.get(Utils.ADDRESS + "getUserInBuilding", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    String code = response.getString("code");
                    if (code.equals("00")) {
                        List<User> usersInBuilding = new ArrayList<>();
                        JSONArray data = response.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++){
                            JSONObject userData = data.getJSONObject(i);
                            usersInBuilding.add(new User(userData));
                        }
                        UsersActivity.this.users.clear();
                        UsersActivity.this.users.addAll(usersInBuilding);
                        System.out.println("ADDED USERS: " + UsersActivity.this.users.toString());
                        adapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


}
