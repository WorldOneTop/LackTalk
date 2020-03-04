package com.example.lacktalk;

import android.widget.ImageView;
import android.widget.TextView;

public class ItemList {
    private String imagePath;
    private String name;
    private String message;

    public ItemList(String a ,String b ,String c){
        imagePath = a;
        name = b;
        message =c;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String time) {
        this.message = time;
    }
}
