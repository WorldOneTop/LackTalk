package com.example.lacktalk;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class ChatList extends AppCompatActivity implements View.OnClickListener{
    Intent intent;
    Handler handler;
    ListView listView;
    ImageView actionbar_search,actionbar_add,underbar_friend,underbar_list,underbar_setting;
    TextView actionbar_main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        init();

    }
    public void init(){
        //채팅방 위에 액션바(뒤로가기 이름 등등 ) 뷰 선언 및 보이게
        View include_layout = findViewById(R.id.include_actionbar);
        include_layout.findViewById(R.id.action_bar_chatRoom).setVisibility(View.GONE);
        include_layout.findViewById(R.id.action_bar_friend).setVisibility(View.VISIBLE);
        actionbar_search = include_layout.findViewById(R.id.icon_search);
        actionbar_add = include_layout.findViewById(R.id.icon_add);
        actionbar_main = include_layout.findViewById(R.id.action_bar_main);

        //채팅방 아래 ( edittext, +버튼 등등 ) 뷰 선언 및 보이게
        View include_underbar = findViewById(R.id.include_underbar);
        include_underbar.findViewById(R.id.underbar_Room).setVisibility(View.GONE);
        include_underbar.findViewById(R.id.underbar_List).setVisibility(View.VISIBLE);
        underbar_friend = include_underbar.findViewById(R.id.icon_friend);
        underbar_list = include_underbar.findViewById(R.id.icon_chatList);
        underbar_setting = include_underbar.findViewById(R.id.icon_setting_underbar);

        init_ClickListener();
    }
    public void init_ClickListener(){
        //액션바 리스너
        actionbar_search.setOnClickListener(this);
        actionbar_add.setOnClickListener(this);
        //언더바 리스너
        underbar_friend.setOnClickListener(this);
        underbar_list.setOnClickListener(this);
        underbar_setting.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.icon_search:
                Toast.makeText(this,"서치클릭",Toast.LENGTH_SHORT).show();
                break;
            case R.id.icon_add:
                Toast.makeText(this,"친구추가클릭",Toast.LENGTH_SHORT).show();
                break;
            case R.id.icon_friend:
                Toast.makeText(this,"친구목록클릭",Toast.LENGTH_SHORT).show();
                actionbar_main.setText("친구들");
                break;
            case R.id.icon_chatList:
                Toast.makeText(this,"채팅리스트클릭",Toast.LENGTH_SHORT).show();
                actionbar_main.setText("채팅방들");
                break;
            case R.id.icon_setting_underbar:
                Toast.makeText(this,"전체설정클릭",Toast.LENGTH_SHORT).show();
                actionbar_main.setText("설정");
                break;
        }
    }
}
