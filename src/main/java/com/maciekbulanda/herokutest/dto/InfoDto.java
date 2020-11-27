package com.maciekbulanda.herokutest.dto;

public class InfoDto {
    private String version;
    public InfoDto() {
        this.version = "1";
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
