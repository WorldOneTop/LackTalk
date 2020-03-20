package com.example.lacktalk;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class Option extends Fragment implements View.OnClickListener {
    TextView textView_logout, textView_host, textView_implement, textView_license, textView_name, textView_pw, textView_theme;
    Context context;
    ChatList chatList;

    public Option(ChatList c) {
        context = c;
        chatList = c;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ScrollView layout = (ScrollView) inflater.inflate(R.layout.activity_option, container, false);

        init(layout);

        return layout;
    }

    public void init(View rootLayout) {
        textView_logout = rootLayout.findViewById(R.id.option_logout);
        textView_host = rootLayout.findViewById(R.id.option_host);
        textView_implement = rootLayout.findViewById(R.id.option_implement);
        textView_license = rootLayout.findViewById(R.id.option_license);
        textView_name = rootLayout.findViewById(R.id.option_name);
        textView_pw = rootLayout.findViewById(R.id.option_pw);
        textView_theme = rootLayout.findViewById(R.id.option_theme);

        textView_logout.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.option_logout:
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("로그아웃 하시겠습니까?")
                        .setCancelable(true)
                        .setPositiveButton("예",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intro.ID="";
                                        NodeJS.getInstance().setHostStart(NodeJS.HOST, context);
                                        new File(context.getFilesDir(), Intro.FILENAME_LOGIN_PATH).delete();
                                        startActivity(new Intent(context, Login.class));
                                        chatList.finish();
                                    }
                                })
                        .setNegativeButton("아니오",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                })
                        .show();
                break;
            case R.id.option_host:
                break;
            case R.id.option_implement:
                break;
            case R.id.option_license:
                break;
            case R.id.option_name:
                break;
            case R.id.option_pw:
                break;
            case R.id.option_theme:
                break;

        }
    }
}
