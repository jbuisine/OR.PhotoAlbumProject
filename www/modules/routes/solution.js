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

router.get('/solution', function (req, res) {
    res.render('index', {
        page: "solution",
        albums: utilities.getDirectories(albumsPath),
        albumsType: utilities.getFiles(albumsTypePath)
    });
});


router.post('/solution-HC', function (req, res) {

    var solutionFile = req.body.solutionFile;
    var albumType = req.body.albumType;
    var criteria = req.body.criteria;
    var iteration = req.body.iterationAlgo;
    var permutation = req.body.permutation;

    console.log(criteria);

    res.send('success');
    /*
    var python = spawn('python', [buildAlbumFile, solsPath + solutionPath, albumType, album]);

    python.stdout.on('data', function (data) {
        io.sockets.emit('uploadProgress', data.toString());
        console.log('stdout: ' + data.toString());
    });

    python.stderr.on('data', function (data) {
        console.log('stderr: ' + data.toString());
    });

    python.on('close', function(code) {
        console.log('closing code: ' + code);
        res.redirect('/album/' + album);
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