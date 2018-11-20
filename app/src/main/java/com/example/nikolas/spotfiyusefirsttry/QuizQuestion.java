package com.example.nikolas.spotfiyusefirsttry;

public class QuizQuestion {

    private String trackName;
    private String trackArtist;
    private String playlistName;
    private String playlistID;
    private String trackID;
    private boolean correct;

    public QuizQuestion(String trackName) {
        this.trackName = trackName;
    }

    public QuizQuestion(String trackName, String trackID, boolean correct) {
        this.trackName = trackName;
        this.trackID = trackID;
        this.correct = correct;
    }

    public String getTrackName() {
        return trackName;
    }

    public String getTrackArtist() {
        return trackArtist;
    }

    public String getPlaylistName() {
        return playlistName;
    }

    public String getPlaylistID() {
        return playlistID;
    }

    public String getTrackID() {
        return trackID;
    }

    public boolean isCorrect() {
        return correct;
    }
}
