package com.pthien.project_ciy.Model;

public class Model_Notification {
    String idnoti, idp, timeNoti, content, uid, uname, uimg;

    public Model_Notification(String idnoti, String idp, String timeNoti, String content, String uid, String uname, String uimg) {
        this.idnoti = idnoti;
        this.idp = idp;
        this.timeNoti = timeNoti;
        this.content = content;
        this.uid = uid;
        this.uname = uname;
        this.uimg = uimg;
    }

    public String getIdnoti() {
        return idnoti;
    }

    public void setIdnoti(String idnoti) {
        this.idnoti = idnoti;
    }

    public String getIdp() {
        return idp;
    }

    public void setIdp(String idp) {
        this.idp = idp;
    }

    public String getTimeNoti() {
        return timeNoti;
    }

    public void setTimeNoti(String timeNoti) {
        this.timeNoti = timeNoti;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getUimg() {
        return uimg;
    }

    public void setUimg(String uimg) {
        this.uimg = uimg;
    }

    public Model_Notification(){

    }
}
