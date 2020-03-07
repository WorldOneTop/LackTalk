package com.example.lacktalk;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ChatList_In_ViewPager extends Fragment implements View.OnClickListener {
    private View rootView;
    private Context context;
    private int status;
    private ImageView actionbar_search,actionbar_add;
    private TextView actionbar_main;
    private AdapterList adapterList;
    private ListView listView;
    private String myName,myPicture,myMsg;

    ChatList_In_ViewPager(){}//앱 재실행 오류 이슈 떄문에 작성만
    ChatList_In_ViewPager(Context c, int s, String a,String b,String cc){
        context = c;
        status = s;
        myName = a;myPicture = b; myMsg = cc;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.viewpager_in_chatlist,container,false);

        init();
        return rootView;
    }
    private void init(){
        rootView.findViewById(R.id.include_actionbar).findViewById(R.id.action_bar_friend).setVisibility(View.VISIBLE);

        //채팅방 위에 액션바(뒤로가기 이름 등등 ) 뷰 선언 및 보이게
        View include_layout = rootView.findViewById(R.id.include_actionbar);
        include_layout.findViewById(R.id.action_bar_chatRoom).setVisibility(View.GONE);//챗룸에서쓰는액션바
        include_layout.findViewById(R.id.action_bar_friend).setVisibility(View.VISIBLE);//리스트에서쓰는액션바
        actionbar_search = include_layout.findViewById(R.id.icon_search);
        actionbar_add = include_layout.findViewById(R.id.icon_add);
        actionbar_main = include_layout.findViewById(R.id.action_bar_main);

        //리스트뷰 설정은 어뎁터에서
        adapterList = new AdapterList(status);
        listView = rootView.findViewById(R.id.listview_list);
        listView.setAdapter(adapterList);

            switch (status){
            case 0:
                addItem("나","들어갈","자리");
                addItem(myPicture,myName,myMsg);//나자신도 추가해야함
                addItem("구분선","친구들","몇명들어갈자리");
                actionbar_main.setText("친구들");
                break;
            case 1:
                actionbar_add.setImageResource(R.drawable.icon_addchat);
                actionbar_main.setText("채팅방들");

                break;
        }

        init_OnClick();
    }
    public void init_OnClick(){
        actionbar_search.setOnClickListener(this);
        actionbar_add.setOnClickListener(this);

        if(status==1){//채팅방클릭걸기
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent(context,ChatRoom.class);
                    intent.putExtra("name",adapterList.getItem(i).getName());
                    startActivity(intent);
                }
            });
        }
    }
    public void addItem(String a,String b,String c){
        adapterList.addItem(a,b,c);
        adapterList.notifyDataSetChanged();
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.icon_search:
                Toast.makeText(context, "서치클릭", Toast.LENGTH_SHORT).show();
                break;
            case R.id.icon_add:
                Toast.makeText(context, "친구추가클릭", Toast.LENGTH_SHORT).show();
                ChatList.randomAdd();
                break;
        }
    }
    public ChatList_In_ViewPager safeVar(Context c, int s){//생성자가 안되다 되다하는 현상 발견으로 인해 만든 안전장치
        context = c;
        status = s;
        return this;
    }
}