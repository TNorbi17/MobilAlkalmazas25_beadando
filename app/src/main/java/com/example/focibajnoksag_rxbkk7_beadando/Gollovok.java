package com.example.focibajnoksag_rxbkk7_beadando;

public class Gollovok {
    private String id;
    private String name;
    private String team;
    private int goals;
    private int votes;

    public Gollovok() {
    }

    public Gollovok(int goals, String team, String name) {
        this.goals = goals;
        this.team = team;
        this.name = name;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getTeam() { return team; }
    public void setTeam(String team) { this.team = team; }
    public int getGoals() { return goals; }
    public void setGoals(int goals) { this.goals = goals; }
    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }
}
