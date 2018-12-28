package com.example.nikolas.spotfiyusefirsttry;

import java.util.ArrayList;
import java.util.List;

public class UserInfo {
    private String nickname;
    private int points;
    private String profileImg;
    private String email;
    private ArrayList<Friend> friends;
    private ArrayList<Object> games;
    private List<QuizResult> results;



    public UserInfo(String nickname, int points, String profileImg, String email) {
        this.nickname = nickname;
        this.points = points;
        this.profileImg = profileImg;
        this.email=email;
        this.friends = new ArrayList<>();
        this.games = new ArrayList<>();

        //only for debuggin purpose i am adding hardcoded friends here, later it will be via the
        // fragment
        friends.add(new Friend("5CJQUtHyefdGeGKTKdpZoNEL04G2","a1"));
        friends.add(new Friend("Hc4Xpv88wYQWG3QoSqo0qjpov4r2","a2"));
        friends.add(new Friend("0test0","t0-dont use"));

        this.results = new ArrayList<>();
    }

    public UserInfo(){
        this.results = new ArrayList<>();
        this.friends = new ArrayList<>();
        this.games = new ArrayList<>();
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

    public ArrayList<Friend> getFriends() {
        return friends;
    }

//    public List<String> getGames() {
//        return games;
//    }

    public List<QuizResult> getResults() {
        return results;
    }
}
