package com.pthien.project_ciy.Model;

public class Model_repQs {
    String repId, repTime, qId, qComment, uid, uimage, uname;

    public Model_repQs(String repId, String repTime, String qId, String qComment, String uid, String uimage, String uname) {
        this.repId = repId;
        this.repTime = repTime;
        this.qId = qId;
        this.qComment = qComment;
        this.uid = uid;
        this.uimage = uimage;
        this.uname = uname;
    }

    public String getRepId() {
        return repId;
    }

    public void setRepId(String repId) {
        this.repId = repId;
    }

    public String getRepTime() {
        return repTime;
    }

    public void setRepTime(String repTime) {
        this.repTime = repTime;
    }

    public String getqId() {
        return qId;
    }

    public void setqId(String qId) {
        this.qId = qId;
    }

    public String getqComment() {
        return qComment;
    }

    public void setqComment(String qComment) {
        this.qComment = qComment;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUimage() {
        return uimage;
    }

    public void setUimage(String uimage) {
        this.uimage = uimage;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public Model_repQs(){

    }
}
