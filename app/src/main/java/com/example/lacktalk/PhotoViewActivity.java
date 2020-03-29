package com.example.lacktalk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.github.chrisbanes.photoview.PhotoView;

public class PhotoViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_view);

        Intent intent = getIntent();
        PhotoView photoView = findViewById(R.id.phothoview);

        if(intent.getStringExtra("picture") !=null && !intent.getStringExtra("picture").isEmpty())
            photoView.setImageBitmap(Intro.getBitmapFromString(intent.getStringExtra("picture")));
        else
            Log.d("asd","tlqkf");


    }
}
