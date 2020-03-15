package com.example.lacktalk;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
    public static AdapterList adapterUser;
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
        //변수 설정
        listView = findViewById(R.id.add_listview);
        scrollView = findViewById(R.id.add_scrollview);
        editText = findViewById(R.id.add_editText);

        //디폴트 설정
        if(!isFriend)
            init_isList();

    }
    public void init_isList(){
        listView.setAdapter(adapterUser);
        listView.setFilterText("");

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });
    }
}
