package com.des;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.des.util.UserPref;
import com.des.util.Utils;

import org.json.JSONObject;

import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.am_facebook_login_button)
    LoginButton loginButton;

    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        // If the user is logged in, check pref to see if the gender settings are set
        // prompt to GenderUpdatingActivity if not, otherwise continue on to BuildingEnterActivity
        final UserPref pref = new UserPref(this);
        if (pref.isUserLoggedIn()){
            if (pref.getGender().equals("")){
                Intent intent = new Intent(MainActivity.this,
                        GenderUpdatingActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(MainActivity.this,
                        BuildingEnteringActivity.class);
                startActivity(intent);
            }
            finish();
            return;
        }

        // Log in using Facebook account
        loginButton.setReadPermissions(Arrays.asList("email"));
        callbackManager = CallbackManager.Factory.create();
        FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                // App code
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                try {
                                    // Get facebook_id, facebook_token, and name from the response
                                    // Make HTTP Post to our server to process login
                                    final String id = object.getString("id");
                                    String name = object.getString("name");
                                    AsyncHttpClient client = new AsyncHttpClient();

                                    RequestParams params = new RequestParams();
                                    params.put("facebook_id", id);
                                    params.put("facebook_token", loginResult.getAccessToken().getToken());
                                    params.put("push_key", "");
                                    params.put("name", name);

                                    client.post(Utils.ADDRESS + "login", params, new JsonHttpResponseHandler(){
                                        @Override
                                        public void onFailure(int statusCode, Header[] headers,
                                                              Throwable throwable, JSONObject errorResponse) {

                                        }

                                        @Override
                                        public void onSuccess(int statusCode, Header[] headers, JSONObject response){
                                            try {
                                                String code = response.getString("code");
                                                if (code.equals("00")) {
                                                    // Cache the response to pref and
                                                    // Start GenderUpdating Activity
                                                    pref.setUserData(id,
                                                            loginResult.getAccessToken().getToken());
                                                    Intent intent = new Intent(MainActivity.this,
                                                            GenderUpdatingActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            } catch (Exception e){
                                                e.printStackTrace();
                                            }

                                        }
                                    });
                                } catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                // App code
                Utils.createAlert(MainActivity.this,
                        getResources().getString(R.string.user_canceled_login));
            }

            @Override
            public void onError(FacebookException exception) {
                Utils.createAlert(MainActivity.this, exception.toString());
            }
        };
        // Support both LoginButton and LoginManager
        loginButton.registerCallback(callbackManager, callback);
        LoginManager.getInstance().registerCallback(callbackManager, callback);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
