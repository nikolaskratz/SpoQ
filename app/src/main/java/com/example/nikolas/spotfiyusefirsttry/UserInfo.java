package com.example.nikolas.spotfiyusefirsttry;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserInfo {
    private String nickname;
    private int points;
    private String profileImg;
    private String email;
    private HashMap<String,Friend> friends;
    private ArrayList<Object> games;
    private Map<String,QuizResult> results;



    public UserInfo(String nickname, int points, String profileImg, String email) {
        this.nickname = nickname;
        this.points = points;
        this.profileImg = profileImg;
        this.email=email;

        this.friends = new HashMap<String,Friend>();
        this.games = new ArrayList<>();
        this.results = new HashMap<String,QuizResult>();
    }

    public UserInfo(){
        this.results = new HashMap<String,QuizResult>();
        this.friends = new HashMap<String,Friend>();
        this.games = new ArrayList<>();
    }

    public HashMap<String, Friend> getFriends() {
        return friends;
    }

    public String getNickname() {
        return nickname;
    }

    public int getPoints()   {
        return points;
    }

    public String getProfileImg() {
        return profileImg;
    }

//    public ArrayList<Friend> getFriends() {
//        return friends;
//    }

    //    public List<String> getGames() {
    //        return games;
    //    }

    public Map<String,QuizResult> getResults() {
        return results;
    }
}