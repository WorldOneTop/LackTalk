package com.example.lacktalk;

import java.util.Date;

public class ItemChat {

    private String imagePath;
    private String name;
    private String text;
    private String time;
    private boolean isme;

    public ItemChat(String a,String b,String c,String d,boolean e){
        imagePath = a; name = b; text = c; time = d; isme = e;
    }
    public boolean getIsme(){
        return isme;
    }
    public void setIsme(boolean isme){
        this.isme = isme;
    }
    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


}


class ItemList {
    private String imagePath;
    private String name;
    private String message;
    private String initial;
    private int primary_num;
    private String id;

    public ItemList(String a ,String b ,String c,int pnum,String idd){
        imagePath = a;
        name = b;
        message =c;
        initial = Intro.getInitialSound(name);
        primary_num = pnum;
        id = idd;
    }
    public int getPrimary_num(){ return primary_num;}
    public String getId(){return id;}

    public String getInitial() {return initial;}
    public void setInitial(String initial) {this.initial = initial;}

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String time) {
        this.message = time;
    }
}
