package com.pthien.project_ciy.Model;

public class Model_group {

    String idGr, titleGr, descGr, idLeader, coverGr, chedo;

    public Model_group(String idGr, String titleGr, String descGr, String idLeader, String coverGr, String chedo) {
        this.idGr = idGr;
        this.titleGr = titleGr;
        this.descGr = descGr;
        this.idLeader = idLeader;
        this.coverGr = coverGr;
        this.chedo = chedo;
    }

    public String getIdGr() {
        return idGr;
    }

    public void setIdGr(String idGr) {
        this.idGr = idGr;
    }

    public String getTitleGr() {
        return titleGr;
    }

    public void setTitleGr(String titleGr) {
        this.titleGr = titleGr;
    }

    public String getDescGr() {
        return descGr;
    }

    public void setDescGr(String descGr) {
        this.descGr = descGr;
    }

    public String getIdLeader() {
        return idLeader;
    }

    public void setIdLeader(String idLeader) {
        this.idLeader = idLeader;
    }

    public String getCoverGr() {
        return coverGr;
    }

    public void setCoverGr(String coverGr) {
        this.coverGr = coverGr;
    }

    public String getChedo() {
        return chedo;
    }

    public void setChedo(String chedo) {
        this.chedo = chedo;
    }

    public Model_group(){

    }
}