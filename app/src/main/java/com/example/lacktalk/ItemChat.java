package com.example.lacktalk;

import android.util.Log;

import java.io.Serializable;
import java.util.Date;

public class ItemChat {

    private String imagePath;//이미지경로
    private String name;     //쓴사람이름
    private String text;     //내용
    private String time;     //쓴 시각
    private boolean isme;    //내가 쓴건지
    private int amount;      //얼마나 안읽었는지
    private int type;        //쓴 타입이 뭔지
    private String id;       //쓴사람 아이디
    private int pNum;        // 고유번호(recode_numserver);
    public ItemChat(String a,String b,String c,String d,boolean e,int f,int g,String idd,int p){
        imagePath = a; name = b; text = c; time = d; isme = e; amount = f; type = g; id =idd;pNum = p;
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

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getpNum() {
        return pNum;
    }

    public void setpNum(int pNum) {
        this.pNum = pNum;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}


class ItemList implements Serializable {//ro.room_picture, ro.room_name , re.recode_text, ro.room_num_server, re.recode_date, SUM(re.recode_read), ro.room_user
    private String imagePath;   //프사    채팅방 사진ro.room_picture
    private String name;        //닉넴    채팅방 이름ro.room_name
    private String message;     //상메    마지막 한 말re.recode_text
    private String initial;     //검색용 namd의 이니셜
    private int primary_num;    //db PK   db server Numro.room_num_server
    private String id;          //db id   마지막 사람이 말한 시간re.recode_date
    private int unread;         //        안읽은 양SUM(re.recode_read)
    private int amount;         //         방안의 사람이 총 몇명인지
    private String users;       //          방안에 참가한 유저의 아이디 들
    public ItemList(String a ,String b ,String c,int pnum, String idd){
        imagePath = a;
        name = b;
        message =c;
        initial = Intro.getInitialSound(name);
        primary_num = pnum;
        id = idd;
    }

    public ItemList initRoom(int unreadd, int amountt,String userss){
        unread = unreadd; amount = amountt;users = userss;return this;
    }
    public int getUnread() {
        return unread;
    }

    public void setUnread(int unread) {
        this.unread = unread;
    }

    public int getPrimary_num(){ return primary_num;}
    public String getId(){return id;}
    public void setId(String id){this.id = id;}
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

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getUsers() {
        return users;
    }

    public void setUsers(String users) {
        this.users = users;
    }
}
