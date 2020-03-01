package com.example.lacktalk;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class ChatRoom extends AppCompatActivity {
    Intent intent;
    Random random;
    ImageView imageView_back,imageView_search,imageView_menu;
    TextView textView_name;
    AdapterChat adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);
        init();
        imageView_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout) ;
                if (!drawer.isDrawerOpen(Gravity.RIGHT))
                    drawer.openDrawer(Gravity.RIGHT) ;
                else
                    drawer.closeDrawer(Gravity.RIGHT) ;
            }
        });

        random = new Random();
        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.addItem("dfault","Test",randomStr(),new SimpleDateFormat("yyyy/MM/dd/HH/ss").format(new Date()),random.nextBoolean());
                adapter.notifyDataSetChanged();
            }
        });


    }
    public String randomStr(){
        int a = random.nextInt(50);
        String result = "";
        for(int i=0;i<a;i++){
            result += (char)((Math.random() * 26) + 97);
        }
        return result;
    }
    public void init(){
        View include_layout = findViewById(R.id.include_actionbar);
        include_layout.findViewById(R.id.action_bar_chatRoom).setVisibility(View.VISIBLE);
        include_layout.findViewById(R.id.action_bar_friend).setVisibility(View.GONE);
        imageView_back= include_layout.findViewById(R.id.icon_back);
        imageView_search = include_layout.findViewById(R.id.icon_search_room);
        imageView_menu = include_layout.findViewById(R.id.icon_menu);
        textView_name = include_layout.findViewById(R.id.icon_name);

        adapter = new AdapterChat();
        ((ListView)findViewById(R.id.listView)).setAdapter(adapter);


    }
}
