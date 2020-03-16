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

public class ProfileActivity extends AppCompatActivity {
    PhotoView photoView;
    RelativeLayout rootLayout,normalLayout;
    ImageView picture;
    TextView name,message;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        init();

//        getintent("isOne");
//        true라면
//        편집 레이아웃 추가
    }


    public void init(){
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
//        intent.putExtra("name",name);
//        intent.putExtra("message",message);
//        intent.putExtra("picture",picture);


        if(intent.getBooleanExtra("isMe",false))
            initOne();

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
                EditText editText = new EditText(ProfileActivity.this);
                editText.setHint("visitor");
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                builder.setTitle("이름을 바꾸시겠습니까?")
                        .setCancelable(false)
                        .setView(editText)
                        .setPositiveButton("예",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Log.d("asd","설정되었습니다");
                                    }
                                })
                        .setNegativeButton("아니오",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                })
                        .show();
            }
        });
    }
    public void initOne(){//나를 누르면 편집모드로
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
                                    Toast.makeText(getApplicationContext(), items[index], Toast.LENGTH_SHORT).show();
                                }
                            })
                        .show();
            }
        });
        rootLayout.addView(editButton);
    }


    @Override
    public void onBackPressed() {
        if(photoView.getVisibility() == View.VISIBLE) {
            normalLayout.setVisibility(View.VISIBLE);
            photoView.setVisibility(View.GONE);
        }
        else
            super.onBackPressed();
    }

}
