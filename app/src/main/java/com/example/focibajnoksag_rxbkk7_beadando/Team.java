package com.example.focibajnoksag_rxbkk7_beadando;

public class Team {



    private String name;
    private int wins;
    private int draws;
    private int losses;
    private int points;

    public Team(String name, int wins, int draws, int losses, int points) {
        this.name = name;
        this.wins = wins;
        this.draws = draws;
        this.losses = losses;
        this.points = points;
    }

    public Team() {

    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getLosses() {
        return losses;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }

    public int getDraws() {
        return draws;
    }

    public void setDraws(int draws) {
        this.draws = draws;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }
}
