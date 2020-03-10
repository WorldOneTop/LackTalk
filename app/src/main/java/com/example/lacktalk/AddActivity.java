package com.example.lacktalk;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AddActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        final HorizontalScrollView mScrollView = findViewById(R.id.add_scrollview);
        final LinearLayout linearLayout = findViewById(R.id.add_linearLayout);

        TextView textView = new TextView(this);
        textView.setText("aaaaaaaaaaaaaa");
        linearLayout.addView(textView);
//        linearLayout.addView(textView);
        final ImageView imageView = findViewById(R.id.imageView2);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageView imageView1 = new ImageView(AddActivity.this);
                imageView1.setImageResource(R.drawable.defaultimg);
                Log.d("asd","aaaaaaa");
                linearLayout.addView(imageView1);
            }
        });
        AdapterChat adapter = new AdapterChat();
        ((ListView)findViewById(R.id.listview1)).setAdapter(adapter);
        adapter.addItem("dfault", "Test ID", "asdads", new SimpleDateFormat("yyyy/MM/dd/HH/mm").format(new Date()), true);
        adapter.addItem("dfault", "Test ID", "asdads", new SimpleDateFormat("yyyy/MM/dd/HH/mm").format(new Date()), true);
        adapter.addItem("dfault", "Test ID", "asdads", new SimpleDateFormat("yyyy/MM/dd/HH/mm").format(new Date()), true);
        adapter.addItem("dfault", "Test ID", "asdads", new SimpleDateFormat("yyyy/MM/dd/HH/mm").format(new Date()), true);
        adapter.addItem("dfault", "Test ID", "asdads", new SimpleDateFormat("yyyy/MM/dd/HH/mm").format(new Date()), true);
        adapter.addItem("dfault", "Test ID", "asdads", new SimpleDateFormat("yyyy/MM/dd/HH/mm").format(new Date()), true);
        EditText editText = findViewById(R.id.add_editText);
    }
}
