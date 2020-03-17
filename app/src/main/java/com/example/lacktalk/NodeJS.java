package com.example.lacktalk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.json.JSONArray;
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
    private static Socket socket;
    public static boolean recvBoolean;//로그인체크,아이디중복체크,서버ip옮겨졌는지 체크
    public static JSONObject recvMsg;
    public static JSONObject recvUserInfo;



    private static class SingletonHolder {
        public static final NodeJS INSTANCE = new NodeJS();
    }
    public static NodeJS getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private NodeJS(){
        PORT = 12345;
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
            File tempFile =new File(context.getFilesDir(), Intro.FILENAME_IP);
            if(!tempFile.exists())
                context.openFileOutput(Intro.FILENAME_IP, Context.MODE_PRIVATE).write("192.168.137.126".getBytes());

            char temp[] = new char[15];//길어도 15
            int tempLen = new FileReader(tempFile).read(temp);
            HOST = String.valueOf(temp,0,tempLen);
            Log.d("asd",tempLen+"Ip : "+HOST);
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
        socket.io().timeout(2500);
        socket.on("msg", onMessage);
        socket.on(Socket.EVENT_CONNECT,onConnect);
        socket.on(Socket.EVENT_DISCONNECT,onDisconnect);
        socket.on(Socket.EVENT_RECONNECT_ERROR,onDisconnect);
        socket.on("onBoolean",onBoolean);   //불린형 체크만을 위해서
        socket.on("userInfo",userInfo);

        socket.connect();
    }
    public static void sendJson(String key, JSONObject value){
        socket.emit(key,value);
    }
    public static boolean getRecvBoolean(){
        return recvBoolean;
    }
    public static JSONObject getMsg(){
        return recvMsg;
    }

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if(Intro.eventConnect != null)
                Intro.eventConnect.onConnect();
        }
    };
    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if(Intro.eventConnect != null)
                Intro.eventConnect.onDisconnect();
        }
    };
    private Emitter.Listener onMessage = new Emitter.Listener() {//.on리스너에 설정된거
        @Override
        public void call(Object... args) {
            try {
                recvMsg = (JSONObject) args[0];
                if(Intro.eventMessage != null)
                    Intro.eventMessage.messageArrive();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    private  Emitter.Listener onBoolean = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            recvBoolean = (boolean)args[0];
            if(Intro.eventBoolean != null)
                Intro.eventBoolean.messageArrive();
        }
    };
    private  Emitter.Listener userInfo = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            recvUserInfo = (JSONObject)args[0];
            if(Intro.eventUserInfo != null)
                Intro.eventUserInfo.messageArrive();
        }
    };

}
