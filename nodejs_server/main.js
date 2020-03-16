var app = require('express');		
var http = require('http').Server(app);
var io = require('socket.io')(http);
var mysql = require('mysql');

var sql = mysql.createConnection({
	host: 'localhost',
	post: 12345,
	user: 'nodejs',
	password: 'nodejs',
	database: 'nodejs'
});

sql.connect();

sql.query("SELECT id,picture,msg FROM user; ",function(error,results,fields){
		console.log(results);
	});

io.on('connection', function(socket){	//연결 되면 이벤트 설정
	console.log('a user connected');
    socket.on('disconnect', function(){//연결해제 이벤트
    	console.log('user disconnected');
    });

	socket.on('login', function(msg){//로그인 확인 이벤트
		login_callback(msg.id,msg.pw,function(val_bool){
			io.emit('onBoolean',val_bool);
		})
	});

	socket.on('signup',function(msg){//아이디 중복 확인 및 회원가입 이벤트
		signup_callback(msg.id,msg.pw,function(val_bool){
			io.emit('onBoolean',val_bool);
		})
	});
	socket.on('msg', function(msg){
		io.emit('msg', msg);
	});
	socket.on('userInfo',function(msg){
		sql.query("SELECT id,name,picture,msg FROM user WHERE id='"+msg.id+"'; ",function(error,results,fields){
			io.emit('userInfo',results[0]);
			
		});
	});


});
login_callback = function(id,pw, callback){
	sql.query("SELECT * FROM user WHERE  user.id = '"+id+"' and user.pw = '"+pw+"';", function (error, results, fields) { 
		if (error) {
			callback(false);
		} else { //결과는 배열인덱스.키값으로 접근 없으면 배열.length가 0
			if(results.length != 0){//로그인 정보가 있다면 나머지 정보도 전송
				io.emit('msg',results[0]);
			}
			callback(results.length != 0);	//결과가 0이면 아이디가 X이므로 false

		}
	});
}
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
}
userInfo_callback = function(id){
	sql.query("SELECT id,picture,msg FROM user WHERE id='"+id+"'; ",function(error,results,fields){
		io.emit('userInfo',results[0]);
	});
}
// function signup(id,pw){
// 	sql.query("SELECT * FROM user WHERE  user.id = '"+id+"';", function (error, results, fields) {  //조회
// 		if (error) {
// 			console.log(error);
// 		} else {
//         console.log(results);  //결과 출력(간혹 커넥션이 끊어졌다는 오류가 나올때가 있다.)
//     }
// });

// }

// //result[n].column
// SELECT * FROM user WHERE EXISTS ( SELECT * FROM user WHERE user.key = user.key )
// connection.query("select * from test", function (error, results, fields) {  //조회
// 	if (error) {
// 		console.log(error);
// 	} else {
//         console.log(results);  //결과 출력(간혹 커넥션이 끊어졌다는 오류가 나올때가 있다.)
//     }
// });

// insert into user values('id','password');


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
// 	name varchar(40) DEFAULT 'visitor',
// 	picture varchar(40) DEFAULT '',
// 	msg varchar (40) DEFAULT ''
// );

// CREATE TABLE chatroom(
// 	room_num INT AUTO_INCREMENT PRIMARY KEY,
// 	room_user varchar(200)
// );

// CREATE TABLE chatrecode(
// 	recode_num INT AUTO_INCREMENT PRIMARY KEY,
// 	room_num INT,
// 	amount INT NOT NULL,
// 	who INT,
// 	date DATETIME NOT NULL,
// 	text TEXT NOT NULL,
// 	type INT NOT NULL,
// 	FOREIGN KEY (room_num) REFERENCES chatroom(room_num),
// 	FOREIGN KEY (who) REFERENCES user(user_num)
// );
//type : 1-문자 , 2-이미지 , 3-파일  ,amount-읽지않은사람의양


// 콜백함수 기본 형태, 선언 후 plus실행시 인자값의 함수가 실행되면서 log실행
// plus = function(a, b, callback){
//   var result = a+b
//   callback(result);
// }
 // 
// plus(5,10, function(res) { console.log(res);});

