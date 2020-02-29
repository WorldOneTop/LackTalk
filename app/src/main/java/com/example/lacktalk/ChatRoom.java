package com.example.lacktalk;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.Date;

public class ChatRoom extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);
        Button button = findViewById(R.id.button);

        final AdapterChat adapter = new AdapterChat();

        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(adapter);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.addItem("dfault","Test","testmassage",new Date());
                adapter.notifyDataSetChanged();
            }
        });
    }
}
