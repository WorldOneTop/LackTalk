package com.example.lacktalk;

import android.content.Context;
import android.database.Cursor;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Dao;
import androidx.room.Database;
import androidx.room.Delete;
import androidx.room.Entity;
import androidx.room.Insert;
import androidx.room.PrimaryKey;
import androidx.room.Query;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.Update;

import java.util.List;

@Entity//테이블 만드는 부분같음
class db_User {
    @Override
    public String toString() {
        return "db_User{" +
                "user_num=" + user_num +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", picture='" + picture + '\'' +
                ", msg='" + msg + '\'' +
                '}';
    }

    public db_User(){};
    public db_User(int a){user_num= a;};//검색시 생성
    public db_User(String a,String b,String c,String d){
        id = a; name = b; picture = c; msg = d;
    }
    @PrimaryKey(autoGenerate = true)
    public int user_num;
    public String id;
    public String name;
    public String picture;
    public String msg;
}
@Entity
class  db_Room{
    @Override
    public String toString() {
        return "db_Room{" +
                "room_num=" + room_num +
                ", room_num_server=" + room_num_server +
                ", room_user='" + room_user + '\'' +
                ", room_name='" + room_name + '\'' +
                ", room_picture='" + room_picture + '\'' +
                ", room_innerpicture='" + room_innerpicture + '\'' +
                ", room_alarm=" + room_alarm +
                '}';
    }

    public db_Room(){}
    public db_Room(int a){room_num_server = a;}
    public db_Room(int server_num,String users){
        room_num_server = server_num;
        room_user = users;
        String []temp = users.split("/");
        String resultName;
        if(temp.length>2){//단톡방
            resultName = "단톡방 "+temp.length+"명";
        }else{
            if(temp[0].equals(Intro.ID))
                resultName = AppDatabase.getInstance(ChatList.CONTEXT).myDao().getNickname(temp[1]);
            else
                resultName = AppDatabase.getInstance(ChatList.CONTEXT).myDao().getNickname(temp[0]);
        }
        room_name = resultName;
        room_alarm = 1;
        startRead = 0;
    }
    @PrimaryKey(autoGenerate = true)
    public int room_num;    //내부디비에서만 쓰는 방번호
    public int room_num_server;//서버에 연동될 방 번호
    public String room_user;    //방에 참가한 유저
    public String room_name;    //방의 이름
    public String room_picture; //방의 사진(아이콘)
    public String room_innerpicture; //방 내부 사진
    public int room_alarm;//알람설정되어있는지 1이면 알람 0이면 알람X
    public int startRead;//읽기 시작해야하는곳(recode_)
}
@Entity
class  db_Recode {
    @Override
    public String toString() {
        return "db_Recode{" +
                "recode_num=" + recode_num +
                ", recode_room=" + recode_room +
                ", recode_amount=" + recode_amount +
                ", recode_who='" + recode_who + '\'' +
                ", recode_date='" + recode_date + '\'' +
                ", recode_text='" + recode_text + '\'' +
                ", recode_type=" + recode_type +
                '}';
    }

    public db_Recode(){}
    public db_Recode(int a){recode_num = a;}
    public db_Recode(int a,int b,String c,String d,String e,int f,int g){
        recode_room = a; recode_amount = b; recode_who = c; recode_date = d; recode_text = e; recode_type = f; recode_numServer=g;
    }
    @PrimaryKey(autoGenerate = true)
    public int recode_num;      //내부에서쓰일 채팅의 번호
    public int recode_room;     //서버에 연동된 방 번호
    public int recode_amount;   //안읽은 사람의 양
    public String recode_who;   //쓴사람
    public String recode_date;  //언제씀
    public String recode_text;  //뭐라씀
    public int recode_type;     //뭐를씀  글자면 1 이미지면 2 파일은 3
    public int recode_numServer;//채팅의 서버번호
}


@Dao
interface MyDao {
    @Insert     //친구 추가
    void insertUser(db_User user);
    @Insert     //채팅방 생성
    long insertRoom(db_Room room);//마지막행의 숨은칼럼인rowid(int자동증가)가 반환됨(PK와같음)
    @Insert     //채팅 생성
    void insertRecode(db_Recode recode);
    @Update     //친구의 메시지 사진 변경    기본키로 조회한다고 함
    void updateUser(db_User user);
    @Delete     //친구 삭제
    void deleteUser(db_User user);
    @Delete     //채팅방 나가기
    void deleteRoom(db_Room room);

    @Query("DELETE FROM db_User") //친구 전부 삭제(다른아이디로그인)
    void deleteUserAll();
    @Query("DELETE FROM db_Recode")//기록 삭제
    void deleteRecodeAll();
    @Query("DELETE FROM db_Room")//채팅방 삭제
    void deleteRoomAll();

    @Query("DELETE FROM db_recode WHERE recode_num = :roomNum")//나간 채팅방 내역 지우기
    void deleteRoomChat(int roomNum);

    @Query("UPDATE db_user SET name = :name WHERE user_num=:num;")//친구 이름 바꾸기
    void updateFriendName(int num,String name);

    @Query("SELECT * FROM db_User")
    List<db_User> getUserAll();

    @Query("SELECT * FROM db_User WHERE id =:id")
    db_User getUserSame(String id);

    @Query("SELECT * FROM db_User WHERE  user_num= :userNum")
    db_User getUser(int userNum);                   //친구 정보 가져오기

    @Query("SELECT * FROM db_Recode WHERE recode_room = :roomNum")
    List<db_Recode> getChatAll(int roomNum);        //해당 채팅방 내역 전체 갖고오기

    @Query("SELECT room_user FROM db_Room  WHERE room_num = :roomNum")
    String getInUser(int roomNum);                  //해당 방의 유저만 보기

    @Query("SELECT user_num FROM db_user WHERE id = :id")
    int isFriend(String id);

    @Query("SELECT ro.room_picture, ro.room_name , re.recode_text, ro.room_num_server, re.recode_date, ro.room_user,re.recode_type FROM db_Room as ro,db_Recode as re " +
            "WHERE ro.room_num_server = re.recode_room group by ro.room_num_server ORDER BY re.recode_date DESC")
    Cursor getChatLastList();                 //전체적인 채팅방뷰 갖고오기(ChatList)
    @Query("SELECT COUNT(room_num) FROM db_recode as re,db_room as ro WHERE re.recode_room = ro.room_num_server AND re.recode_num=:roomNum AND re.recode_numServer >ro.startRead ")
    int getListAmount(int roomNum);//방의 안읽은 수 알고싶을때
    @Query("SELECT picture FROM db_User WHERE  id=:id")
    String getUser(String id);                   //친구 정보 가져오기


    @Query("SELECT u.picture,u.name,re.recode_text,re.recode_date,re.recode_amount,re.recode_type,u.id,recode_numServer FROM db_recode as re, db_User as u WHERE re.recode_who = u.id AND re.recode_room =:roomNum ORDER BY re.recode_date ")
    Cursor getChatInRoom(int roomNum);                 //해당 방에 필요한 채팅 정보
    @Query("SELECT u.picture,u.name,re.recode_text,re.recode_date,re.recode_amount,re.recode_type,u.id,recode_numServer FROM db_recode as re, db_User as u WHERE re.recode_who = u.id AND re.recode_room =:roomNum ORDER BY re.recode_date DESC LIMIT 1")
    Cursor getChatInRoomLast(int roomNum);                 //해당 방에 필요한 마지막 채팅 정보
    @Query("SELECT name FROM db_User WHERE id=:id")
    String getNickname(String id);
    @Query("SELECT room_num_server FROM db_Room WHERE room_num_server = :roomNum")
    int roomExists(int roomNum);
    @Query("UPDATE db_Room SET startRead = :start WHERE room_num_server = :roomNUm")
    void updateRead(int start,int roomNUm);
    @Query("UPDATE db_User SET picture = :img WHERE id = :id")
    void updateProfile(String id, String img);


    @Query("SELECT startRead FROM db_Room WHERE room_num_server = :roomNum")
    int getStartRead(int roomNum);
    @Query("UPDATE db_room SET startRead = (SELECT recode_numServer FROM db_Recode WHERE recode_room = :roomNum ORDER BY recode_numServer DESC LIMIT 1) WHERE room_num_server = :roomNum")
    void setListAmount(int roomNum);//방 들어갔을때 실행

    @Query("SELECT recode_num,recode_amount FROM db_Recode WHERE recode_room = :roomNum AND recode_numServer >:start")
    Cursor getAmount(int roomNum,int start);
    @Query("UPDATE db_Recode SET recode_amount =:amount WHERE recode_num = :pNum")
    void setAmount(int pNum,int amount);


    @Query("SELECT * FROM db_Room")
    List<db_Room> getRoomAll();
    @Query("SELECT * FROM db_Recode")
    List<db_Recode> getRecodeAll();
}
@Database(entities = {db_User.class,db_Room.class,db_Recode.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract MyDao myDao();
    private static AppDatabase instance;
    private static final Object sLock = new Object();
    public static AppDatabase getInstance(Context context) {
        synchronized (sLock) {
            if(instance==null) {
                instance=Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase.class, "Users.db")
                        .build();
            }
            return instance;
        }
    }
}