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
public class NodeJS {
    public static String HOST="http://192.168.219.154:";
    public static int PORT = 12345;
    public static int STATUS;//0은 disconnect, 1은 connect, 2는 error
    private static Socket socket;

    public void setHost(String str){
        SocketTest.HOST = "http://"+HOST+":";
        start();
    }
    public void start() {
        /* init  부분*/
        try {
            socket = IO.socket("http://192.168.219.154:12345");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        /* default 설정 부분*/
        socket.close();//세팅후 재시작일 수 있어서
        socket.on("msg", onMessage);
        socket.on(Socket.EVENT_CONNECT,onConnect);
        socket.on(Socket.EVENT_DISCONNECT,onDisconnect);
        socket.on(Socket.EVENT_RECONNECT_ERROR,onError);

        STATUS = 0;


        socket.connect();
    }

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            STATUS = 1;
        }
    };
    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            STATUS = 0;
        }
    };
    private Emitter.Listener onError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            STATUS = 2;
        }
    };
    private Emitter.Listener onMessage = new Emitter.Listener() {//.on리스너에 설정된거
        @Override
        public void call(Object... args) {
            try {
//                    JSONObject json = new JSONObject((String)args[0]); json.put("키","값");
                Log.d("asd",((JSONObject)args[0]).getString("asd"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };




}
