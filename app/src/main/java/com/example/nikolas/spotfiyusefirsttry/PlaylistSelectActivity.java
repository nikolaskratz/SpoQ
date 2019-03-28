package com.example.nikolas.spotfiyusefirsttry;

import android.accounts.AccountManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.PlaylistTrack;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.client.UrlConnectionClient;

public class PlaylistSelectActivity extends AppCompatActivity {

    private static final String TAG = "PlaylistSelectDebug";

    private static final String  QUERY_AUTH_TOKEN = "https://accounts.spotify.com/authorize?client_id=2b034014a25644488ec9b5e285abf490&response_type=code&redirect_uri=testschema://callback";

    TextView textView;
    String playlistID;
    String playlistUser;
    PlaylistInfo playlistInfo = new PlaylistInfo();
    private Button b1,b2,b3,b4,b5,b6,b7,b8;
    private static final String CLIENT_ID = "2b034014a25644488ec9b5e285abf490";
    private static final String REDIRECT_URI = "testschema://callback";
    private static final int REQUEST_CODE = 1337;

    public static PlaylistSelectActivity playlistSelect;

    private String authToken;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_select);


        //AsyncTaskDebug asyncTaskDebug = new AsyncTaskDebug();
       // asyncTaskDebug.execute(QUERY_AUTH_TOKEN);

        // setting up authentication to fetch authToken
        setUpAuthentication();

        //debug
        //SpotifyWebApiUtils.fetchFeaturedPlaylists("limit=5","country=SE");

        playlistSelect = this;
        initControl();
        setPlaylistNameButton();
        listen();
    }

    //asyncTask for debugging purposes

    private class AsyncTaskDebug extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... strings) {

            Log.d(TAG, "doInBackground: " + strings[1]);
            return SpotifyWebApiUtils.getData(strings[0],strings[1]);
        }

        @Override
        protected void onPostExecute(String s) {
            Log.d(TAG, "RESPONSE_FROM_SPOTIFY: " + s);
        }
    }


    //set up authentication (Web)
    public void setUpAuthentication(){
        AuthenticationRequest.Builder builder =
                new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);
        builder.setScopes(new String[]{"streaming"});
        AuthenticationRequest request = builder.build();


        //AuthenticationClient.openLoginInBrowser(this, request);

        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
    }

    //Authentication function which also saves the AccesToken to use with WebApi
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        super.onActivityResult(requestCode, resultCode, intent);
        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            //Log.i(TAG, "reached auth, response: "+response.getType());

            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    authToken = response.getAccessToken();
                    Log.i(TAG, "reached token " + authToken);

                    AsyncTaskDebug asyncTaskDebug = new AsyncTaskDebug();
                    asyncTaskDebug.execute("https://api.spotify.com/v1/browse/featured-playlists", authToken);
                    // start async task here ?

                    break;

                // Auth flow returned an error
                case ERROR:
                    Log.i(TAG, "reached error");
                    // Handle error response
                    break;

                // Most likely auth flow was cancelled
                default:
                    Log.i(TAG, "reached default");
                    // Handle other cases
            }
        }
    }

    public void getPlaylistTracks(String playlistID, String playlistUser, final GamePlayManager gamePlayManager)  {
        Log.e("getPlaylistTracksTest", "started getPlaylistTracksTest");
        SpotifyApi api = new SpotifyApi();
        api.setAccessToken(authToken);
        SpotifyService spotify = api.getService();
        Log.e("getPlaylistTracksTest", "authToken: "+authToken);
        spotify.getPlaylistTracks(playlistUser, playlistID, new Callback<Pager<PlaylistTrack>>() {

            @Override
            public void success(Pager<PlaylistTrack> playlistTrackPager, Response response) {
                Log.e("getPlaylistTracksTest", "GOT the tracks in playlist");
                List<PlaylistTrack> items = playlistTrackPager.items;
                gamePlayManager.setPlaylist(items);
                gamePlayManager.proceed();
            }
            @Override
            public void failure(RetrofitError error) {
                Log.e("getPlaylistTracksTest", "error while getting tracks of playlist");
            }
        });

    }

    private void initControl(){
        b1 = (Button) findViewById(R.id.button1);
        b2 = (Button) findViewById(R.id.button2);
        b3 = (Button) findViewById(R.id.button3);
        b4 = (Button) findViewById(R.id.button4);
        b5 = (Button) findViewById(R.id.button5);
        b6 = (Button) findViewById(R.id.button6);
        b7 = (Button) findViewById(R.id.button7);
        b8 = (Button) findViewById(R.id.button8);
    }

    public void listen(){
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playlistID=playlistInfo.getPlaylistID().get(0);
                playlistUser=playlistInfo.getPlaylistUser().get(0);
                startQuiz();
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playlistID=playlistInfo.getPlaylistID().get(1);
                playlistUser=playlistInfo.getPlaylistUser().get(1);
                startQuiz();
            }
        });
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playlistID=playlistInfo.getPlaylistID().get(2);
                playlistUser=playlistInfo.getPlaylistUser().get(2);
                startQuiz();
            }
        });
        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playlistID=playlistInfo.getPlaylistID().get(3);
                playlistUser=playlistInfo.getPlaylistUser().get(3);
                startQuiz();
            }
        });
        b5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playlistID=playlistInfo.getPlaylistID().get(4);
                playlistUser=playlistInfo.getPlaylistUser().get(4);
                startQuiz();
            }
        });
        b6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playlistID=playlistInfo.getPlaylistID().get(5);
                playlistUser=playlistInfo.getPlaylistUser().get(5);
                startQuiz();
            }
        });
        b7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playlistID=playlistInfo.getPlaylistID().get(6);
                playlistUser=playlistInfo.getPlaylistUser().get(6);
                startQuiz();
            }
        });
        b8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playlistID=playlistInfo.getPlaylistID().get(7);
                playlistUser=playlistInfo.getPlaylistUser().get(7);
                startQuiz();
            }
        });
    }

    void setPlaylistNameButton (){
        b1.setText(playlistInfo.getPlaylistName().get(0));
        b2.setText(playlistInfo.getPlaylistName().get(1));
        b3.setText(playlistInfo.getPlaylistName().get(2));
        b4.setText(playlistInfo.getPlaylistName().get(3));
        b5.setText(playlistInfo.getPlaylistName().get(4));
        b6.setText(playlistInfo.getPlaylistName().get(5));
        b7.setText(playlistInfo.getPlaylistName().get(6));
        b8.setText(playlistInfo.getPlaylistName().get(7));

    }

    public String getPlaylistID() {
        return playlistID;
    }

    public String getPlaylistUser() {
        return playlistUser;
    }

    public static PlaylistSelectActivity getPlaylistSelect() {
        return playlistSelect;
    }

    void startQuiz(){
        Intent intent = new Intent(PlaylistSelectActivity.this, PlayQuiz.class);
        intent.putExtra("me",getIntent().getExtras().getString("me"));
        intent.putExtra("vs",getIntent().getExtras().getString("vs"));
        startActivity(intent);
        finish();
    }
}
