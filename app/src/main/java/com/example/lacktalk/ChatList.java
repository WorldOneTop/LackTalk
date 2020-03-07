package com.example.lacktalk;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.Random;

public class ChatList extends AppCompatActivity implements View.OnClickListener{
    private Intent intent;
    private Handler handler;
    private ListView listView;
    private ImageView underbar_friend,underbar_list,underbar_setting;
    private RelativeLayout relativeLayout;
    private static final int NUM_PAGE = 3;//3페이지만 구성해놓음
    private ViewPager pager;
    PagerAdapter pagerAdapter;
    private View upperLine1,upperLine2,upperLine3;
    static ChatList_In_ViewPager[] viewPager_chatList;
    private String myName,myPicture,myMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewpager);

        init();



    }

    //어댑터 안에서 각각의 아이템을 데이터로서 관리한다
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public Fragment getItem(int position) {//생성자가 안되다 되다하는 현상 발견으로 인해 만든 안전장치 safeVar
            if(position==2) return new Option();
            return viewPager_chatList[position] = new ChatList_In_ViewPager(getApplicationContext(), position,myName,myPicture,myMsg).safeVar(getApplicationContext(), position);
        }
        @Override
        public int getCount() {
            return NUM_PAGE;
        }
    }


    public void init(){
        //밖의 레이아웃 및 변수 선언
        intent = getIntent();
        myName= intent.getStringExtra("name");
        myPicture = intent.getStringExtra("picture");
        myMsg = intent.getStringExtra("msg");
        if(myName.equals("null")) myName = "";
        if(myPicture.equals("null")) myPicture ="";
        if(myMsg.equals("null")) myMsg = "";


        //페이지마다 보여질 뷰 및 변수 선언
        pager = findViewById(R.id.pager);
        viewPager_chatList = new ChatList_In_ViewPager[NUM_PAGE];//관리할수있게 따로 저장
        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());//페이지 어뎁터매니저 선언

        //채팅방 아래 ( edittext, +버튼 등등 ) 뷰 선언 및 보이게
        View include_underbar = findViewById(R.id.include_underbar);
        include_underbar.findViewById(R.id.underbar_Room).setVisibility(View.GONE);
        include_underbar.findViewById(R.id.underbar_List).setVisibility(View.VISIBLE);
        underbar_friend = include_underbar.findViewById(R.id.icon_friend);
        underbar_list = include_underbar.findViewById(R.id.icon_chatList);
        underbar_setting = include_underbar.findViewById(R.id.icon_setting_underbar);
        upperLine1 = findViewById(R.id.upperline_1);
        upperLine2 = findViewById(R.id.upperline_2);
        upperLine3 = findViewById(R.id.upperline_3);


        //디폴트 설정
        pager.setAdapter(pagerAdapter);
        pager.setPageTransformer(true, new ZoomOutPageTransformer());
        pager.setCurrentItem(1);//현재 화면을 1번째 인덱스로
        pager.setOffscreenPageLimit(2);//준비해놓는 페이지를 양옆 2개씩으로 oncreateview로 실행
        upperLine2.setVisibility(View.VISIBLE);//처음은 채팅방들부터 보여지기때문에


        init_ClickListener();

    }
    public void init_ClickListener(){
        //언더바 리스너
        underbar_friend.setOnClickListener(this);
        underbar_list.setOnClickListener(this);
        underbar_setting.setOnClickListener(this);

        //페이지이벤트
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        upperLine1.setVisibility(View.VISIBLE);
                        upperLine2.setVisibility(View.INVISIBLE);
                        upperLine3.setVisibility(View.INVISIBLE);
                    break;
                    case 1:
                        upperLine1.setVisibility(View.INVISIBLE);
                        upperLine2.setVisibility(View.VISIBLE);
                        upperLine3.setVisibility(View.INVISIBLE);
                        break;
                    case 2:
                        upperLine1.setVisibility(View.INVISIBLE);
                        upperLine2.setVisibility(View.INVISIBLE);
                        upperLine3.setVisibility(View.VISIBLE);

                        break;
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {}
        });

    }
    static Random random = new Random();
    public static void randomAdd(){
        for(int i=0;i<2;i++) {
            viewPager_chatList[i].addItem("", randomStr(), randomStr());
        }
    }
    public static String randomStr() {
        int a = random.nextInt(50);
        String result = "";
        for (int i = 0; i < a; i++) {
            result += (char) ((Math.random() * 26) + 97);
        }
        return result;
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.icon_friend:
                pager.setCurrentItem(0);//페이지 넘기는 역할인가봄
                break;
            case R.id.icon_chatList:
                pager.setCurrentItem(1);
                break;
            case R.id.icon_setting_underbar:
                pager.setCurrentItem(2);
                break;
        }
    }
}
