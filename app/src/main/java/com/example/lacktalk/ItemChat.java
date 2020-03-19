package com.example.lacktalk;

import android.util.Log;

import java.io.Serializable;
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


class ItemList implements Serializable {//ro.room_picture, ro.room_name , re.recode_text, ro.room_num_server, re.recode_date, SUM(re.recode_read)
    private String imagePath;   //프사    채팅방 사진ro.room_picture
    private String name;        //닉넴    채팅방 이름ro.room_name
    private String message;     //상메    마지막 한 말re.recode_text
    private String initial;     //검색용 namd의 이니셜
    private int primary_num;    //db PK   db server Numro.room_num_server
    private String id;          //db id   마지막 사람이 말한 시간re.recode_date

    private int unread;         //        안읽은 양SUM(re.recode_read)
    public ItemList(String a ,String b ,String c,int pnum, String idd){
        imagePath = a;
        name = b;
        message =c;
        initial = Intro.getInitialSound(name);
        primary_num = pnum;
        id = idd;
    }

    public int getUnread() {
        return unread;
    }

    public void setUnread(int unread) {
        this.unread = unread;
    }

    public ItemList initRoom(int unreadd){
        unread = unreadd; return this;
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
