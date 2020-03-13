package com.example.lacktalk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
public class NodeJS {//싱글톤 클래스
    public static String HOST;
    private int PORT;
    public static int STATUS;//0은 disconnect, 1은 connect, 2는 error, 3은 연결 시도중
    private static Socket socket;

    public static boolean isRecv;
    private static boolean recvBoolean;//로그인체크,아이디중복체크,서버ip옮겨졌는지 체크

    public static boolean isRecv_msg;
    private static JSONObject recvMsg;

    private NodeJS(){
        PORT = 12345;  isRecv =false; isRecv_msg=false; STATUS = 0;
    }
    private static class SingletonHolder {
        public static final NodeJS INSTANCE = new NodeJS();
    }
    public static NodeJS getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void setHostStart(String str,Context context){
        socket.close();
        try {
            context.openFileOutput(Intro.FILENAME_IP,Context.MODE_PRIVATE).write(str.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        HOST = str;
        start(context);
    }
    public void start(Context context) {
        try {
            if(!(new File(context.getFilesDir(), Intro.FILENAME_IP).exists()))
                context.openFileOutput(Intro.FILENAME_IP, Context.MODE_PRIVATE).write("192.168.137.126".getBytes());

            byte temp[] = new byte[15];
            context.openFileInput(Intro.FILENAME_IP).read(temp);
            HOST = new String(temp);
            Log.d("asd","Ip : "+new String(temp));
        } catch (Exception e) {
            e.printStackTrace();
        }
        /* init  부분*/
        try {
            socket = IO.socket("http://"+HOST+":"+PORT);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        /* default 설정 부분*/
        socket.on("msg", onMessage);
        socket.on(Socket.EVENT_CONNECT,onConnect);
        socket.on(Socket.EVENT_DISCONNECT,onDisconnect);
        socket.on(Socket.EVENT_RECONNECT_ERROR,onError);
        socket.on("onBoolean",onBoolean);   //불린형 체크만을 위해서

        socket.connect();
    }
    public void sendJson(String key, JSONObject value){
        socket.emit(key,value);
    }
    public static boolean getRecvBoolean(){
        if(!isRecv) return false; //안읽었다면 그냥 false로
        isRecv = false;//읽었다는 표시
        return recvBoolean;
    }
    public static JSONObject getMsg(){
        if(!isRecv_msg) return null; //안읽었다면 그냥 null로
        isRecv_msg = false;//읽었다는 표시
        return recvMsg;
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
                recvMsg = (JSONObject) args[0];
                isRecv_msg= true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    private Emitter.Listener onBoolean = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            recvBoolean = (boolean)args[0];
            isRecv = true;
        }
    };



}
