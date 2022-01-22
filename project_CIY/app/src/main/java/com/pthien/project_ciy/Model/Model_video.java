package com.pthien.project_ciy.Model;

public class Model_video {
    String pCate, pDesc, pvd, pTime, pTitle, pId, uid, uDp, uName;

    public Model_video(String pCate, String pDesc, String pvd, String pTime, String pTitle, String pId, String uid, String uDp, String uName) {
        this.pCate = pCate;
        this.pDesc = pDesc;
        this.pvd = pvd;
        this.pTime = pTime;
        this.pTitle = pTitle;
        this.pId = pId;
        this.uid = uid;
        this.uDp = uDp;
        this.uName = uName;
    }

    public String getpCate() {
        return pCate;
    }

    public void setpCate(String pCate) {
        this.pCate = pCate;
    }

    public String getpDesc() {
        return pDesc;
    }

    public void setpDesc(String pDesc) {
        this.pDesc = pDesc;
    }

    public String getPvd() {
        return pvd;
    }

    public void setPvd(String pvd) {
        this.pvd = pvd;
    }

    public String getpTime() {
        return pTime;
    }

    public void setpTime(String pTime) {
        this.pTime = pTime;
    }

    public String getpTitle() {
        return pTitle;
    }

    public void setpTitle(String pTitle) {
        this.pTitle = pTitle;
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getuDp() {
        return uDp;
    }

    public void setuDp(String uDp) {
        this.uDp = uDp;
    }

    public String getuName() {
        return uName;
    }

    public void setuName(String uName) {
        this.uName = uName;
    }

    public Model_video(){

    }
}
