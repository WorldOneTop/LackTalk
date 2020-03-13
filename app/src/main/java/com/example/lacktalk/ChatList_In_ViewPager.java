package com.example.lacktalk;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ChatList_In_ViewPager extends Fragment implements View.OnClickListener {
    private View rootView;
    private Context context;
    private boolean isUserList;
    private ImageView actionbar_search,actionbar_add,searchImage;
    private TextView actionbar_main;
    private AdapterList adapterList;
    private ListView listView;
    private String myName,myPicture,myMsg;
    private RelativeLayout searchLayout;
    private EditText searchEdit;
    private InputMethodManager imm;


    ChatList_In_ViewPager(){}//앱 재실행 오류 이슈 떄문에 작성만
    ChatList_In_ViewPager(Context c, boolean i, String a,String b,String cc){
        context = c;
        isUserList = i;
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

        //검색부분
        searchLayout = rootView.findViewById(R.id.layout_search);
        searchEdit = rootView.findViewById(R.id.search_edittext);
        searchImage  = rootView.findViewById(R.id.search_x);

        //리스트뷰 설정은 어뎁터에서
        adapterList = new AdapterList(isUserList);
        listView = rootView.findViewById(R.id.listview_list);
        listView.setAdapter(adapterList);

        imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);//키보드 이벤트 발생용

        if(isUserList){
            addItem("나","들어갈","자리");
            addItem(myPicture,myName,myMsg);//나자신도 추가해야함
            addItem("구분선","친구들","몇명들어갈자리");
            actionbar_main.setText("친구들");
        }else{
            actionbar_add.setImageResource(R.drawable.icon_addchat);
            actionbar_main.setText("채팅방들");
        }


        init_OnClick();
    }
    public void init_OnClick(){
        actionbar_search.setOnClickListener(this);
        actionbar_add.setOnClickListener(this);
        searchImage.setOnClickListener(this);



        searchEdit.addTextChangedListener(new TextWatcher() {//필터링 하기위해
            @Override
            public void afterTextChanged(Editable edit) {
                    adapterList.getFilter().filter(edit.toString());
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });


        if(isUserList){

        }else{//아이템클릭걸기
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent(context,ChatRoom.class);
                    intent.putExtra("name",adapterList.getItem(i).getName());
                    startActivity(intent);
                    adapterList.getFilter().filter("");
                    searchLayout.setVisibility(View.GONE);
                }
            });
        }



    }
    public void addItem(String a,String b,String c){//테스트상이고 나중엔 지울것
        adapterList.addItem(a,b,c);
        adapterList.notifyDataSetChanged();
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.icon_search:
                if(searchLayout.getVisibility() == View.VISIBLE){
                    searchLayout.setVisibility(View.GONE);
                    searchEdit.setText("");
                    adapterList.getFilter().filter("");
                    imm.hideSoftInputFromWindow(searchEdit.getWindowToken(), 0);
                }else{
                    searchLayout.setVisibility(View.VISIBLE);
                    searchEdit.requestFocus();
                    imm.showSoftInput(searchEdit, 0);
                }
                break;
            case R.id.icon_add:
                Toast.makeText(context, "친구추가클릭", Toast.LENGTH_SHORT).show();
                ChatList.randomAdd();
                if(isUserList){
//                    AppDatabase.getInstance(context).myDao().insertUser(new db_User());
                }
                else{

                }
                break;
            case R.id.search_x:
                searchEdit.setText("");
                adapterList.getFilter().filter("");
                break;
        }
    }
    public ChatList_In_ViewPager safeVar(Context c, boolean i){//생성자가 안되다 되다하는 현상 발견으로 인해 만든 안전장치
        context = c;
        isUserList = i;
        return this;
    }
}