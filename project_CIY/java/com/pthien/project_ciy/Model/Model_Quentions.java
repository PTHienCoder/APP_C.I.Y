package com.pthien.project_ciy.Model;

public class Model_Quentions {
    String qId,qCate, qTime, qTitle, qDesc, qImage, uimg, uname, uid;

    public Model_Quentions(String qId, String qCate, String qTime, String qTitle, String qDesc, String qImage, String uimg, String uname, String uid) {
        this.qId = qId;
        this.qCate = qCate;
        this.qTime = qTime;
        this.qTitle = qTitle;
        this.qDesc = qDesc;
        this.qImage = qImage;
        this.uimg = uimg;
        this.uname = uname;
        this.uid = uid;
    }

    public String getqId() {
        return qId;
    }

    public void setqId(String qId) {
        this.qId = qId;
    }

    public String getqCate() {
        return qCate;
    }

    public void setqCate(String qCate) {
        this.qCate = qCate;
    }

    public String getqTime() {
        return qTime;
    }

    public void setqTime(String qTime) {
        this.qTime = qTime;
    }

    public String getqTitle() {
        return qTitle;
    }

    public void setqTitle(String qTitle) {
        this.qTitle = qTitle;
    }

    public String getqDesc() {
        return qDesc;
    }

    public void setqDesc(String qDesc) {
        this.qDesc = qDesc;
    }

    public String getqImage() {
        return qImage;
    }

    public void setqImage(String qImage) {
        this.qImage = qImage;
    }

    public String getUimg() {
        return uimg;
    }

    public void setUimg(String uimg) {
        this.uimg = uimg;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Model_Quentions(){
        
    }
}
