package com.example.lacktalk;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class Intro extends AppCompatActivity {
    //권한체크 폴더체크 디비체크
    public static int WIDTH, HEIGHT;//상태바, 소프트키 크기 제외
    public static int HEIGHT_KEYBOARD;//키보드값 파일에서 따옴
    public static final String FILENAME = "keyboardHeight.txt";
    public static final String FILENAME_LOGIN_PATH = "autoLoginInfo.txt";
    Handler handler;
    ConstraintLayout rootLayout;
    InputMethodManager imm;
    EditText editText;
    boolean onDraw;//그린시점확인용
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        init();

        File file = new File(this.getFilesDir(), FILENAME);
        if (!file.exists()) {//키보드높이 저장한 파일이 없다면
            rootLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (!onDraw) {
                        imm.showSoftInput(editText, 0);
                    }
                    if (getKeyboardHeight() > 300 && !onDraw) { //변화량이 300이 넘으면 키보드 가올라갔다고 간주
                        HEIGHT_KEYBOARD = getKeyboardHeight();
                        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                        onDraw = true;
                        try {
                            FileOutputStream outputStream = openFileOutput(FILENAME, Context.MODE_PRIVATE);
                            outputStream.write((HEIGHT_KEYBOARD+"").getBytes());
                            outputStream.close();


                        } catch (Exception e) {
                            Log.d("asd", "인트로 중 파일에러" + e);
                            e.printStackTrace();
                        }
                        startNextActivityDelay(400);
                    }
                }
            });

        } else {//키보드값이 저장돼있다면
            try {
                InputStream inputStream = openFileInput(FILENAME);
                int i;
                String result="";
                while ((i = inputStream.read()) != -1)
                    result +=(char)i;
                inputStream.close();

                HEIGHT_KEYBOARD = Integer.parseInt(result);
                startNextActivityDelay(800);
            } catch (Exception e) {
                Toast.makeText(this,"알 수 없는 에러로 종료합니다.",Toast.LENGTH_LONG).show();
                Log.d("asd", "인트로 중 파일에러" + e);
                e.printStackTrace();
                finish();
            }
        }



    }
    public void init() {
        handler = new Handler();
        rootLayout = findViewById(R.id.intro_rootLayout);
        editText = findViewById(R.id.blind_editText);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);//키보드 이벤트 발생용
        onDraw = false;//그려지는시점으로 판단
        editText.requestFocus();//edittext에 포커스줘서 키보드 올릴수있게

        //status bar의 크기가 무시되므로 빼줌
        WIDTH = getApplicationContext().getResources().getDisplayMetrics().widthPixels;
        HEIGHT = getApplicationContext().getResources().getDisplayMetrics().heightPixels - getStatusBarHeight();

        intent = new Intent(Intro.this, ChatList.class);
    }

    private int getKeyboardHeight() {
        Rect visibleFrameSize = new Rect();
        rootLayout.getWindowVisibleDisplayFrame(visibleFrameSize);

        return HEIGHT - (visibleFrameSize.bottom - visibleFrameSize.top);
    }

    public int getStatusBarHeight() {//상태바 크기 구하기
        int statusHeight = 0;
        int screenSizeType = (getApplicationContext().getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK);
        if (screenSizeType != Configuration.SCREENLAYOUT_SIZE_XLARGE) {
            int resourceId = getApplicationContext().getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                statusHeight = getApplicationContext().getResources().getDimensionPixelSize(resourceId);
            }
        }
        return statusHeight;
    }

    private void startNextActivityDelay(int delaye){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                File file2 = new File(Intro.this.getFilesDir(),FILENAME_LOGIN_PATH);
                if(file2.exists()){//자동로그인 체크해재시 파일을 지움
                    intent = new Intent(Intro.this,ChatList.class);
                    intent.putExtra("id","");
                    intent.putExtra("pw","");
                }
                else{
                    intent = new Intent(Intro.this,Login.class);
                    startActivity(intent);
                    finish();
                }

            }
        }, delaye);
    }
}
