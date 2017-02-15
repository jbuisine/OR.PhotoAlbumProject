/**
 * Created by jbuisine on 09/02/17.
 */

var app = require('./../../app');
var io = app.io;

var express = require('express');
var router = express.Router();
var utilities = require('./../utilities');
const spawn = require('child_process').spawn;

const albumsPath = './views/albums/';

const solsPath = './../resources/solutions/';
const albumsTypePath = './../resources/data/albums-type/';
const buildAlbumFile = './../utilities/buildAlbum.py';
const classPathUtils = './../utilities/compile:./../utilities/lib/json-simple-1.1.1.jar';

router.get('/solution', function (req, res) {
    res.render('index', {
        page: "solution",
        albums: utilities.getDirectories(albumsPath),
        albumsType: utilities.getFiles(albumsTypePath)
    });
});


router.post('/create-solution', function (req, res) {

    console.log(req.body)


    var solutionScript = spawn('scala', ['-cp', classPathUtils, 'MainWebApp', JSON.stringify(req.body)]);

    solutionScript.stdout.on('data', function (data) {
        io.sockets.emit('uploadProgress', data.toString());
        console.log('stdout: ' + data.toString());
    });

    solutionScript.stderr.on('data', function (data) {
        console.log('stderr: ' + data.toString());
    });

    solutionScript.on('close', function(code) {
        console.log('closing code: ' + code);
        res.contentType('text/html');
        res.send('Finished');
    });

    /*exec('scala -cp ' + classPathUtils + ' MainWebApp ' + JSON.stringify(req.body), (error, stdout, stderr) => {
      if (error) {
        console.log('stderr: ' + error.toString());
        res.contentType('text/html');
        res.send("error");
      }

      if(stdout){
        io.sockets.emit('uploadProgress', stdout.toString());
        console.log('stdout: ' + stdout.toString());
      }
      console.log(`stdout: ${stdout}`);
      console.log(`stderr: ${stderr}`);
    });*/
});

router.post('/solution-ILS', function (req, res) {

});

router.post('/solution-EA', function (req, res) {

});

router.post('/solution-PLS', function (req, res) {

});

router.post('/solution-MOEAD', function (req, res) {

});

module.exports = router;