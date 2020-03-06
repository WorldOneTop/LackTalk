var app = require('express');		
var http = require('http').Server(app);
var io = require('socket.io')(http);
var mysql = require('mysql');

var connection = mysql.createConnection({
    host: 'localhost',
    post: 12345,
    user: 'nodejs',
    password: 'nodejs',
    database: 'fornodejs'
});

connection.connect();




 

io.on('connection', function(socket){
    console.log('a user connected');
    socket.on('disconnect', function(){
      console.log('user disconnected');
    });
 
    socket.on('msg', function(msg){
        console.log('message: ' + msg.asd);
        io.emit('msg', msg);
    });
});
 
http.listen(12345, function(){
  console.log('listening on *:12345');
});