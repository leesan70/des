package com.sblee.des;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RadioButton;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sblee.des.util.UserPref;
import com.sblee.des.util.Utils;

import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

public class GenderUpdatingActivity extends AppCompatActivity {

    @Bind(R.id.agu_gender_male)
    RadioButton genderMale;
    @Bind(R.id.agu_gender_female)
    RadioButton genderFemale;

    @Bind(R.id.agu_pref_female)
    RadioButton prefFemale;
    @Bind(R.id.agu_pref_male)
    RadioButton prefMale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gender_updating);

        ButterKnife.bind(this);
    }

    @OnCheckedChanged(R.id.agu_gender_male) void onGenderMaleChecked(){
        genderFemale.setChecked(false);
    }

    @OnCheckedChanged(R.id.agu_gender_female) void onGenderFemaleChecked(){
        genderMale.setChecked(false);
    }

    @OnCheckedChanged(R.id.agu_pref_male) void onPrefMaleChecked(){
        prefFemale.setChecked(false);
    }

    @OnCheckedChanged(R.id.agu_pref_female) void onPrefFemaleChecked(){
        prefMale.setChecked(false);
    }

    @OnClick(R.id.agu_next_textview) void onNextClick() {
        final String gender = genderMale.isChecked() ? "Male" : "Female";
        String preference = prefFemale.isChecked() ? "Female" : "Male";
        final UserPref pref = new UserPref(this);

        AsyncHttpClient client = new AsyncHttpClient();

        RequestParams params = new RequestParams();
        params.put("facebook_id", pref.getFacebookId());
        params.put("gender", gender);
        params.put("preference", preference);

        client.put(Utils.ADDRESS + "updateGenderPreference", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    String code = response.getString("code");
                    if (code.equals("00")) {
                        pref.setGender(gender);
                        Intent intent = new Intent(GenderUpdatingActivity.this,
                                BuildingEnteringActivity.class);
                        startActivity(intent);
                        finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
