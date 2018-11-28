package com.example.nikolas.spotfiyusefirsttry;

import java.util.ArrayList;
import java.util.List;

public class QuizGame {

    List<Quiz> quizList= new ArrayList<>();

    public void addQuiz(Quiz quiz){
        quizList.add(quiz);
    }

    public List<Quiz> getQuizList() {
        return quizList;
    }
}
