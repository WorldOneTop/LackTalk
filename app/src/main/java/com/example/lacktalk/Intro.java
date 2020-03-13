package com.example.lacktalk;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
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
    public static final String FILENAME_IP = "ip.txt";
    public static boolean showingIP = false;
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
//        startActivity(new Intent(this,AddActivity.class));
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


        NodeJS.getInstance().start(this); //공유할 nodejs객체 늦은 init 선언

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
                if (file2.exists()) {//자동로그인이 될경우 바로 로그인
                    intent = new Intent(Intro.this, ChatList.class);
                    try {
                        FileReader fileReader = new FileReader(file2);
                        char []buf = new char[2048];
                        fileReader.read(buf);
                        JSONObject jsonObject = new JSONObject(new String(buf));
                        fileReader.close();
                        intent.putExtra("name",jsonObject.getString("name"));
                        intent.putExtra("picture",jsonObject.getString("picture"));
                        intent.putExtra("msg",jsonObject.getString("msg"));
                        checkAutoLogin(jsonObject.getString("id"),jsonObject.getString("pw"),intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {//자동로그인 해재시 파일을 지우면됨
                        intent = new Intent(Intro.this, Login.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, delaye);
    }

    public void checkAutoLogin(final String id, final String pw, final Intent intent) throws JSONException {//자동로그인 정보 가져온걸 서버랑 비교해서 담액티비티로
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", id);
        jsonObject.put("pw", pw);
        rootLayout.setAlpha(0.7f);
        NodeJS.getInstance().sendJson("login", jsonObject);
        handler.post(new Runnable() {
            @Override
            public void run() {
                Log.d("asd","네트워크 상태 : "+NodeJS.STATUS+"받은 상태 "+ NodeJS.isRecv);
               if (NodeJS.STATUS != 1) {//연결이 끊기거나 에러라면
                    Toast.makeText(Intro.this, "서버와의 연결 상태를 확인해 주세요.", Toast.LENGTH_LONG).show();
                    if(!showingIP) {   //액티비티 안보이면
                        selectIP_Dialog(Intro.this);
                        handler.postDelayed(this, 1200);
                        return;
                    }
                    else{//설정창이보이고 연결이안된상태라면
                        handler.postDelayed(this, 500);
                        return;
                    }
                }
                else {//연결이 된 상태라면
                    if (NodeJS.isRecv) {//결과를 받았다면
                        rootLayout.setAlpha(1);
                        if (NodeJS.getRecvBoolean()) {//그 결과가 참이라면
                            intent.putExtra("id", id);
                            intent.putExtra("pw", pw);
                            startActivity(intent);
                            finish();
                            return;
                        } else {
                            Intent intent = new Intent(Intro.this, Login.class);
                            startActivity(intent);
                            finish();
                            Toast.makeText(Intro.this, "등록된 로그인 정보가 다릅니다.", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                    else{//연결되고 결과안받았다면
                        handler.postDelayed(this, 600);
                        return;
                    }
                }

            }
        });
    }

    //이밑에는공용으로쓰일 static 메서드

    public static void selectIP_Dialog(final Context context) {
        Intro.showingIP = true;
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
                        NodeJS.getInstance().setHostStart(NodeJS.HOST,context);
                        Toast.makeText(context, "취소 되었습니다.", Toast.LENGTH_LONG).show();
                        Intro.showingIP = false;
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
                    NodeJS.getInstance().setHostStart(edittext.getText()+"",context);
                    Toast.makeText(context, "설정 되었습니다.", Toast.LENGTH_LONG).show();
                    Intro.showingIP = false;
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
    public static String getInitialSound(String text) {
        String[] chs = { "ㄱ", "ㄲ", "ㄴ", "ㄷ", "ㄸ", "ㄹ", "ㅁ", "ㅂ", "ㅃ", "ㅅ", "ㅆ", "ㅇ", "ㅈ", "ㅉ", "ㅊ", "ㅋ", "ㅌ", "ㅍ",
                "ㅎ" };

        if (text.length() > 0) {
            String result="";
            for (int i = 0; i < text.length(); i++) {
                char chName = text.charAt(i);
                if (chName >= 0xAC00) {
                    int uniVal = chName - 0xAC00;
                    int cho = ((uniVal - (uniVal % 28)) / 28) / 21;
                    result += chs[cho];
                }
            }
            return result;
        }

        return null;
    }
}
