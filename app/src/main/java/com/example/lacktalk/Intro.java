package com.example.lacktalk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import java.io.File;

public class Intro extends AppCompatActivity {
    //권한체크 폴더체크 디비체크

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

//        File file = new File(this.getFilesDir(), "image");
//        if( !file.exists() )
//            file.mkdir();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(getApplicationContext(),ChatRoom.class));
                finish();
            }
        }, 1200);
    }
}
