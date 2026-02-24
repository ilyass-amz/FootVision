package com.exemple.footvision.Models;

public class Team {
    private int id;
    private String name;
    private String shortName;
    private String tla;
    private String crest;

    public int getId() { return id; }
    public String getName() { return name; }
    public String getShortName() { return shortName; }
    public String getTla() { return tla; }
    public String getCrest() { return crest; }

    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setShortName(String shortName) { this.shortName = shortName; }
    public void setTla(String tla) { this.tla = tla; }
    public void setCrest(String crest) { this.crest = crest; }
}