package com.pthien.project_ciy.Model;

public class Model_Comments {
    String cmtId, cmtTime, pId, pComment, uid, uimage, uname;

    public Model_Comments(String cmtId, String cmtTime, String pId, String pComment, String uid, String uimage, String uname) {
        this.cmtId = cmtId;
        this.cmtTime = cmtTime;
        this.pId = pId;
        this.pComment = pComment;
        this.uid = uid;
        this.uimage = uimage;
        this.uname = uname;
    }

    public String getCmtId() {
        return cmtId;
    }

    public void setCmtId(String cmtId) {
        this.cmtId = cmtId;
    }

    public String getCmtTime() {
        return cmtTime;
    }

    public void setCmtTime(String cmtTime) {
        this.cmtTime = cmtTime;
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getpComment() {
        return pComment;
    }

    public void setpComment(String pComment) {
        this.pComment = pComment;
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

    public Model_Comments(){}

}
