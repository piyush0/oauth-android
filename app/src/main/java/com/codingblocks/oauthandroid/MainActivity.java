package com.codingblocks.oauthandroid;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private static final String CLIENT_ID = "9047417470";
    private static final String REDIRECT_URI = "http://localhost";
    private static final String OAUTH_URL = "https://account.codingblocks.com/oauth/authorize";
    public static final String TAG = "MainActivity";

    WebView web;
    Button auth;
    SharedPreferences pref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pref = getSharedPreferences("AppPref", MODE_PRIVATE);
        auth = (Button) findViewById(R.id.auth);
        auth.setOnClickListener(new View.OnClickListener() {
            Dialog auth_dialog;

            @Override
            public void onClick(View arg0) {

                auth_dialog = new Dialog(MainActivity.this);
                auth_dialog.setContentView(R.layout.auth_dialog);
                web = (WebView) auth_dialog.findViewById(R.id.webv);
                web.getSettings().setJavaScriptEnabled(true);
                web.loadUrl(OAUTH_URL + "?redirect_uri=" + REDIRECT_URI + "&response_type=token&client_id=" + CLIENT_ID);
                web.setWebViewClient(new WebViewClient() {

                    boolean authComplete = false;
                    String authToken;

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);

                        if (url.contains("access_token=") && !authComplete) {
                            authToken = getAccessToken(url);
                            Log.d(TAG, "onPageFinished: " + url);
                            Log.d(TAG, "onPageFinished: " + authToken);
                            authComplete = true;
                            SharedPreferences.Editor edit = pref.edit();
                            edit.putString("access_token", authToken);
                            edit.commit();
                            auth_dialog.dismiss();
                        } else if (url.contains("error=access_denied")) {
                            Log.w(TAG, "ACCESS_DENIED_HERE");
                            authComplete = true;
                            auth_dialog.dismiss();
                        }
                    }
                });
                auth_dialog.show();
                auth_dialog.setTitle("");
                auth_dialog.setCancelable(true);
            }
        });
    }

    private String getAccessToken(String url) {

        int accessTokenIndex = url.indexOf("access_token");
        int loopStartIndex = accessTokenIndex + "access_token".length() + 1;
        int andIndex = url.indexOf("&");
        StringBuilder sb = new StringBuilder("");
        for (int i = loopStartIndex; i < andIndex; i++) {
            sb.append(url.charAt(i));
        }
        return sb.toString();
    }
}