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
public class NodeJS {//싱글톤 클래스
    private String HOST;
    private int PORT;
    public static int STATUS;//0은 disconnect, 1은 connect, 2는 error
    private static Socket socket;

    public static boolean isRecv;
    private static boolean recvBoolean;//로그인체크,아이디중복체크,서버ip옮겨졌는지 체크

    public static boolean isRecv_msg;
    private static JSONObject recvMsg;

    private NodeJS(){
        HOST="http://192.168.219.154:"; PORT = 12345;  isRecv =false; isRecv_msg=false;
    }
    private static class SingletonHolder {
        public static final NodeJS INSTANCE = new NodeJS();
    }
    public static NodeJS getInstance() {
        return SingletonHolder.INSTANCE;
    }
    public void setHostStart(String str){
        socket.close();
        HOST = "http://"+str+":";
        start();
    }
    public void start() {
        /* init  부분*/
        try {
            socket = IO.socket(HOST+PORT);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        /* default 설정 부분*/
        socket.close();//세팅후 재시작일 수 있어서
        socket.on("msg", onMessage);
        socket.on(Socket.EVENT_CONNECT,onConnect);
        socket.on(Socket.EVENT_DISCONNECT,onDisconnect);
        socket.on(Socket.EVENT_RECONNECT_ERROR,onError);
        socket.on("onBoolean",onBoolean);   //불린형 체크만을 위해서

        STATUS = 0;


        socket.connect();
    }
    public void sendJson(String key, JSONObject value){
        socket.emit(key,value);
        Log.d("asd","보냄 status : "+STATUS);
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
