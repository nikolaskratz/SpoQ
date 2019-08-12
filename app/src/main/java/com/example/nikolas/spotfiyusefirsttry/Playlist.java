package com.example.nikolas.spotfiyusefirsttry;

import android.graphics.Bitmap;

public class Playlist {
    private String name;
    private Bitmap cover;
    private String id;

    public Playlist(String name, Bitmap cover, String id) {
        this.name = name;
        this.cover = cover;
        this.id = id;
    }

    public String getPlaylistName() {
        return name;
    }

    public Bitmap getPlaylistCover() {
        return cover;
    }

    public String getPlaylistId() {
        return id;
    }

}
