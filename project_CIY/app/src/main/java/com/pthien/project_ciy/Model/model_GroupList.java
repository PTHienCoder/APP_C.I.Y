package com.pthien.project_ciy.Model;

public class model_GroupList {
    String check, idGr, iduser;

    public model_GroupList(String check, String idGr, String iduser) {
        this.check = check;
        this.idGr = idGr;
        this.iduser = iduser;
    }

    public String getCheck() {
        return check;
    }

    public void setCheck(String check) {
        this.check = check;
    }

    public String getIdGr() {
        return idGr;
    }

    public void setIdGr(String idGr) {
        this.idGr = idGr;
    }

    public String getIduser() {
        return iduser;
    }

    public void setIduser(String iduser) {
        this.iduser = iduser;
    }

    public model_GroupList(){

    }

}
