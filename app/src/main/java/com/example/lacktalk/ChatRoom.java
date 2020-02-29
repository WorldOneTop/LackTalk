package com.example.lacktalk;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class ChatRoom extends AppCompatActivity {
    Random random;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);
        Button button = findViewById(R.id.button);

        final AdapterChat adapter = new AdapterChat();

        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(adapter);

        random = new Random();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                adapter.addItem("dfault","Test",randomStr(),new SimpleDateFormat("yyyy/MM/dd/HH/ss").format(new Date()),random.nextBoolean());
                adapter.notifyDataSetChanged();
            }
        });
    }
    public String randomStr(){
        int a = random.nextInt(50);
        String result = "";
        for(int i=0;i<a;i++){
            result += (char)((Math.random() * 26) + 97);
        }
        return result;
    }
}
