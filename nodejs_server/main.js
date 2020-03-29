var app = require('express');		
var http = require('http').Server(app);
var io = require('socket.io')(http);
var mysql = require('mysql');

var sql = mysql.createConnection({
	host: 'localhost',
	post: 12345,
	user: 'nodejs',
	password: 'nodejs',
	database: 'nodejs',
	charset : 'utf8mb4'
});

sql.connect();


io.on('connection', function(socket){	//연결 되면 이벤트 설정
	console.log('a user connected');
    socket.on('disconnect', function(){//연결해제 이벤트
    	console.log('user disconnected');
    });
	socket.on('login', function(msg){//로그인 확인 이벤트
		sql.query("SELECT * FROM user WHERE  user.id = '"+msg.id+"' and user.pw = '"+msg.pw+"';", function (error, results, fields) {
			if (error) {
				io.emit('onBoolean'+msg.myID,false);
			} 
			else { //결과는 배열인덱스.키값으로 접근 없으면 배열.length가 0
				if(results.length != 0){//로그인 정보가 있다면 나머지 정보도 전송
					io.emit('msg'+msg.myID,results[0]);
					// console.log("로그인 "+JSON.stringify(results[0]));
				}
			io.emit('onBoolean'+msg.myID,results.length != 0);//결과가 0이면 아이디가 X이므로 false
		}
	});
	});

	socket.on('signup',function(msg){//아이디 중복 확인 및 회원가입 이벤트
		signup_callback(msg.id,msg.pw,function(val_bool){
			io.emit('onBoolean'+msg.myID,val_bool);
		});
	});
	socket.on('msg', function(msg){
		io.emit('msg'+msg.myID, msg);

	});
	socket.on('userInfo',function(msg){
		sql.query("SELECT id,name,picture,msg FROM user WHERE id='"+msg.id+"'; ",function(error,results,fields){
			io.emit('userInfo'+msg.myID,results[0]);
		});
	});
	socket.on('userUpdate',function(msg){
		sql.query("UPDATE user SET name = ?,msg=? WHERE id='"+msg.id+"'; ",[msg.name,msg.msg],function(error,results,fields){if(error) console.log("error"+error);
			console.log(msg.name,msg.msg);
		});
	});
	socket.on('addFriend',function(msg){
		sql.query("INSERT INTO user_friend(id_me,id_friend,name_friend) VALUES(?,?,?);",[msg.me,msg.friend,msg.name],function(error,results,fields){
		});//위는 그냥 친구추가 아래는 상대방도 자동으로 추가시킴 (없다면)
		sql.query("SELECT * FROM user_friend WHERE id_me = ? AND id_friend = ?",[msg.friend,msg.me], function(error,results,fields){
			if(results[0] == null){
				sql.query("INSERT INTO user_friend(id_me,id_friend,name_friend) VALUES(?,?,?);",[msg.friend,msg.me,msg.myname],function(error,results,fields){//로그인할때 첨들어갈때만 초기화됨
					sql.query("SELECT * FROM user WHERE id = '"+msg.me+"';",[msg.friend,msg.me,msg.myname],function(error,results,fields){
						if(results[0].picture == null)
							results[0].picture = "";
						io.emit("addFriend"+msg.friend,results[0]);
					});	
				});	
			}


		});
	});
	socket.on('getFriend',function(msg){
		sql.query("SELECT user.id, user_friend.name_friend, user.picture, user.msg FROM user, user_friend WHERE user.id = user_friend.id_friend AND user_friend.id_me = '"+msg.id+"';",function(error,results,fields){
			io.emit('getFriend'+msg.myID,results);
		});
	});
	socket.on('updateFriendName',function(msg){
		sql.query("UPDATE user_friend SET name_friend = ? WHERE id_me = ?",[msg.name,msg.id],function(error,results,fields){
		});
	});
	socket.on('deleteFriend',function(msg){
		sql.query("DELETE FROM user_friend WHERE id_me= ? AND id_friend = ?;",[msg.me,msg.friend],function(error,results,fields){
		});
	});
	socket.on('addChatRoom',function(msg){
		sql.query("INSERT INTO chatroom(room_user) VALUES(?);",[msg.users],function(error,results,fields){
			sql.query("SELECT room_num FROM chatroom ORDER BY room_num DESC LIMIT 1;",function(error,results,fields){
				io.emit("addChatRoom"+msg.myID,results[0].room_num);
			});
		});
	});
	socket.on('addChatRecode',function(msg){//채팅 친걸 서버로 보냈을때
		sql.query("INSERT INTO chatrecode(room_num,who,date,text,type) VALUES(?,?,?,?,?);",[msg.a,msg.c,msg.d,msg.e,msg.f],function(error,results,fields){//채팅레코드 추가
			if(error)console.log("에러 : "+error);
			sql.query("SELECT recode_num FROM chatrecode ORDER BY recode_num DESC LIMIT 1;",function(error2,results2,fields2){//추가한 레코드 넘버 확인
				var jsonobj = new Object();
				jsonobj.num = msg.a;
				jsonobj.amount = msg.b;
				jsonobj.id = msg.c;
				jsonobj.time = msg.d;
				jsonobj.type = msg.f;
				jsonobj.server = results2[0].recode_num;

				userName_callback(msg.isCreate,msg.e,msg.c ,function(result){
					jsonobj.text = result;
					console.log(result);
					for(var i of msg.g.split("/")){
						io.emit("sendChatting"+i,jsonobj);
					}
				});

			});
		});
	});
	socket.on('readChat',function(msg){
			io.emit("readChat",msg);//msg.roomNum, msg.start;
		});
	socket.on('initChatRoom',function(msg){
		sql.query("SELECT * FROM chatroom",function(error,results,fields){
			var jsonArr = new Array();
			for(var i in results){
				var array = results[i].room_user.split('/');
				for(var j in array){
					if(array[j] == msg.id){
						var jsonobj = new Object();
						jsonobj.num = results[i].room_num;
						jsonobj.user = results[i].room_user;
						jsonArr.push(jsonobj);
						break;
					}
				}
			}
			io.emit('initChatRoom'+msg.myID,jsonArr);
		});
	});
	socket.on('createRoomID',function(msg){
		sql.query("SELECT * FROM chatroom WHERE room_num = '"+msg.num+"';",function(error,results,fields){ io.emit('createRoomID'+msg.myID,results[0]);
	});
	});
	socket.on('updateProfile',function(msg){
		sql.query("UPDATE user SET picture = ? WHERE id = ? ;",[msg.img,msg.id],function(error,results,fields){ 
			console.log(msg.img,msg.id);
			io.emit('updateProfile',msg);
		});
	});


});
signup_callback = function(id,pw,callback){
	sql.query("SELECT * FROM user WHERE  user.id = '"+id+"';", function (error, results, fields) { 
		if (error) {
			callback(false);
		} else { //결과는 배열인덱스.키값으로 접근 없으면 배열.length가 0
			if(results.length == 0){//하려는 아이디가 없다면 insert
				sql.query("insert into user(id,pw) values('"+id+"','"+pw+"');", function (error, results, fields) { });//쿼리문도 콜백형식
				callback(true);
			}else{
				callback(false);
			}
		}
	});
};
userName_callback = function(isCreate,msg,who,callback){
	if(isCreate){
		sql.query("SELECT name,id FROM user",function (error, results, fields) {
			var resultMsg = "제가 ";
			var users = msg.split("/");
			for(var userID of users){//받은 유저 모두 검사
				if(userID == who) 
					continue;//나면 넘어감
				for(var column of results){//유저아이디에 해당하는 닉네임 찾기
					if(column.id == userID){
						resultMsg += column.name+" 님, ";
						break;
					}
				}
			}
			resultMsg = resultMsg.substring(0,resultMsg.length -2);
			resultMsg += "을 초대하였습니다.";
			callback(resultMsg);
		});
	}else{
		callback(msg);
	}
};
http.listen(12345, function(){
	console.log('listening on *:12345');
});

// mysql 쿼리문, char set utf-8, varchar(n)은 바이트수가 아닌 글자 수,외래키사용 데이터관리주의

// create database nodejs;
// CREATE USER nodejs@'localhost' IDENTIFIED BY 'nodejs'
// grant all privileges on nodejs.* to nodejs@'localhost';

// CREATE TABLE user(
// 	user_num int AUTO_INCREMENT PRIMARY KEY,
// 	id varchar(30) NOT NULL,
// 	pw char(64) NOT NULL,
// 	name varchar(20) DEFAULT 'visitor',
// 	picture TEXT ,
// 	msg varchar (40) DEFAULT ''
// );

// CREATE TABLE chatroom(
// 	room_num INT AUTO_INCREMENT PRIMARY KEY,
// 	room_user TEXT
// );

// CREATE TABLE chatrecode(
// 	recode_num INT AUTO_INCREMENT PRIMARY KEY,
// 	room_num INT,
// 	who varchar(30) NOT NULL,
// 	date char(19) NOT NULL,
// 	text TEXT NOT NULL,
// 	type TINYINT NOT NULL,
// 	FOREIGN KEY (room_num) REFERENCES chatroom(room_num)
// );

// CREATE TABLE user_friend(
// 	friend_num INT AUTO_INCREMENT PRIMARY KEY,
// 	id_me varchar(30) NOT NULL,
// 	id_friend varchar(30) NOT NULL,
// 	name_friend varchar(20) NOT NULL
// );



//type : 1-문자 , 2-이미지 , 3-파일