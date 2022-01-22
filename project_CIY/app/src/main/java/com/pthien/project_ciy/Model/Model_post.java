package com.pthien.project_ciy.Model;

public class Model_post {
    String pCate, pDesc, pImage, pDetails, pTime, pTitle, pEmail, uid, pId, uDp, uName;

    public Model_post(String pCate, String pDesc, String pImage, String pDetails, String pTime, String pTitle, String pEmail, String uid, String pId, String uDp, String uName) {
        this.pCate = pCate;
        this.pDesc = pDesc;
        this.pImage = pImage;
        this.pDetails = pDetails;
        this.pTime = pTime;
        this.pTitle = pTitle;
        this.pEmail = pEmail;
        this.uid = uid;
        this.pId = pId;
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

    public String getpImage() {
        return pImage;
    }

    public void setpImage(String pImage) {
        this.pImage = pImage;
    }

    public String getpDetails() {
        return pDetails;
    }

    public void setpDetails(String pDetails) {
        this.pDetails = pDetails;
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

    public String getpEmail() {
        return pEmail;
    }

    public void setpEmail(String pEmail) {
        this.pEmail = pEmail;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
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

    public Model_post(){

    }
}
