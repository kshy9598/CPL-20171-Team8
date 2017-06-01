/* �ۼ� 20170422 ��̼� */
var express = require('express')
        ,http = require('http')
        ,bodyParser = require('body-parser')
        ,fs = require('fs')
        ,moment = require('moment');

var app = express();
var mysql = require('mysql');
var connection = mysql.createConnection({
        host    : 'localhost',
        user    : 'root',
        password : 'QgC5R05xP9Nt',
        port    : '3306',
        database : 'bitnami_wordpress'
});

console.log(moment().format('x'));
connection.query('SELECT longitude, latitude from �������', function(err, rows, fields){
        var objs = [];
        if(!err){
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

app.use(bodyParser.urlencoded({extended: true, limit: '5mb'}));
app.use(bodyParser.json({limit: '5mb'}));

console.log(moment().format('LLLL'));
// �ۿ��� ��� �Ű� ���� ���� ó�� �Լ�.
app.post("/", function(req, res){
        console.log('POST request');
        var username = req.body.username;
        console.log(username);
        var longitude = req.body.longitude;
        console.log("�浵 : "+ longitude);
        var latitude = req.body.latitude;
        console.log("���� : "+ latitude);
        var phone = req.body.phone;
        console.log("��ȭ��ȣ : "+ phone);
        var photo = req.body.photo;
        console.log("���� ���� ");
        var fileName = moment().format('x')+'.jpg';
        console.log(fileName);

        var buf = new Buffer(photo, 'base64');
        fs.writeFile('./images/' + fileName, buf, 'binary', function(err) {
                if(err) {
                    console.log(err);
                } else {
                    console.log("The file was saved!");
                }
        });
        var fileDir = '52.79.214.37:8080/image/' + fileName;
        var data = [username, phone, fileDir, latitude, longitude]
        connection.query('insert into ������� values(date_add(now(), interval +9 hour), ?, ?, ?, ?, ?)',data, function(error, result){
                if(!error){
                        console.log(result);
                }
        });
        var accountObj = {
        success:true
        }
        var accountStr = JSON.stringify(accountObj);
        res.end(accountStr);
});

// �ۿ��� ��� ����Ʈ ��û���� ���� ó�� �Լ�.
app.get("/", function(req, res){
        console.log('GET request');
        connection.query('SELECT longitude, latitude from �������', function(err, rows, fields){
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

// �̹��� ȣ���� �Լ�.
app.get('/image/:name',function (req,res){
    var filename = req.params.name;
    console.log(__dirname+'/images/'+filename);
    fs.exists(__dirname+'/images/'+filename, function (exists) {
        if (exists) {
            fs.readFile(__dirname+'/images/'+filename, function (err,data){
                res.end(data);
            });
        } else {
            res.end('file is not exists');
        }
    })
});

app.use(function( req, res){
    res.writeHead(404,{"Content-Type" : "text/plain"});
    res.end('404 ERROR');
});

http.createServer(app).listen(8080);
