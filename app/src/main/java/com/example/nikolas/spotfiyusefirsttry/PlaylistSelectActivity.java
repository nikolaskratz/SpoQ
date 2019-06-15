package com.example.nikolas.spotfiyusefirsttry;

import android.content.Intent;
import android.content.Loader;
import android.content.pm.ActivityInfo;
import android.graphics.PorterDuff;
import android.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.SearchView;

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

public class PlaylistSelectActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Playlist>> {

    // TODO: 6/7/2019 fix scrolling -- need to implelent recycler viewer with layout manager
    
    private static final String TAG = "PlaylistSelectDebug";

    String playlistID;
    String playlistUser;
    PlaylistInfo playlistInfo = new PlaylistInfo();
    private static final String CLIENT_ID = "2b034014a25644488ec9b5e285abf490";
    private static final String REDIRECT_URI = "testschema://callback";
    private static final int REQUEST_CODE = 1337;

    private static final int PLAYLIST_LOADER_ID = 1;
    private static final int SEARCH_PLAYLIST_LOADER_ID = 2;

    private static final String FEATURED_PLAYLIST_URL = "https://api.spotify.com/v1/browse/featured-playlists?limit=9";
    private static final String SEARCH_PLAYLIST_URL = "https://api.spotify.com/v1/search";

    private GridView playlistGridView;
    private GridView playlistSearchGridView;

    public static PlaylistSelectActivity playlistSelect;
    private ProgressBar progressBar;
    private SearchView searchBar;

    private String authToken;

    String searchPlaylistUrlParam;
    @Override
    protected void onResume() {
        super.onResume();
        // takes off the focus from searchView
        View current = getCurrentFocus();
        if (current != null) current.clearFocus();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_select);
        playlistSelect = this;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        progressBar = (ProgressBar) findViewById(R.id.progressBarPlaylistSelection);
        progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this, R.color.searchViewBackground), PorterDuff.Mode.SRC_IN);

        searchBar = (SearchView) findViewById(R.id.sv_search_playlist);

        // enables clicking on the whole bar instead on the icon to activate it
        searchBar.setIconified(false);

        playlistGridView = (GridView) findViewById(R.id.playlistGridView);
        playlistSearchGridView = (GridView) findViewById(R.id.playlistSearchGridView);

        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchPlaylistUrlParam = addSearchParamToSearchPlaylistsQuery(SEARCH_PLAYLIST_URL, query);

                LoaderManager loaderManager = getLoaderManager();

                //restarting query
                loaderManager.restartLoader(SEARCH_PLAYLIST_LOADER_ID, null, playlistSelect);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        setUpAuthentication();
    }

    private String addSearchParamToSearchPlaylistsQuery(String baseURL, String param) {

        if (param == null) {
            return null;
        }
        param = param.replace(" ", "%20");

        return baseURL + "?q=" + param + "&type=playlist" + "&limit=6";
    }

    // implemenation of LoadManager interface for new playlistLoader
    @Override
    public Loader<List<Playlist>> onCreateLoader(int id, Bundle args) {

        PlaylistSelectLoader playlistSelectLoader = null;

        switch (id) {
            case PLAYLIST_LOADER_ID:
                playlistSelectLoader = new PlaylistSelectLoader(this, FEATURED_PLAYLIST_URL, authToken);
                break;
            case SEARCH_PLAYLIST_LOADER_ID:
                playlistSelectLoader = new PlaylistSelectLoader(this, searchPlaylistUrlParam, authToken);
                break;
        }

        return playlistSelectLoader;
    }

    @Override
    public void onLoadFinished(Loader<List<Playlist>> loader, List<Playlist> data) {

        progressBar.setVisibility(View.GONE);

        switch (loader.getId()) {

            // if the default spotify playlist loader returns, call this adapter
            case PLAYLIST_LOADER_ID:
                PlaylistSelectAdapter playlistSelectAdapterRelated = new PlaylistSelectAdapter(this, data);
                playlistGridView.setAdapter(playlistSelectAdapterRelated);

                //attach onClick listener to the adapter
                playlistGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        playlistID = data.get(position).getPlaylistId();
                        playlistUser = "spotify";

                        Intent intent = new Intent(PlaylistSelectActivity.this, PlayQuiz.class);
                        intent.putExtra("me", getIntent().getExtras().getString("me"));
                        intent.putExtra("vs", getIntent().getExtras().getString("vs"));
                        startActivity(intent);
                        finish();
                    }
                });
                break;

            case SEARCH_PLAYLIST_LOADER_ID:
                //bring back playlistSearchView
                playlistSearchGridView.setVisibility(View.VISIBLE);

                PlaylistSelectAdapter playlistSelectAdapterSearched = new PlaylistSelectAdapter(this, data);
                playlistSearchGridView.setAdapter(playlistSelectAdapterSearched);


                Log.d(TAG, "data inspect: " + data.get(0).getPlaylistName() );
                playlistSelectAdapterSearched.notifyDataSetChanged();

                //attach onClick listener to the adapter
                playlistSearchGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        Log.d(TAG, "onItemClick: " + data.get(position).getPlaylistName());

                        playlistID = data.get(position).getPlaylistId();
                        playlistUser = "spotify";

                        Intent intent = new Intent(PlaylistSelectActivity.this, PlayQuiz.class);
                        intent.putExtra("me", getIntent().getExtras().getString("me"));
                        intent.putExtra("vs", getIntent().getExtras().getString("vs"));
                        startActivity(intent);
                        finish();
                    }
                });
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Playlist>> loader) {

    }

    //set up authentication (Web)
    public void setUpAuthentication() {
        AuthenticationRequest.Builder builder =
                new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);
        builder.setScopes(new String[]{"streaming"});
        AuthenticationRequest request = builder.build();
        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
    }

    //Authentication function which also saves the AccesToken to use with WebApi
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        super.onActivityResult(requestCode, resultCode, intent);
        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            //Log.i(TAG, "reached auth, response: " + response.getType());

            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    authToken = response.getAccessToken();

                    LoaderManager loaderManager = getLoaderManager();
                    loaderManager.initLoader(PLAYLIST_LOADER_ID, null, this);
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

    // used in PlayQuiz class
    public void getPlaylistTracks(String playlistID, String playlistUser, final GamePlayManager gamePlayManager) {
        Log.e("getPlaylistTracksTest", "started getPlaylistTracksTest");
        SpotifyApi api = new SpotifyApi();
        api.setAccessToken(authToken);
        SpotifyService spotify = api.getService();
        Log.e("getPlaylistTracksTest", "authToken: " + authToken);
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

    public String getPlaylistID() {
        return playlistID;
    }

    public String getPlaylistUser() {
        return playlistUser;
    }

    public static PlaylistSelectActivity getPlaylistSelect() {
        return playlistSelect;
    }

}
