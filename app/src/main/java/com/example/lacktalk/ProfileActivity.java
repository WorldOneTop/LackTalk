package com.example.lacktalk;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.chrisbanes.photoview.PhotoView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;

public class ProfileActivity extends AppCompatActivity {
    PhotoView photoView;
    RelativeLayout rootLayout, normalLayout;
    ImageView picture;
    TextView name, message;
    Intent intent;
    boolean isMe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        init();

//        getintent("isOne");
//        true라면
//        편집 레이아웃 추가
    }


    public void init() {
        //선언부
        intent = getIntent();
        rootLayout = findViewById(R.id.profile_rootLayout);
        picture = findViewById(R.id.profile_picture);
        normalLayout = findViewById(R.id.profile_normalLayout);
        photoView = findViewById(R.id.profile_photoview);
        name = findViewById(R.id.profile_name);
        message = findViewById(R.id.profile_message);

        name.setText(intent.getStringExtra("name"));
        message.setText(intent.getStringExtra("message"));
        isMe = intent.getBooleanExtra("isMe", false);
//        intent.putExtra("name",name);
//        intent.putExtra("message",message);
//        intent.putExtra("picture",picture);


        if (isMe)
            initMe();

        //클릭리스너 구현부
        photoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                normalLayout.setVisibility(View.GONE);
                photoView.setImageResource(R.drawable.defaultimg);
                photoView.setVisibility(View.VISIBLE);
            }
        });
        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile(0);
            }
        });

    }

    public void initMe() {//나를 누르면 편집모드로
        Button editButton = new Button(this);
        editButton.setText("편집");
        final CharSequence[] items = {"이름", "상태 메시지", "배경 이미지"};
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                builder.setTitle("무엇을 편집하시겠습니까?")
                        .setItems(items, new DialogInterface.OnClickListener() {    // 목록 클릭시 설정
                            public void onClick(DialogInterface dialog, int index) {
                                updateProfile(index);
                            }
                        })
                        .show();
            }
        });
        rootLayout.addView(editButton);
    }

    //0은 이름, 1은 상메 2는 배사
    public void updateProfile(final int caseNum) {
        if (caseNum == 2)
            Toast.makeText(ProfileActivity.this, "추후에 함", Toast.LENGTH_LONG).show();

        final EditText editText = new EditText(ProfileActivity.this);
        editText.setHint(name.getText());
        final AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        if (caseNum == 0)
            builder.setTitle("이름을 바꾸시겠습니까?");
        else if (caseNum == 1)
            builder.setTitle("상태 메시지를 바꾸시겠습니까?");
        builder.setCancelable(false)
                .setPositiveButton("예",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
//                                이미지 설정용 아니면 오버라이드 해서 없어질 내용
                            }
                        })
                .setNegativeButton("아니오",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
        if (caseNum != 2) {//이름이나 상매바꾸는건 글자 수 제한때문에 따로 만듦
            builder.setView(editText);
            final AlertDialog dialog = builder.create();
            dialog.show();
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!editText.getText().toString().isEmpty()) {
                        if (editText.getText().toString().length() > 20) {
                            Toast.makeText(ProfileActivity.this, "20글자 이내로 작성해주세요.", Toast.LENGTH_LONG).show();
                            return;
                        }
                        else if(editText.getText().toString().contains("/")){//자르기용
                            Toast.makeText(ProfileActivity.this, "포함 할 수 없는 글자가 있습니다.", Toast.LENGTH_LONG).show();
                            return;
                        }
                        try {
                            if (isMe) {
                                FileReader fileReader = new FileReader(new File(ProfileActivity.this.getFilesDir(), Intro.FILENAME_LOGIN_PATH));
                                char[] buf = new char[2048];
                                fileReader.read(buf);
                                JSONObject jsonObject = new JSONObject(new String(buf));
                                fileReader.close();
                                if (caseNum == 0) {
                                    jsonObject.remove("name");
                                    jsonObject.put("name", editText.getText());
                                    name.setText(editText.getText());
                                } else if (caseNum == 1) {
                                    jsonObject.remove("msg");
                                    jsonObject.put("msg", editText.getText());
                                    message.setText(editText.getText());
                                }
                                FileWriter fileWriter = new FileWriter(new File(ProfileActivity.this.getFilesDir(), Intro.FILENAME_LOGIN_PATH));
                                fileWriter.write(jsonObject.toString());
                                fileWriter.flush();
                                fileWriter.close();
                                NodeJS.sendJson("userUpdate", jsonObject);
                                ChatList.viewPager_chatList[0].initFriendList();
                            } else {
                                name.setText(editText.getText());
                                new Thread() {
                                    @Override
                                    public void run() {
                                        try {
                                            AppDatabase.getInstance(ProfileActivity.this).myDao().updateFriendName(intent.getIntExtra("num", 0), editText.getText().toString());
                                            JSONObject jsonObject = new JSONObject();
                                            jsonObject.put("name", editText.getText().toString());
                                            jsonObject.put("id", Intro.ID);
                                            NodeJS.sendJson("updateFriendName", jsonObject);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                ChatList.viewPager_chatList[0].initFriendList();
                                            }
                                        });
                                    }
                                }.start();
                            }
                            Toast.makeText(ProfileActivity.this, "설정되었습니다", Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        } catch (Exception e) {
                            Log.d("asd", "에러 : " + e);
                        }
                    }
                }
            });
        } else//그게아니면 위에 정의한 대로 이미지 설정함
            builder.show();


    }


    @Override
    public void onBackPressed() {
        if (photoView.getVisibility() == View.VISIBLE) {
            normalLayout.setVisibility(View.VISIBLE);
            photoView.setVisibility(View.GONE);
        } else
            super.onBackPressed();
    }

}
