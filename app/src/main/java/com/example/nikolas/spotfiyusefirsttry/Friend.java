package com.example.nikolas.spotfiyusefirsttry;

public class Friend {

    private String userID;
    private String nickname;


    public Friend(String userID, String nickname) {
        this.userID = userID;
        this.nickname = nickname;
    }
    public Friend(){}

    public String getUserID() {
        return userID;
    }

    public String getNickname() {
        return nickname;
    }
}
