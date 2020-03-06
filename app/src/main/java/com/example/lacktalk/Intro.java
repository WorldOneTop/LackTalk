package com.example.lacktalk;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.PatternMatcher;
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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

public class Intro extends AppCompatActivity {
    //권한체크 폴더체크 디비체크
    public static int WIDTH, HEIGHT;//상태바, 소프트키 크기 제외
    public static int HEIGHT_KEYBOARD;//키보드값 파일에서 따옴
    public static final String FILENAME = "keyboardHeight.txt";
    public static final String FILENAME_LOGIN_PATH = "autoLoginInfo.txt";
    public static NodeJS nodeJS;
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
                            outputStream.write((HEIGHT_KEYBOARD + "").getBytes());
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
                String result = "";
                while ((i = inputStream.read()) != -1)
                    result += (char) i;
                inputStream.close();

                HEIGHT_KEYBOARD = Integer.parseInt(result);
                startNextActivityDelay(800);
            } catch (Exception e) {
                Toast.makeText(this, "알 수 없는 에러로 종료합니다.", Toast.LENGTH_LONG).show();
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

        //공유할 nodejs객체 선언
        nodeJS = new NodeJS();
//        nodeJS.start();

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

    private void startNextActivityDelay(int delaye) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                File file2 = new File(Intro.this.getFilesDir(), FILENAME_LOGIN_PATH);
                if (file2.exists()) {//자동로그인 체크해재시 파일을 지움
                    intent = new Intent(Intro.this, ChatList.class);
                    intent.putExtra("id", "");
                    intent.putExtra("pw", "");
                } else {
                    intent = new Intent(Intro.this, SocketTest.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, delaye);
    }

    //이밑에는공용으로쓰일 static 메서드

    public static void selectIP_Dialog(final Context context) {
        final Pattern ipaddressPattern = Pattern.compile(
                "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

        final EditText edittext = new EditText(context);

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("서버 IP 지정");
        builder.setMessage("ex) 192.168.0.1 ");
        builder.setView(edittext);
        builder.setCancelable(false);
        builder.setPositiveButton("입력",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {/*override*/}});
        builder.setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(context, "취소 되었습니다.", Toast.LENGTH_LONG).show();
                    }
                });
        //이밑에는 버튼눌러도 안닫히게끔 재정의
        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ipaddressPattern.matcher(edittext.getText().toString()).matches()) {
                    Toast.makeText(context, "IP형식에 맞게 다시 입력해주세요.", Toast.LENGTH_LONG).show();
                } else {
                    NodeJS.setHost(edittext.getText()+"");
                    Toast.makeText(context, "설정 되었습니다.", Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }

            }

        });
    }

    public static String byteToHexString(byte[] data) {
        StringBuilder sb = new StringBuilder();
        for (byte b : data)
            sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
        return sb.toString();
    }

    public static String stringToSHA_256(String str) throws NoSuchAlgorithmException {

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(str.getBytes());
        return byteToHexString(md.digest());
    }
}
