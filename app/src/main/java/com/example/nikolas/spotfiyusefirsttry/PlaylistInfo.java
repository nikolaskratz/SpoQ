package com.example.nikolas.spotfiyusefirsttry;

import java.util.ArrayList;
import java.util.List;

public class PlaylistInfo {

    private List<String> playlistID = new ArrayList<>();
    private List<String> playlistUser = new ArrayList<>();
    private List<String> playlistName = new ArrayList<>();

    public PlaylistInfo(String playlistID, String playlistUser, String playlistName) {
//        this.playlistID = playlistID;
//        this.playlistUser = playlistUser;
//        this.playlistName = playlistName;
    }

    public PlaylistInfo(){
        playlistID.add("37i9dQZF1DX0XUsuxWHRQd");
        playlistID.add("37i9dQZF1DXb57FjYWz00c");
        playlistID.add("37i9dQZF1DWTJ7xPn4vNaz");
        playlistID.add("37i9dQZF1DXdLtD0qszB1w");
        playlistID.add("3rT1Fcx6X9fhyMof5Za2NN");
        playlistID.add("26ugmIxhiUMXo7ntOTUA5c");
        playlistID.add("37i9dQZF1DXbITWG1ZJKYt");
        playlistID.add("37i9dQZEVXbJiZcmkrIHGU");

        playlistName.add("RapCaviar");
        playlistName.add("80s Hits");
        playlistName.add("All out 70s");
        playlistName.add("This Is The Beatles");
        playlistName.add("Schlager by Niko");
        playlistName.add("Laxen by Niko");
        playlistName.add("Jazz Classics");
        playlistName.add("Top 50 Deutschland");

        playlistUser.add("spotify");
        playlistUser.add("spotify");
        playlistUser.add("spotify");
        playlistUser.add("spotify");
        playlistUser.add("1146468343");
        playlistUser.add("1146468343");
        playlistUser.add("spotify");
        playlistUser.add("spotifycharts");
    }

    public List<String> getPlaylistID() {
        return playlistID;
    }

    public List<String> getPlaylistUser() {
        return playlistUser;
    }

    public List<String> getPlaylistName() {
        return playlistName;
    }
}
