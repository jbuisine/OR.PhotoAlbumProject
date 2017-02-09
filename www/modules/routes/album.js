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

router.get('/album/:name', function (req, res) {

    utilities.readFileContent(albumsPath + req.params.name + "/info.txt").then(function(dataFile){

        var currentSol = dataFile.substr(dataFile.lastIndexOf("/") + 1);

        var albums = utilities.getDirectories(albumsPath);

        var albumsType = utilities.getFiles(albumsTypePath);

        if(albums.indexOf(req.params.name) !== -1){

            res.render('index', {
                page: "album",
                albumName: req.params.name,
                idPage: 0,
                albums: utilities.getDirectories(albumsPath),
                albumsType: albumsType,
                solutions: utilities.getFiles(solsPath + "/" + albumsType[0].replace('.json', '')),
                currentSolution: currentSol
            });
        }else{
            res.redirect('error');
        }
    });
});

router.get('/album/:name/:id', function (req, res) {

    var albums = utilities.getDirectories(albumsPath);

    var album = req.params.name;
    var id = req.params.id;
    if(albums.indexOf(req.params.name) !== -1){

        utilities.filePathExists(albumsPath + album + '/page_' + id + '.ejs').then(function(exists) {

            if(exists){
                utilities.readFileContent(albumsPath + req.params.name + "/info.txt").then(function(dataFile) {

                    var currentSol = dataFile.substr(dataFile.lastIndexOf("/") + 1);
                    var albumsType = utilities.getFiles(albumsTypePath);
                    res.render('index', {
                        page: "album",
                        albumName: album,
                        idPage: id,
                        albums: albums,
                        albumsType: albumsType,
                        solutions: utilities.getFiles(solsPath + "/" + albumsType[0].replace('.json', '')),
                        currentSolution: currentSol
                    });
                });
            } else{
                res.redirect("/album/"+album);
            }
        }).catch(function(e) { throw e; });

    }else{
        res.redirect('/error');
    }
});

router.post('/generate-album', function (req, res) {

    var album = req.body.albumName;
    var solutionFile = req.body.solutionFile;
    var albumType= req.body.albumType;

    var albums = utilities.getDirectories(albumsPath);

    if (albums.indexOf(album) !== -1) {

        var solutionPath = solsPath + albumType.replace(".json", "") + "/" + solutionFile;
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
        });

    } else {
        res.redirect('/error');
    }
});

router.post('/load-solutions', function (req, res) {

    var albumType = req.body.albumType;

    var albumsType = utilities.getFiles(solsPath + "/" + albumType.replace('.json', ''))

    res.send(albumsType);
});

module.exports = router;
