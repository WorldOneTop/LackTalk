package com.example.lacktalk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.database.Cursor;
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
import java.text.SimpleDateFormat;
import java.util.Date;

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
    public static JSONArray recvFriendList;
    public static JSONArray recvChatList;
    public static int recvInt;
    public static JSONObject recvCreateRoom;
    public static JSONObject recvChatting;


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
    public synchronized void start(Context context) {
        if(socket != null && socket.connected())
            socket.close();
        try {
            File tempFile =new File(context.getFilesDir(), Intro.FILENAME_IP);
            if(!tempFile.exists())
                context.openFileOutput(Intro.FILENAME_IP, Context.MODE_PRIVATE).write("192.168.219.154".getBytes());

            char temp[] = new char[15];//길어도 15
            int tempLen = new FileReader(tempFile).read(temp);
            HOST = String.valueOf(temp,0,tempLen);
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
        socket.on("msg"+Intro.ID, onMessage);
        socket.on(Socket.EVENT_CONNECT,onConnect);
        socket.on(Socket.EVENT_DISCONNECT,onDisconnect);
        socket.on(Socket.EVENT_RECONNECT_ERROR,onDisconnect);
        socket.on("onBoolean"+Intro.ID,onBoolean);   //불린형 체크만을 위해서
        socket.on("userInfo"+Intro.ID,userInfo);
        socket.on("getFriend"+Intro.ID,getFriend);
        socket.on("addChatRoom"+Intro.ID,addChatRoom);
        socket.on("initChatRoom"+Intro.ID,initChatRoom);
        socket.on("addFriend"+Intro.ID,addFriend);
        socket.on("sendChatting"+Intro.ID,sendChatting);
        socket.on("createRoomID"+Intro.ID,createRoomID);
        socket.on("readChat",readChat);
        socket.on("updateProfile",updateProfile);

        socket.connect();
    }
    public static void sendJson(String key, JSONObject value){
            try {
                value.put("myID",Intro.ID);
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
    private Emitter.Listener getFriend = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if(args[0].toString().equals("[]")){//노드에서 result[0]이 아닌 results 전체를 반환해서 이런식으로 체크
                recvFriendList = null;
            }else{
                recvFriendList = (JSONArray) args[0];
            }
            if(Intro.eventGetFriend != null)
                Intro.eventGetFriend.messageArrive();
        }
    };
    private Emitter.Listener addChatRoom = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            recvInt = (int)args[0];
            if(Intro.eventAddChatRoom != null)
                Intro.eventAddChatRoom.messageArrive();
        }
    };
    private Emitter.Listener initChatRoom = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if(args[0].toString().equals("[]")){//노드에서 result[0]이 아닌 results 전체를 반환해서 이런식으로 체크
                recvChatList = null;
            }else{
                recvChatList = (JSONArray) args[0];
            }
            if(Intro.eventInitChatRoom != null)
                Intro.eventInitChatRoom.messageArrive();
        }
    };
    private Emitter.Listener addFriend = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject json = (JSONObject) args[0];
            try {
                AppDatabase.getInstance(ChatList.CONTEXT).myDao().insertUser(new db_User(json.getString("id"),json.getString("name"), json.getString("picture"), json.getString("msg")));
                if(ChatList.viewPager_chatList[0] != null)
                    ChatList.viewPager_chatList[0].initFriendList();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
    private Emitter.Listener sendChatting = new Emitter.Listener() {
        @Override
            public void call(Object... args) {
            recvChatting = (JSONObject)args[0];
            if(Intro.recvChatting != null)
                Intro.recvChatting.messageReceive();

        }
    };
    private Emitter.Listener createRoomID = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            recvCreateRoom = (JSONObject)args[0];
            if(Intro.eventCreateRoom != null)
                Intro.eventCreateRoom.messageArrive();
        }
    };
    private Emitter.Listener readChat = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Cursor cursor = null;
            try {
                cursor = AppDatabase.getInstance(ChatList.CONTEXT).myDao().getAmount(((JSONObject)args[0]).getInt("roomNum"),((JSONObject)args[0]).getInt("start"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            while(cursor.moveToNext()){
                AppDatabase.getInstance(ChatList.CONTEXT).myDao().setAmount(cursor.getInt(0),cursor.getInt(1)-1);
            }
            if(Intro.eventRead != null)
                Intro.eventRead.messageRead();

        }
    };

    private Emitter.Listener updateProfile = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject jsonObject = (JSONObject) args[0];
            try {
                if(AppDatabase.getInstance(ChatList.CONTEXT).myDao().isFriend(jsonObject.getString("id")) != 0){//있는사람일경우
                    AppDatabase.getInstance(ChatList.CONTEXT).myDao().updateProfile(jsonObject.getString("id"),jsonObject.getString("img"));
                    ChatList.viewPager_chatList[0].initFriendList();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };
}
