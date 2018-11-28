package com.example.nikolas.spotfiyusefirsttry;

import java.util.ArrayList;
import java.util.List;

public class Quiz {
    private int randomButtonNumber;
    private List<QuizQuestion> questionList = new ArrayList<>();
    private String playlistID;

    public Quiz(List<QuizQuestion> questionList,String playlistID) {
        this.randomButtonNumber = randomButtonNumber;
        this.questionList = questionList;
        this.playlistID=playlistID;
    }

    public Quiz() {
    }

    public int getRandomButtonNumber() {
        return randomButtonNumber;
    }

    public void setRandomButtonNumber(int randomButtonNumber) {
        this.randomButtonNumber = randomButtonNumber;
    }

    public List<QuizQuestion> getQuestionList() {
        return questionList;
    }

    public void setQuestionList(List<QuizQuestion> questionList) {
        this.questionList = questionList;
    }

    public String getPlaylistID() {
        return playlistID;
    }
}
