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
		sql.query("SELECT * FROM user WHERE  user.id = '"+msg.id+"' and user.pw = '"+msg.pw+"';", function (error, results, fields) {console.log(msg.myID+"AAAAAAAAAAAAAA");
			if (error) {
				io.emit('onBoolean'+msg.myID,false);
			} 
			else { //결과는 배열인덱스.키값으로 접근 없으면 배열.length가 0
				if(results.length != 0){//로그인 정보가 있다면 나머지 정보도 전송
					io.emit('msg'+msg.myID,results[0]);
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
		sql.query("UPDATE user SET name = ?,picture=?,msg=? WHERE id='"+msg.id+"'; ",[msg.name,msg.picture,msg.msg],function(error,results,fields){if(error) console.log("tq");
			console.log(msg.name);
		});
	});
	socket.on('addFriend',function(msg){
		sql.query("INSERT INTO user_friend(id_me,id_friend,name_friend) VALUES(?,?,?);",[msg.me,msg.friend,msg.name],function(error,results,fields){
		});//위는 그냥 친구추가 아래는 상대방도 자동으로 추가시킴 (없다면)
		sql.query("SELECT * FROM user_friend WHERE id_me = ? AND id_friend = ?",[msg.friend,msg.me], function(error,results,fields){
			if(results=="[]"){
				sql.query("INSERT INTO user_friend(id_me,id_friend,name_friend) VALUES(?,?,?);",[msg.friend,msg.me,msg.myname],function(error,results,fields){
					io.emit('addFriend'+msg.myID,"친추요청옴");
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
	socket.on('addChatRecode',function(msg){
		sql.query("INSERT INTO chatrecode(room_num,amount,who,date,text,type) VALUES(?,?,?,?,?,?);",[msg.a,msg.b,msg.c,msg.d,msg.e,msg.f],function(error,results,fields){
		});
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
			}console.log(jsonArr);
			io.emit('initChatRoom'+msg.myID,jsonArr);
		});
	});
});

login_callback = function(id,pw,myID, callback){
	sql.query("SELECT * FROM user WHERE  user.id = '"+id+"' and user.pw = '"+pw+"';", function (error, results, fields) {
		if (error) {
			callback(false);
		} else { //결과는 배열인덱스.키값으로 접근 없으면 배열.length가 0
			if(results.length != 0){//로그인 정보가 있다면 나머지 정보도 전송
				io.emit('msg'+myID,results[0]);
			}
			callback(results.length != 0);	//결과가 0이면 아이디가 X이므로 false

		}
	});
};
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
// 	picture varchar(40) DEFAULT '',
// 	msg varchar (40) DEFAULT ''
// );

// CREATE TABLE chatroom(
// 	room_num INT AUTO_INCREMENT PRIMARY KEY,
// 	room_user TEXT
// );

// CREATE TABLE chatrecode(
// 	recode_num INT AUTO_INCREMENT PRIMARY KEY,
// 	room_num INT,
// 	amount SMALLINT NOT NULL,
// 	who varchar(30) NOT NULL,
// 	date TIMESTAMP NOT NULL,
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

//type : 1-문자 , 2-이미지 , 3-파일  ,amount-읽지않은사람의양,|가 사람 나누는 기준


// 콜백함수 기본 형태, 선언 후 plus실행시 인자값의 함수가 실행되면서 log실행
// plus = function(a, b, callback){
//   var result = a+b
//   callback(result);
// }
 // 
// plus(5,10, function(res) { console.log(res);});

