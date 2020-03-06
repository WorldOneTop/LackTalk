package com.example.lacktalk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
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
    Pattern p = Pattern.compile("^[a-zA-Z0-9]+@[a-zA-Z0-9]+\\.[a-zA-Z0-9]+$");
    Pattern p2 = Pattern.compile("^[a-zA-Z0-9!@.#$%^&*?_~]{4,16}$");
    InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        id = findViewById(R.id.emailInput);
        pw = findViewById(R.id.passwordInput);
        login = findViewById(R.id.loginButton);
        signup = findViewById(R.id.signupButton);

        signup.setTag(false);//회원가입누르지않았다는뜻


        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);//키보드 이벤트 발생용


        findViewById(R.id.rootLayout_intro).setOnClickListener(new View.OnClickListener() {//딴데누르면 키보드 닫히게
            @Override
            public void onClick(View view) {
                imm.hideSoftInputFromWindow(id.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(pw.getWindowToken(), 0);
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(p2.matcher(pw.getText().toString()).matches() && p.matcher(id.getText().toString()).matches()){
                    //네트워크로보내서 아이디 중복 여부 후 가입 or 다시시
               }else
                    Toast.makeText(Login.this,"모두 올바르게 기입해주세요",Toast.LENGTH_LONG).show();
            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if((boolean)signup.getTag()){//눌렀었다면 있는아이디인지 확인차 누른것
                    signup.setText("Submit");
                    login.setVisibility(View.VISIBLE);//임시로보여지는거
                    signup.setTag(false);
                }
                else{//누르지않았는데 눌렀다면 회원가입폼으로
                    id.setText("");
                    pw.setText("");
                    login.setVisibility(View.GONE);
                    signup.setTag(true);
                }
            }
        });


        onTextChangeListenerID();
        pw.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //Enter key Action
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {

                    if (!p2.matcher(pw.getText().toString()).matches()) {
                        Toast.makeText(Login.this, "4~16글자를 사용해주세요.", Toast.LENGTH_SHORT).show();
                        imm.showSoftInput(pw, 0);
                        pw.setSelection(pw.getText().length());

                    } else {
                        imm.hideSoftInputFromWindow(pw.getWindowToken(), 0);
                        login.performClick();
                    }
                    return true;
                }
                return false;
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
