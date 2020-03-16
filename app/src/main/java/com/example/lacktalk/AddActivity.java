package com.example.lacktalk;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AddActivity extends AppCompatActivity implements View.OnClickListener {
    public static AdapterList adapterUser;
    public static boolean isFriend;
    private ListView listView_friend;
    private HorizontalScrollView scrollView;
    private RelativeLayout profileLayout;
    private ImageView profile,search;
    private TextView name,message;
    private EditText editText;
    private InputMethodManager imm;
    private Handler handler;
    private View includeView;
    private TextView noSearch;
    private String id,imgPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        init();
    }
    public void init(){
        //공통 변수설정
        editText = findViewById(R.id.add_editText);
        search = findViewById(R.id.add_search);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);//키보드 이벤트 발생용
        handler = new Handler();

        //공통 뷰 디폴트 설정
        includeView = findViewById(R.id.add_actionbar);
        includeView.findViewById(R.id.action_bar_line).setVisibility(View.GONE);
        includeView.findViewById(R.id.action_bar_chatRoom).setVisibility(View.VISIBLE);
        includeView.findViewById(R.id.icon_menu).setVisibility(View.GONE);
        includeView.findViewById(R.id.icon_search_room).setVisibility(View.GONE);
        editText.requestFocus();
        imm.showSoftInput(editText, 0);
        includeView.findViewById(R.id.icon_back).setOnClickListener(this);



        if(isFriend)
            addFriend();
        else
            addRoom();

        search.setOnClickListener(this);
        profile.setOnClickListener(this);


        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0 && s.charAt(start + count - 1) == '\n') {//엔터키 판정
                    editText.setText(editText.getText().toString().replace("\n", ""));
                    imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                    if(isFriend)//친구추가
                        search.performClick();
                }
            }
            @Override// 글자를 지웠을때임
            public void afterTextChanged(Editable arg0) {}
            @Override// 입력하기 전에
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}});





    }

    public void addFriend(){
        ((TextView)includeView.findViewById(R.id.icon_name)).setText("친구 추가");
        profileLayout = findViewById(R.id.add_profileLayout);
        profileLayout.setOnClickListener(this);

        name = findViewById(R.id.add_nickname);
        message = findViewById(R.id.add_profile_massage);
        profile = findViewById(R.id.add_profile);
        noSearch = findViewById(R.id.add_noSearch);


        Intro.eventUserInfo = new EventUserInfo() {
            @Override
            public void messageArrive() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(NodeJS.recvUserInfo != null) {//한명이상있다는 뜻
                            try {
                                noSearch.setVisibility(View.GONE);
                                profileLayout.setVisibility(View.VISIBLE);
                                name.setText(NodeJS.recvUserInfo.getString("name"));
                                message.setText(NodeJS.recvUserInfo.getString("msg"));
                                id = NodeJS.recvUserInfo.getString("id");
                                imgPath = NodeJS.recvUserInfo.getString("picture");
                                if(imgPath.isEmpty())
                                    profile.setImageResource(R.drawable.defaultimg);
                                else
                                    Log.d("asd",NodeJS.recvUserInfo.getString("picture")+"      asdasd");
//                                    Intro.eventUserInfo = null;//뒤로가기시에 적용함
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else{
                            noSearch.setVisibility(View.VISIBLE);
                            profileLayout.setVisibility(View.GONE);

                        }
                    }
                });
            }
        };
    }
    public void addRoom(){
        ((TextView)includeView.findViewById(R.id.icon_name)).setText("채팅방 추가");

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.icon_back:
                onBackPressed();
                break;
            case R.id.add_search:
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                if(isFriend){//친구추가
                    JSONObject jsonObject = new JSONObject();
                    if(!editText.getText().toString().isEmpty()) {
                        try {
                            jsonObject.put("id", editText.getText());
                            NodeJS.sendJson("userInfo",jsonObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }}}
                break;
            case R.id.add_profile:
                Intent intent = new Intent(this,ProfileActivity.class);
                intent.putExtra("name",name.getText());
                intent.putExtra("message",message.getText());
                intent.putExtra("picture","");
                intent.putExtra("isMe", false);
                startActivity(intent);
                break;
            case R.id.add_profileLayout:
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                if(noSearch.getVisibility() == View.GONE) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("추가하시겠습니까?");
                    builder.setCancelable(true);
                    builder.setPositiveButton("예",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    new Thread(){
                                        @Override
                                        public void run() {
                                            if(AppDatabase.getInstance(AddActivity.this).myDao().getUserSame(id) == null) {
                                                AppDatabase.getInstance(AddActivity.this).myDao().insertUser(new db_User(id, name.getText() + "", imgPath, message.getText() + ""));
                                                onBackPressed();
                                            }
                                            else
                                                handler.post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        if(Intro.ID.equals(id))
                                                            Toast.makeText(AddActivity.this,"자기 자신은 등록 할 수 없습니다.",Toast.LENGTH_LONG).show();
                                                        else
                                                            Toast.makeText(AddActivity.this,"이미 친구로 등록 하셨습니다.",Toast.LENGTH_LONG).show();
                                                    }
                                                });
                                        }
                                    }.start();

                                }
                            });
                    builder.setNegativeButton("아니오",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                    builder.show();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Intro.eventUserInfo = null;
        super.onBackPressed();
    }
}
