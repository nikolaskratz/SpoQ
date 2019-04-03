package com.example.nikolas.spotfiyusefirsttry;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;


public class SpotifyWebApiUtils {

    private static final String TAG = "SpotifyWebApiUtilsDebug";
    private static final String CLIENT_ID = "2b034014a25644488ec9b5e285abf490";
    private static final String REDIRECT_URI = "testschema://callback";
    private static final int REQUEST_CODE = 1337;

    // testing with full response
    private static final String  QUERY_AUTH_TOKEN = "https://accounts.spotify.com/authorize?client_id=2b034014a25644488ec9b5e285abf490&response_type=code&redirect_uri=https%3A%2F%2Fexample.com%2Fcallback&scope=streaming";

    public SpotifyWebApiUtils() {}

    public static ArrayList<Playlist> getFeaturedPlaylists (String requestUrl , String authToken) {
        String featuredPlaylistsJSON = "";

        // create URL object from link in a string format
        URL urlForToken = getURLfromString(requestUrl);

        // make HTTP call to get the data
        try {
            featuredPlaylistsJSON = makeHttpRequestForPlaylists(urlForToken, authToken);
            Log.d(TAG, "getFeaturedPlaylists: " + featuredPlaylistsJSON);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayList<Playlist> playlistList = new ArrayList<Playlist>();
        playlistList = parseFeaturedPlaylists(featuredPlaylistsJSON);
        return playlistList;
    }

    private static ArrayList<Playlist> parseFeaturedPlaylists(String featuredPlaylistsJSON) {

        ArrayList<Playlist> playlists = new ArrayList<Playlist>();

        try {
            JSONObject root = new JSONObject(featuredPlaylistsJSON);
            JSONArray playlistsList = root.optJSONObject("playlists").optJSONArray("items");

            for(int i = 0; i < playlistsList.length(); i++){
                JSONObject playlistFeatures = playlistsList.getJSONObject(i);

                String playlistID = playlistFeatures.optString("id");

                String playlistName = playlistFeatures.optString("name");

                JSONObject image = playlistFeatures.getJSONArray("images").getJSONObject(0);;

                String playlistCoverUrl = image.optString("url");

                URL url = getURLfromString(playlistCoverUrl);
                Bitmap bmp = null;
                try {
                    bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                playlists.add(new Playlist(playlistName,bmp,playlistID));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return playlists;
    }

    //TODO refactor this function to more generic
    private static String makeHttpRequestForPlaylists(URL requestURL, String authToken) throws IOException{
        String serverResponse = "";

        if(requestURL == null) {
            Log.e(TAG, "URL is empty.");
            return serverResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) requestURL.openConnection();
            urlConnection.setRequestProperty("Accept","application/json");
            urlConnection.setRequestProperty("Content-Type","application/json");
            urlConnection.setRequestProperty("Authorization: ","Bearer " + authToken);
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");

            urlConnection.connect();

            // positive response from the server
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                serverResponse = readFromStream(inputStream);
            }
            else {
                Log.e(TAG, "Problem with the response, Response code:  " + urlConnection.getResponseCode());
            }

        } catch (IOException e) {
            Log.e(TAG, "Problem with retrieving JSON response ", e );
        } finally {
            if (urlConnection != null){
                urlConnection.disconnect();
            }
            if (inputStream != null){
                inputStream.close();
            }
        }
        return serverResponse;
    }

    // redundant method
    private static String makeHttpRequest(URL requestURL) throws IOException {
        String serverResponse = "";

        if(requestURL == null) {
            Log.e(TAG, "URL is empty.");
            return serverResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) requestURL.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                serverResponse = readFromStream(inputStream);
            }
            else {
                Log.e(TAG, "Problem with the response, Response code:  " + urlConnection.getResponseCode());
            }

        } catch (IOException e) {
            Log.e(TAG, "Problem with retrieving JSON response ", e );
        } finally {
            if (urlConnection != null){
                urlConnection.disconnect();
            }
            if (inputStream != null){
                inputStream.close();
            }
        }
        return serverResponse;
    }

    /**
     * Method generating a String from http request (input stream = bytes of data)
     * @param inputStream input stream from http request
     * @return returns a string, which is a complete json response from the server
     * @throws IOException  throws exception which is handled in the first invoke method
     */

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if(inputStream != null ){
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null){
                output.append(line);
                line =reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Basic conversion from String to URL object
     * @param requestURLString String form of the HTTP address
     * @return returns URL object with the link
     */

    private static URL getURLfromString(String requestURLString) {
        URL newURL = null;

        try {
            newURL = new URL(requestURLString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return newURL;
    }
}
