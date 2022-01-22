package com.pthien.project_ciy.Model;

public class Model_Repcomment {
    String repcmtId, repcmtTime, cmtId, repComment, userid, uid, uname;

    public Model_Repcomment(String repcmtId, String repcmtTime, String cmtId, String repComment, String userid, String uid, String uname) {
        this.repcmtId = repcmtId;
        this.repcmtTime = repcmtTime;
        this.cmtId = cmtId;
        this.repComment = repComment;
        this.userid = userid;
        this.uid = uid;
        this.uname = uname;
    }

    public String getRepcmtId() {
        return repcmtId;
    }

    public void setRepcmtId(String repcmtId) {
        this.repcmtId = repcmtId;
    }

    public String getRepcmtTime() {
        return repcmtTime;
    }

    public void setRepcmtTime(String repcmtTime) {
        this.repcmtTime = repcmtTime;
    }

    public String getCmtId() {
        return cmtId;
    }

    public void setCmtId(String cmtId) {
        this.cmtId = cmtId;
    }

    public String getRepComment() {
        return repComment;
    }

    public void setRepComment(String repComment) {
        this.repComment = repComment;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
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

    public Model_Repcomment(){

    }
}
