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

    public String authToken;
    private  String CLIENT_ID = "2b034014a25644488ec9b5e285abf490";
    private  String REDIRECT_URI = "testschema://callback";
    private  int REQUEST_CODE = 1337;
    private SpotifyAppRemote mSpotifyAppRemote;
    private Context invokeContext;

    //singeltons constructor
    private SpotifyApiManager() {
    }

    public void setUpAuthetication() {


        AuthenticationRequest.Builder builder =
                new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);

        builder.setScopes(new String[]{"streaming"});
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(MainActivity.getInstace(), REQUEST_CODE, request);
    }



    public void connect() {


        ConnectionParams connectionParams =
                new ConnectionParams.Builder(CLIENT_ID)
                        .setRedirectUri(REDIRECT_URI)
                        .showAuthView(true)
                        .build();

        SpotifyAppRemote.connect(invokeContext, connectionParams,
                new Connector.ConnectionListener() {

                    @Override
                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote;
                        Log.d("MainActivity", "Connected! Yay!...... " + invokeContext);
                        mSpotifyAppRemote.getPlayerApi().play("spotify:user:spotify:playlist:37i9dQZF1DX2sUQwD7tbmL");
                        // Now you can start interacting with App Remote
                        //connect();
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.e("MainActivity", throwable.getMessage(), throwable);

                        // Something went wrong when attempting to connect! Handle errors here
                    }
                });
        Log.d("MainActivity", "IN SINGLETON"+ invokeContext);

    }

    public void setInvokeContext(Context invokeContext) {
        this.invokeContext = invokeContext;
    }

    // singleton
    public static SpotifyApiManager getInstance() {
        return ourInstance;
    }
}
