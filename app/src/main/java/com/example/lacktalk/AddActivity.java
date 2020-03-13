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
    public static AdapterList adapterList;
    public static AdapterChat adapterChat;
    public static boolean isFriend;
    private ListView listView;
    private HorizontalScrollView scrollView;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        init();
    }
    public void init(){
        listView = findViewById(R.id.add_listview);
        scrollView = findViewById(R.id.add_scrollview);
        editText = findViewById(R.id.add_editText);

//        listView.setAdapter(isFriend ? adapterList);
//        listView.setFilterText("");
//        listView.clearTextFilter();
    }
}
