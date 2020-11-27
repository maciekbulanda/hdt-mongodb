package com.maciekbulanda.herokutest.dto;

public class InfoDto {
    private String name;
    private String version;
    private String creator;
    public InfoDto() {
        this.name = "Heroku With MongoDB Test App";
        this.version = "1";
        this.creator = "Maciej Bulanda";
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }
}
