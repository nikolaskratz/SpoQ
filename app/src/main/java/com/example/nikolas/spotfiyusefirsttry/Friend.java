package com.example.nikolas.spotfiyusefirsttry;

public class Friend {

    private String userID;
    private String nickname;
    private String profilePicture;

    public Friend(String userID, String nickname, String profilePicture) {
        this.userID = userID;
        this.nickname = nickname;
        this.profilePicture = profilePicture;
    }

    public Friend(){}

    public String getUserID() {
        return userID;
    }

    public String getNickname() {
        return nickname;
    }

    public String getProfilePicture() {
        return profilePicture;
    }
}
