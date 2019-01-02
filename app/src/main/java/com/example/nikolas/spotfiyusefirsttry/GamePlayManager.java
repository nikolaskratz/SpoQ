package com.example.nikolas.spotfiyusefirsttry;

import java.util.List;

import kaaes.spotify.webapi.android.models.PlaylistTrack;

public interface GamePlayManager {


    public void setPlaylist(List<PlaylistTrack> playlistTracks);

    public void proceed();

}
