package com.pthien.project_ciy.Model;

public class BannerModel {
    public BannerModel(){

    }
    private int banner_img;

    public int getBanner_img() {
        return banner_img;
    }

    public void setBanner_img(int banner_img) {
        this.banner_img = banner_img;
    }

    public BannerModel(int banner_img) {
        this.banner_img = banner_img;
    }
}
