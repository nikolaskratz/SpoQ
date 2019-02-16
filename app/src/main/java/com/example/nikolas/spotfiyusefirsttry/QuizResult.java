package com.example.nikolas.spotfiyusefirsttry;

public class QuizResult {

    int pointsP1;
    int pointsP2;
    String quizID;
    String p1Name;
    String p2Name;


    public QuizResult(int pointsP1,String quizID,String p1Name, String p2Name) {
        this.pointsP1 = pointsP1;
        this.quizID=quizID;
        this.p1Name=p1Name;
        this.p2Name=p2Name;
    }
    public QuizResult(){};

    public void setPointsP2(int pointsP2) {
        this.pointsP2 = pointsP2;
    }

    public int getPointsP1() {
        return pointsP1;
    }

    public int getPointsP2() {
        return pointsP2;
    }

    public String getQuizID(){
        return quizID;
    }

    public String getP1Name() {
        return p1Name;
    }

    public String getP2Name() {
        return p2Name;
    }
}
