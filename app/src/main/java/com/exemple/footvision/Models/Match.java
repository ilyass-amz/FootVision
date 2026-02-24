package com.exemple.footvision.Models;

public class Match {
    private int id;
    private Team homeTeam;
    private Team awayTeam;
    private Score score;
    private String utcDate;

    public int getId() { return id; }
    public String getHomeTeam() { return homeTeam.name; }
    public String getAwayTeam() { return awayTeam.name; }
    public String getDate() { return utcDate; }
    public String getScore() {
        if(score != null && score.fullTime != null) {
            return score.fullTime.home + " - " + score.fullTime.away;
        }
        return "N/A";
    }

    static class Team {
        String name;
    }

    static class Score {
        FullTime fullTime;
        static class FullTime {
            int home;
            int away;
        }
    }
}