package com.example.nikolas.spotfiyusefirsttry;

import java.util.ArrayList;
import java.util.List;

public class Quiz {
    private int randomButtonNumber;
    private List<QuizQuestion> questionList = new ArrayList<>();

    public Quiz(List<QuizQuestion> questionList) {
        this.randomButtonNumber = randomButtonNumber;
        this.questionList = questionList;
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
}
