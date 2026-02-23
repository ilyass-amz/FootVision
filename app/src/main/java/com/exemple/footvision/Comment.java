package com.exemple.footvision;

public class Comment {
    private int id;
    private String matchId;
    private String user;
    private String comment;

    public Comment(int id, String matchId, String user, String comment){
        this.id = id;
        this.matchId = matchId;
        this.user = user;
        this.comment = comment;
    }

    public int getId() { return id; }
    public String getMatchId() { return matchId; }
    public String getUser() { return user; }
    public String getComment() { return comment; }

    public void setComment(String comment) { this.comment = comment; }
}