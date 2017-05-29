var express = require('express')
   ,http = require('http')
   ,bodyParser = require('body-parser');
var app = express();
var mysql = require('mysql');
var connection = mysql.createConnection({
        host    : 'localhost',
        user    : 'root',
        password : 'QgC5R05xP9Nt',
        port    : '3306',
        database : 'bitnami_wordpress'
});

connection.query('SELECT longitude, latitude from 차량사고', function(err, rows, fields){
        var objs = [];
        if(!err){i
                for(var i = 0; i < rows.length; i++){
                        objs.push({longitude: rows[i].longitude, latitude: rows[i].latitude});
                        console.log('longitude: ', objs[i].longitude);
                        console.log('latitude: ', objs[i]. latitude);
                        }
                console.log(typeof JSON.stringify(objs));
                }
        else
                console.log('Error ', err);
        });

app.use(bodyParser.urlencoded({extended: true}));
app.use(bodyParser.json());
// 앱에서 사고 신고 받을 때의 처리 함수.
app.post("/", function(req, res){
        console.log('POST request');
        var username = req.body.username;
        var longitude = req.body.longitude;
        var latitude = req.body.latitude;
        console.log(username);
        console.log("경도 : "+ longitude);
        console.log("위도 : "+ latitude);

        var accountObj = {
        success:true
        }
        var accountStr = JSON.stringify(accountObj);
        res.end(accountStr);
});
app.get("/", function(req, res){
        console.log('GET request');
        connection.query('SELECT longitude, latitude from 차량사고', function(err, rows, fields){
        var objs = [];
        if(!err){
                for(var i = 0; i < rows.length; i++){
                        objs.push({longitude: rows[i].longitude, latitude: rows[i].latitude});
                        }
                var jsonObject = {GpsPointList: objs};
                res.end(JSON.stringify(jsonObject));
        }
        else
                console.log('Error ', err);
        });
});

app.use(function( req, res){
    res.writeHead(404,{"Content-Type" : "text/plain"});
    res.end('404 ERROR');
});
http.createServer(app).listen(8080);

