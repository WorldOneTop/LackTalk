package com.example.lacktalk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.socket.client.IO;
import io.socket.client.Socket;

public class Login extends AppCompatActivity {
    EditText id, pw;
    Button login, signup;
    ImageView goBack;
    Pattern p = Pattern.compile("^[a-zA-Z0-9]+@[a-zA-Z0-9]+\\.[a-zA-Z0-9]+$");
    Pattern p2 = Pattern.compile("^[a-zA-Z0-9!@.#$%^&*?_~]+$");
    InputMethodManager imm;
    RelativeLayout rootLayout;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        id = findViewById(R.id.emailInput);
        pw = findViewById(R.id.passwordInput);
        login = findViewById(R.id.loginButton);
        signup = findViewById(R.id.signupButton);
        goBack = findViewById(R.id.Login_goBack);
        rootLayout = findViewById(R.id.rootLayout_intro);
        handler = new Handler();

        signup.setTag(false);//회원가입누르지않았다는뜻


        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);//키보드 이벤트 발생용


        rootLayout.setOnClickListener(new View.OnClickListener() {//딴데누르면 키보드 닫히게
            @Override
            public void onClick(View view) {
                imm.hideSoftInputFromWindow(id.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(pw.getWindowToken(), 0);
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (p2.matcher(pw.getText().toString()).matches() && p.matcher(id.getText().toString()).matches()) {//조건에 맞게 로그인 클릭
                    JSONObject jsonObject = new JSONObject();
                    try {
                        wait_server_forResult(id.getText() + "",pw.getText() + "",true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else
                    Toast.makeText(Login.this, "모두 올바르게 기입해주세요", Toast.LENGTH_LONG).show();
            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((boolean) signup.getTag()) {//회원가입폼으로 눌렀다면

                    if (p2.matcher(pw.getText().toString()).matches() && p.matcher(id.getText().toString()).matches()) {//조건에 맞게  클릭
                        try {
                            wait_server_forResult(id.getText() + "",pw.getText() + "",false);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else
                        Toast.makeText(Login.this, "모두 올바르게 기입해주세요", Toast.LENGTH_LONG).show();
                } else {//누르지않았는데 눌렀다면 회원가입폼으로
                    signup.setText("Submit");
                    id.setText("");
                    pw.setText("");
                    login.setVisibility(View.GONE);
                    goBack.setVisibility(View.VISIBLE);
                    signup.setTag(true);
                }
            }
        });
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signup.setText("Sign up");
                login.setVisibility(View.VISIBLE);
                signup.setTag(false);
                goBack.setVisibility(View.GONE);
            }
        });

        onTextChangeListenerID();
        pw.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //Enter key Action
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {

                    if (!p2.matcher(pw.getText().toString()).matches()) {
                        Toast.makeText(Login.this, "제한된 특수문자를 사용하였습니다.", Toast.LENGTH_SHORT).show();
                        pw.requestFocus();
                        pw.setSelection(pw.getText().length());

                    } else {
                        imm.hideSoftInputFromWindow(pw.getWindowToken(), 0);
                        if((boolean)signup.getTag())
                            signup.performClick();
                        else
                            login.performClick();
                    }
                    return true;
                }
                return false;
            }
        });
    }

    public void wait_server_forResult(final String id, final String pw, final boolean isLogin) throws Exception {
        JSONObject jsonObject = new JSONObject();
        final String SHA_str = Intro.stringToSHA_256(pw);
        jsonObject.put("id", id);
        jsonObject.put("pw", SHA_str);
        rootLayout.setAlpha(0.7f);
        NodeJS.getInstance().sendJson(isLogin ? "login":"signup", jsonObject);
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (NodeJS.STATUS != 1) {//연결이 끊기거나 에러라면
                    Toast.makeText(Login.this, "서버와의 연결 상태를 확인해 주세요.", Toast.LENGTH_LONG).show();
                    Intro.selectIP_Dialog(Login.this);
                    return;
                }
                if (!NodeJS.isRecv)
                    handler.postDelayed(this, 800);
                else {//결과를 받았다면
                    if (NodeJS.getRecvBoolean()) {//결과가 참이라면
                        Intent intent = new Intent(Login.this, ChatList.class);
                        intent.putExtra("id", id);
                        intent.putExtra("pw", SHA_str);
                        if(isLogin){     //신규 사용자가 아니라 정보 얻기위해서
                            try {
                                for (int i = 0; i < 5; i++) {
                                    if (NodeJS.isRecv_msg) break;
                                    if (i == 4) {
                                        rootLayout.setAlpha(1);
                                        Toast.makeText(Login.this, "서버와의 연결 상태를 확인해 주세요.", Toast.LENGTH_LONG).show();
                                        Intro.selectIP_Dialog(Login.this);
                                        return;
                                    }
                                    Thread.sleep(800);//사용자 정보 얻기 위해서 시간 벎
                                }
                                rootLayout.setAlpha(1);//msg가 들어온거 확인한시점
                                JSONObject json_login = NodeJS.getMsg();
                                FileWriter fileWriter = new FileWriter(new File(Login.this.getFilesDir(),Intro.FILENAME_LOGIN_PATH));
                                fileWriter.write(json_login.toString());
                                fileWriter.flush();
                                fileWriter.close();
                                intent.putExtra("name",json_login.getString("name"));     intent.putExtra("picture",json_login.getString("picture"));
                                intent.putExtra("msg",json_login.getString("msg"));
                                startActivity(intent);
                                finish();
                                return;

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        //값 자동로그인으로 저장
                        try {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("id", id);           jsonObject.put("pw", SHA_str);
                            intent.putExtra("name","");     jsonObject.put("name", "");
                            intent.putExtra("picture","");  jsonObject.put("picture", "");
                            intent.putExtra("msg","");      jsonObject.put("msg", "");
                            FileWriter fileWriter = new FileWriter(new File(Login.this.getFilesDir(),Intro.FILENAME_LOGIN_PATH));
                            fileWriter.write(jsonObject.toString());
                            fileWriter.flush();
                            fileWriter.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(Login.this,"자동로그인 설정 실패",Toast.LENGTH_LONG).show();
                        }
                        startActivity(intent);
                        finish();
                    } else {//아니라면
                        if (isLogin)
                            Toast.makeText(Login.this, "ID 혹은 비밀번호를 확인해 주세요.", Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(Login.this, "이미 사용중인 ID 입니다.", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    public void onTextChangeListenerID() {
        id.addTextChangedListener(new TextWatcher() {
            @Override                   //총 글자      시작글자(공백제외)  내가 쓴 위치는 시작지점+개수 -1(index)
            public void onTextChanged(CharSequence s, int start, int before, int count) {// 입력되는 텍스트에 변화가 있을 때
                if (s.length() != 0 && s.charAt(start + count - 1) == '\n') {//엔터키 판정
                    id.setText(id.getText().toString().replace("\n", ""));
                    if (!p.matcher(id.getText().toString()).matches()) {
                        Toast.makeText(Login.this, "Email형식으로 입력하세요", Toast.LENGTH_SHORT).show();
                        id.setTextColor(Color.RED);
                        id.setSelection(id.getText().length());
                    } else {
                        id.setTextColor(Color.BLACK);
                        pw.requestFocus();
                    }
                }
            }

            @Override// 글자를 지웠을때임
            public void afterTextChanged(Editable arg0) {
            }

            @Override// 입력하기 전에
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
        });
    }
}
