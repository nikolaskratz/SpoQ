package com.example.nikolas.spotfiyusefirsttry;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.google.android.gms.common.api.Response;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Album;
import retrofit.Callback;
import retrofit.RetrofitError;

/*THIS SINGLETON IS UNDER CONSTRUCTION*/

public class SpotifyApiManager {
    public static final SpotifyApiManager ourInstance = new SpotifyApiManager();

    private  String CLIENT_ID = "2b034014a25644488ec9b5e285abf490";
    private  String REDIRECT_URI = "testschema://callback";
    private  int REQUEST_CODE = 1337;
    private SpotifyAppRemote mSpotifyAppRemote;
    private Context invokeContext;

    //singelton's constructor
    private SpotifyApiManager() {
    }

    // first step of authentication
    public void setUpAuthetication() {

        AuthenticationRequest.Builder builder = 
                new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);
        builder.setScopes(new String[]{"streaming"});
        AuthenticationRequest request = builder.build();

        // TODO: 24/11/2018 change activity in parameter below
        AuthenticationClient.openLoginActivity(MainActivity.getInstace(), REQUEST_CODE, request);
    }

        // will be implemented
    public void connect() {
    }

    // setter for context
    public void setInvokeContext(Context invokeContext) {
        this.invokeContext = invokeContext;
    }

    // singleton
    public static SpotifyApiManager getInstance() {
        return ourInstance;
    }
}
