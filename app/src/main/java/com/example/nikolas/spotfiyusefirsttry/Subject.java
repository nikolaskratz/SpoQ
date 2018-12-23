package com.example.nikolas.spotfiyusefirsttry;

// interface for observer pattern
public interface Subject {
    public void register(Observer observer);
    public void unregister(Observer observer);
    public void notifyObservers();
}
