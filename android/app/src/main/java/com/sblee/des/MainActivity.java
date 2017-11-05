package com.sblee.des;

import android.content.Intent;
import android.location.Address;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.internal.Utility;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sblee.des.util.Utils;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.am_facebook_login_button)
    LoginButton loginButton;

    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        loginButton.setReadPermissions("email", "name");
        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                // App code
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            String id = object.getString("id");
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
                                    Intent intent = new Intent(MainActivity.this, BuildingEnteringActivity.class);
                                    startActivity(intent);
                                    finish();
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
        });
    }
}
