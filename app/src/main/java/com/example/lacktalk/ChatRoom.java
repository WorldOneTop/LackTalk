package com.example.lacktalk;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class ChatRoom extends Activity implements View.OnClickListener {
    Intent intent;
    ImageView imageView_back, imageView_search, imageView_menu, imageView_add, imageView_send;
    ImageView drawer_alarm, drawer_exit, drawer_setting;
    TextView textView_name, drawer_main, drawer_picture, drawer_file;
    EditText editText_chat;
    public static AdapterChat adapter;
    InputMethodManager imm;
    DrawerLayout drawer;
    TableLayout under_Table;
    ListView listView, drawer_listview;
    RelativeLayout drawer_rootLayout, chatroom_roootLayout;
    Handler handler;
    int roomNum, sumPeople;//방번호, 총 사람 수
    String users;//방에 있는 유저 아이디 /로 자르기


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);
        init();


    }


    public void init() {
        //채팅방 위에 액션바(뒤로가기 이름 등등 ) 뷰 선언 및 보이게
        View include_layout = findViewById(R.id.include_actionbar);
        include_layout.findViewById(R.id.action_bar_chatRoom).setVisibility(View.VISIBLE);
        include_layout.findViewById(R.id.action_bar_friend).setVisibility(View.GONE);
        imageView_back = include_layout.findViewById(R.id.icon_back);
        imageView_search = include_layout.findViewById(R.id.icon_search_room);
        imageView_menu = include_layout.findViewById(R.id.icon_menu);
        textView_name = include_layout.findViewById(R.id.icon_name);

        //채팅방 아래 ( edittext, +버튼 등등 ) 뷰 선언 및 보이게
        View include_underbar = findViewById(R.id.include_underbar);
        include_underbar.findViewById(R.id.underbar_Room).setVisibility(View.VISIBLE);
        include_underbar.findViewById(R.id.underbar_List).setVisibility(View.GONE);
        imageView_add = include_underbar.findViewById(R.id.icon_add_underbar);
        imageView_send = include_underbar.findViewById(R.id.icon_send);
        editText_chat = include_underbar.findViewById(R.id.edttext_chatroom);
        drawer = (DrawerLayout) findViewById(R.id.drawerLayout);

        //채팅방 서랍 열면 나오는 뷰 선언
        drawer_alarm = findViewById(R.id.chatroom_drawer_chatAlarm);
        drawer_exit = findViewById(R.id.chatroom_drawer_chatOut);
        drawer_setting = findViewById(R.id.chatroom_drawer_chatSetting);
        drawer_main = findViewById(R.id.chatroom_drawer_main);
        drawer_picture = findViewById(R.id.chatroom_drawer_picture);
        drawer_file = findViewById(R.id.chatroom_drawer_file);
        drawer_listview = findViewById(R.id.chatroom_drawer_listview);
        drawer_rootLayout = findViewById(R.id.drawerLayout_root);

        //그외 기본 나와있는 뷰 및 변수 선언
        listView = findViewById(R.id.listView);
        under_Table = include_underbar.findViewById(R.id.underbar_Table);
        chatroom_roootLayout = findViewById(R.id.chatroom_rootLayout);
        intent = getIntent();
        handler = new Handler();
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);//키보드 이벤트 발생용
        roomNum = intent.getIntExtra("pnum", 0);
        sumPeople = intent.getIntExtra("amount", 0);
        users = intent.getStringExtra("users");


        //디폴트 설정
        drawer_alarm.setTag(1);//알람켜짐 태그
        under_Table.setMinimumHeight(Intro.HEIGHT_KEYBOARD);//+버튼 누른 후 나오는 만큼을 키보드 높이만
        textView_name.setText(intent.getExtras().getString("name"));//채팅방 이름 설정
        drawer_rootLayout.setOnClickListener(null);//서랍 뒤가 터치 안되게 막아놓음
        include_layout.findViewById(R.id.action_bar_chatRoom).setBackgroundColor(getResources().getColor(R.color.color_background));//배경색상설정  #ddFFF2CB
        final String[] strings = users.split("/");
        final HashMap<String,String> hashMap = new HashMap<>();
        new Thread(){
            @Override
            public void run() {
                for(String str : strings) {
                    if(str.equals(Intro.ID))
                        continue;
                    hashMap.put(str,AppDatabase.getInstance(ChatRoom.this).myDao().getUser(str));
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        adapter = new AdapterChat(hashMap);
                        listView.setAdapter(adapter);//리스트뷰 커스텀 어뎁터 설정

                        init_ClickListener();
                        init_chatting();
                        init_reciver();
                    }
                });

            }
        }.start();


    }

    public void init_ClickListener() {
        imageView_back.setOnClickListener(this);
        imageView_search.setOnClickListener(this);
        imageView_menu.setOnClickListener(this);
        imageView_add.setOnClickListener(this);
        imageView_send.setOnClickListener(this);

        //서랍 부분
        drawer_main.setOnClickListener(this);
        drawer_picture.setOnClickListener(this);
        drawer_file.setOnClickListener(this);
        drawer_exit.setOnClickListener(this);
        drawer_alarm.setOnClickListener(this);
        drawer_setting.setOnClickListener(this);

        findViewById(R.id.table_album).setOnClickListener(this);


        chatroom_roootLayout.setOnClickListener(new View.OnClickListener() {//키보드 내리기용
            @Override
            public void onClick(View view) {
                imm.hideSoftInputFromWindow(editText_chat.getWindowToken(), 0);
            }
        });
        //일단은 키보드 내리기용 이후 기능 추가(ex 답장 삭제)
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            }
        });
        editText_chat.setOnTouchListener(new View.OnTouchListener() {//처음 edittext눌렀을땐 반응안해서 터치로
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    if (under_Table.getVisibility() == View.VISIBLE) {//테이블매뉴가 보인다면
                        imageView_add.setRotation(imageView_add.getRotation() + 45);

                    }
                    under_Table.setVisibility(View.GONE);
                    imm.showSoftInput(editText_chat, 0);
                    editText_chat.setSelection(editText_chat.getText().length());
                }
                return false;
            }
        });
    }

    public void init_chatting() {
        new Thread() {
            @Override
            public void run() {
                try {//방들어와서 현재 방의 데이터 다 읽었다고알림
                    NodeJS.sendJson("readChat", new JSONObject().put("roomNum", roomNum).put("start", AppDatabase.getInstance(ChatRoom.this).myDao().getStartRead(roomNum)));
                    AppDatabase.getInstance(ChatRoom.this).myDao().setListAmount(roomNum);//읽은위치 추가
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Cursor list = AppDatabase.getInstance(ChatRoom.this).myDao().getChatInRoom(roomNum);
                list.moveToNext();//맨처음은 방 만드려고 만든 데이터
//                int db_room = AppDatabase.getInstance(ChatRoom.this).myDao().roomExists(roomNum);
                while (list.moveToNext()) {
                    adapter.addItem(list.getString(0), list.getString(1), list.getString(2), list.getString(3), Intro.ID.equals(list.getString(6)), list.getInt(4), list.getInt(5), list.getString(6), list.getInt(7));
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        }.start();
    }

    public void init_reciver() {
        Intro.recvChatting = new RecvChatting() {
            @Override
            public void messageReceive() {
                try {
                    JSONObject j = NodeJS.recvChatting;

                    if (AppDatabase.getInstance(ChatRoom.this).myDao().roomExists(j.getInt("num")) == 0) {//방이없었다면 방 DB추가
                        NodeJS.sendJson("createRoomID", new JSONObject().put("num", j.getInt("num")));
                        AppDatabase.getInstance(ChatList.CONTEXT).myDao().insertRecode(new db_Recode(j.getInt("num"), 0, Intro.ID, "0000.00.00.00.00", "", 1, 0));
                    }
                    if (AppDatabase.getInstance(ChatRoom.this).myDao().isFriend(j.getString("id")) == 0) {//만약 친구사이가아니라면
                        return;
                    }

                    db_Recode recode = new db_Recode(j.getInt("num"), j.getInt("amount"), j.getString("id"), j.getString("time"), j.getString("text"), j.getInt("type"), j.getInt("server"));
                    AppDatabase.getInstance(ChatRoom.this).myDao().insertRecode(recode);//채팅 디비에 넣고
//                    db_Recode db_recode = new db_Recode(NodeJS.recvInt,0,Intro.ID,new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()),"",1,0);

                    if (roomNum == j.getInt("num")) {//받은데이터가 보고있는 방에서 나왔다면         아이템추가후
                        Cursor list = AppDatabase.getInstance(ChatRoom.this).myDao().getChatInRoomLast(j.getInt("num"));
                        list.moveToNext();
                        adapter.addItem(list.getString(0), list.getString(1), list.getString(2), list.getString(3), Intro.ID.equals(list.getString(6)), list.getInt(4), list.getInt(5), list.getString(6), list.getInt(7));
                        int start = j.getInt("server");
                        NodeJS.sendJson("readChat", new JSONObject().put("roomNum", roomNum).put("start", start));//읽었다고 알림
                        AppDatabase.getInstance(ChatList.CONTEXT).myDao().updateRead(j.getInt("server"), roomNum);//읽은위치 추가
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        Intro.eventRead = new EventRead() {
            @Override
            public void messageRead() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        adapter.readChat();
                    }
                });
            }
        };
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ChatList.room_out();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.icon_back:
                onBackPressed();
                break;
            case R.id.icon_search_room:
                if (!drawer.isDrawerOpen(Gravity.RIGHT)) {//열려있지 않다면
                    Toast.makeText(this, "서치클릭", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.icon_menu:
                if (!drawer.isDrawerOpen(Gravity.RIGHT)) {//열려있지 않다면
                    drawer.openDrawer(Gravity.RIGHT);
                    imm.hideSoftInputFromWindow(editText_chat.getWindowToken(), 0);
                }
                break;
            case R.id.icon_add_underbar:  // 테이블메뉴 애니메이션 처리 해결 X 그냥 시간차로 해서 놔둠
                if (under_Table.getVisibility() == View.VISIBLE) {//테이블매뉴가 보인다면
                    under_Table.setVisibility(View.GONE);
                    imm.showSoftInput(editText_chat, 0);
                } else {//안보이는상황
                    imm.hideSoftInputFromWindow(editText_chat.getWindowToken(), 0);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            under_Table.setVisibility(View.VISIBLE);
                        }
                    }, 50);

                }
                imageView_add.setRotation(imageView_add.getRotation() + 45);
                break;
            case R.id.icon_send://보냄 버튼 누름
                if (editText_chat.getText().toString().length() != 0) {
                    sendMsg(1, editText_chat.getText().toString());
                }
                break;//이밑에는 서랍
            case R.id.chatroom_drawer_main:
                Toast.makeText(this, "공지사항 클릭, 스크롤뷰로 내역 뜨게 할것", Toast.LENGTH_SHORT).show();
                break;
            case R.id.chatroom_drawer_picture:
                Toast.makeText(this, "사진동영상 클릭, 스크롤뷰로 내역 뜨게 할것", Toast.LENGTH_SHORT).show();
                break;
            case R.id.chatroom_drawer_file:
                Toast.makeText(this, "파일 클릭, 스크롤뷰로 내역 뜨게 할것", Toast.LENGTH_SHORT).show();
                break;
            case R.id.chatroom_drawer_chatOut:
                Toast.makeText(this, "나가기 클릭, 다이얼로그로 확인받고 나가게하기", Toast.LENGTH_SHORT).show();
                finish();
                break;
            case R.id.chatroom_drawer_chatAlarm:
                if ((int) drawer_alarm.getTag() == 1) {
                    Toast.makeText(this, "해당 방 알람이 해제 되었습니다.", Toast.LENGTH_SHORT).show();
                    drawer_alarm.setImageResource(R.drawable.icon_no_alarm);
                    drawer_alarm.setTag(0);
                } else {
                    Toast.makeText(this, "해당 방 알람이 설정 되었습니다.", Toast.LENGTH_SHORT).show();
                    drawer_alarm.setImageResource(R.drawable.icon_alarm);
                    drawer_alarm.setTag(1);
                }
                break;
            case R.id.chatroom_drawer_chatSetting:
                Toast.makeText(this, "설정 클릭, 채팅방 설정 전용 액티비티로 이동", Toast.LENGTH_SHORT).show();
                break;
            case R.id.table_album:
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent, 3);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 3) {
            if (resultCode == RESULT_OK) {
                try {
                    InputStream in = getContentResolver().openInputStream(data.getData());
                    Bitmap img = BitmapFactory.decodeStream(in);
                    in.close();
                    sendMsg(2, Intro.getStringFromBitmap(img));
                } catch (Exception e) {

                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "사진 선택 취소", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void sendMsg(final int type, final String text) {
        final String now = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        new Thread() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = new JSONObject();//room_num,amount,who,date,text,type
                    jsonObject.put("a", roomNum);
                    jsonObject.put("b", sumPeople);
                    jsonObject.put("c", Intro.ID);
                    jsonObject.put("d", now);
                    jsonObject.put("e", text);
                    jsonObject.put("f", type);
                    jsonObject.put("g", users);
                    jsonObject.put("isCreate", false);
                    NodeJS.sendJson("addChatRecode", jsonObject);
                    editText_chat.setText("");
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("asd", "채팅추가 에러 :" + e);
                }
            }
        }.start();
    }
}
