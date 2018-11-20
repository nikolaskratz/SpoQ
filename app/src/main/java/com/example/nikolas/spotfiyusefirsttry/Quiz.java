package com.example.nikolas.spotfiyusefirsttry;

public class Quiz {
    private QuizQuestion track1;
    private QuizQuestion track2;
    private QuizQuestion track3;
    private QuizQuestion track4Correct;

    public Quiz(QuizQuestion track1, QuizQuestion track2, QuizQuestion track3, QuizQuestion track4Correct) {
        this.track1 = track1;
        this.track2 = track2;
        this.track3 = track3;
        this.track4Correct = track4Correct;
    }

    public QuizQuestion getTrack1() {
        return track1;
    }

    public void setTrack1(QuizQuestion track1) {
        this.track1 = track1;
    }

    public QuizQuestion getTrack2() {
        return track2;
    }

    public void setTrack2(QuizQuestion track2) {
        this.track2 = track2;
    }

    public QuizQuestion getTrack3() {
        return track3;
    }

    public void setTrack3(QuizQuestion track3) {
        this.track3 = track3;
    }

    public QuizQuestion getTrack4Correct() {
        return track4Correct;
    }

    public void setTrack4Correct(QuizQuestion track4Correct) {
        this.track4Correct = track4Correct;
    }



}
