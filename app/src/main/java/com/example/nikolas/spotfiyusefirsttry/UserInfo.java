package com.example.nikolas.spotfiyusefirsttry;

import java.util.ArrayList;
import java.util.List;

public class UserInfo {
    private String nickname;
    private int points;
    private String profileImg;
    private String email;
    private ArrayList<Friend> friends;
    private List<String> games;
    private List<QuizResult> results;



    public UserInfo(String nickname, int points, String profileImg, String email) {
        this.nickname = nickname;
        this.points = points;
        this.profileImg = profileImg;
        this.email=email;
        this.friends = new ArrayList<>();

        //only for debuggin purpose i am adding hardcoded friends here, later it will be via the
        // fragment
        friends.add(new Friend("Dd4D3vktbGMtl3oTBV1HDrG4Fc92","t1"));
        friends.add(new Friend("TYxUsxT3FBMeIP9ipD2FULLi7yV2","t0"));
        friends.add(new Friend("00000test","t0-dont use"));

        this.games = new ArrayList<>();
        this.results = new ArrayList<>();
    }
    public UserInfo() {
        this.games = new ArrayList<String>();
        this.results = new ArrayList<QuizResult>();
        this.friends = new ArrayList<Friend>();
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

    public List<String> getGames() {
        return games;
    }

    public List<QuizResult> getResults() {
        return results;
    }
}
