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


public class ChatList extends AppCompatActivity implements View.OnClickListener {
    private Intent intent;
    private Handler handler;
    private ListView listView;
    private ImageView underbar_friend, underbar_list, underbar_setting;
    private RelativeLayout relativeLayout;
    private static final int NUM_PAGE = 3;//3페이지만 구성해놓음
    private ViewPager pager;
    PagerAdapter pagerAdapter;
    private View upperLine1, upperLine2, upperLine3;
    public static ChatList_In_ViewPager[] viewPager_chatList;
    private String myName, myPicture, myMsg;
    private long backKeyPressedTime;

    ChatList() {
    }

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
            if (position == 2) return new Option(ChatList.this);
            return viewPager_chatList[position] = new ChatList_In_ViewPager(ChatList.this, position == 0 ? true : false).safeVar(getApplicationContext(), position == 0 ? true : false);
        }

        @Override
        public int getCount() {
            return NUM_PAGE;
        }
    }


    public void init() {
        //밖의 레이아웃 및 변수 선언
        intent = getIntent();
        myName = intent.getStringExtra("name");
        myPicture = intent.getStringExtra("picture");
        myMsg = intent.getStringExtra("msg");

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

    public void init_ClickListener() {
        //언더바 리스너
        underbar_friend.setOnClickListener(this);
        underbar_list.setOnClickListener(this);
        underbar_setting.setOnClickListener(this);

        //페이지이벤트
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

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
            public void onPageScrollStateChanged(int state) {
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
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

    @Override
    public void onBackPressed() {
        if (pager != null) {
            if (pager.getCurrentItem() == 2) {
                if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
                    backKeyPressedTime = System.currentTimeMillis();
                    Toast.makeText(this, "\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
                    super.onBackPressed();
                }
            } else {
                if (viewPager_chatList[pager.getCurrentItem()].onBackKeyPressM.press()) {
                    if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
                        backKeyPressedTime = System.currentTimeMillis();
                        Toast.makeText(this, "\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
                        super.onBackPressed();
                    }
                }
            }

        } else {
            super.onBackPressed();
        }
    }
}
