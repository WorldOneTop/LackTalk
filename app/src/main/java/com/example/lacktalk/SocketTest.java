package com.example.lacktalk;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

//EVENT_RECONNECT_ERROR만 감지
public class SocketTest extends AppCompatActivity {
    public static String HOST="http://192.168.219.154:";
    public static int PORT = 12345;

    private Socket socket;
    public void init(){

    }


    {
            try {
                socket = IO.socket("http://192.168.219.154:12345");
                socket.close();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    Button btn;
    Button btn2;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_socket_test);

            btn2 = findViewById(R.id.sendBtn2);

            socket.on("msg", onMessage);
            socket.on(Socket.EVENT_CONNECT,onConnect);
            socket.on(Socket.EVENT_DISCONNECT,onDisconnect);
            socket.on(Socket.EVENT_PING,onPing);

            socket.on(Socket.EVENT_RECONNECT_FAILED, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.d("asd","페일드");
                }
            });
            socket.on(Socket.EVENT_RECONNECT_ERROR, new Emitter.Listener() {
                @Override
                public void call(Object... args) {//서버가 끊겼을때, 주소가 다를때
                    Log.d("asd","에러");
                }
            });
            socket.on(Socket.EVENT_CONNECT_TIMEOUT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.d("asd","타임아웃");
                }
            });




            socket.connect();
            btn = (Button)findViewById(R.id.sendBtn);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    socket.close();
                }
            });
            btn2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        try {
                            socket = IO.socket("http://192.168.219.154:12345");
                        } catch (URISyntaxException e) {
                            e.printStackTrace();
                        }
                        socket.connect();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("asd","켬");
        }
    };
    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("asd","끔");
        }
    };
    private Emitter.Listener onMessage = new Emitter.Listener() {//.on리스너에 설정된거
        @Override
        public void call(Object... args) {
            try {
//                    JSONObject json = new JSONObject((String)args[0]);
                Log.d("asd",((JSONObject)args[0]).getString("asd"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
    private Emitter.Listener onPing = new Emitter.Listener() {//.on리스너에 설정된거
        @Override
        public void call(Object... args) {
            try {
                Log.d("asd","핑??");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };



                            //아이피만 들어온다고 가정
    public void setHOST(String HOST) {
        SocketTest.HOST = "http://"+HOST+":";
    }

}
