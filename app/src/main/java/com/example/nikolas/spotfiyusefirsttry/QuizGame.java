package com.example.nikolas.spotfiyusefirsttry;

import java.util.ArrayList;
import java.util.List;

public class QuizGame {

    List<Quiz> quizList= new ArrayList<>();
    private QuizResult quizResult;

    public void addQuiz(Quiz quiz){
        quizList.add(quiz);
    }

    public List<Quiz> getQuizList() {
        return quizList;
    }

    public QuizResult getQuizResult() {
        return quizResult;
    }

    public void setQuizResult(QuizResult quizResult) {
        this.quizResult = quizResult;
    }
}
