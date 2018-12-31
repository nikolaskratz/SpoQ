package com.example.nikolas.spotfiyusefirsttry;

public class QuizResult {

    int pointsP1;
    int pointsP2;

    public QuizResult(int pointsP1) {
        this.pointsP1 = pointsP1;
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
}
