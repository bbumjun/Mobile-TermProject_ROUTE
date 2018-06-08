package com.termproject.route.route;

import android.net.Uri;
import android.support.v4.view.ViewPager;

import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class thePost {
    private String name;
    private String route;
    private String time;
    private List<String> ImageUrl = new ArrayList();
    public thePost()
    {}
    public thePost(String route,String name,String time,List<String> ImageUrl){

        this.route=route;

        this.name=name;

        this.time=time;

        this.ImageUrl=ImageUrl;
    }

    public String getRoute(){
        return route;
    }
    public void setRoute(String route){this.route=route;}
    public String getName(){
        return name;
    }
    public void setName(String name){this.name=name;}
    public String getTime(){
        return time;
    }
    public void setTime(String time){this.time=time;}
    public void setImageUrl(List<String> ImageUrl){
        this.ImageUrl=ImageUrl;
    }
    public List<String> getImageUrl(){

        return ImageUrl;
    }
}