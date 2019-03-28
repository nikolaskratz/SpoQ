package com.example.nikolas.spotfiyusefirsttry;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

import retrofit.client.UrlConnectionClient;


//GET https://accounts.spotify.com/authorize?client_id=5fe01282e44241328a84e7c5cc169165&response_type=code&redirect_uri=https%3A%2F%2Fexample.com%2Fcallback&scope=user-read-private%20user-read-email&state=34fFs29kd09

public class SpotifyWebApiUtils {

    private static final String TAG = "SpotifyWebApiUtilsDebug";

    private static final String CLIENT_ID = "2b034014a25644488ec9b5e285abf490";
    private static final String REDIRECT_URI = "testschema://callback";
    private static final int REQUEST_CODE = 1337;


    // testing with full response
    private static final String  QUERY_AUTH_TOKEN = "https://accounts.spotify.com/authorize?client_id=2b034014a25644488ec9b5e285abf490&response_type=code&redirect_uri=https%3A%2F%2Fexample.com%2Fcallback&scope=streaming";

    public SpotifyWebApiUtils() {}

    public static void fetchFeaturedPlaylists(String... param){

        //declaring the base query form for request
        final String QUERY_BASE = "https://api.spotify.com/v1/browse/featured-playlists";

        // get auth token
        //String authToken = getAuthToken();

        //Log.d(TAG, "fetchFeaturedPlaylists: " + authToken);

        // creating a url to fetch data
        //URL requestURL = prepareURLforQuery(QUERY_BASE,authToken,param);

        // make httpRequest to fetch the data
    }

    public static String getData(String requestUrl , String authToken) {
        String testData = "";

        Log.d(TAG, "getData: " + authToken);
        URL urlForToken = getURLfromString(requestUrl);

        try {
            testData = makeHttpRequestSpotify(urlForToken, authToken);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "getData: " + testData);
        return testData;
    }


    public static String getAuthToken(String requestUrl) {
        String authToken = "";

        URL urlForToken = getURLfromString(requestUrl);

        try {
            authToken = makeHttpRequest(urlForToken);
        } catch (IOException e) {
            e.printStackTrace();
        }


        return authToken;
    }

    private static String makeHttpRequestSpotify(URL requestURL, String authToken) throws IOException{
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

            // if the data is fetched
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

            // if the data is fetched
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
