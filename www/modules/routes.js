/**
 * Created by jbuisine on 06/02/17.
 */

var express = require('express');
var router = express.Router();
var utilities = require('./utilities');
const spawn = require('child_process').spawn;

const albumsPath = './views/albums/';
const solsPath = './../resources/solutions/';
const buildAlbumFile = './../utilities/buildAlbum.py';

router.get('/', function (req, res) {
    res.render('index', {
        page: "home",
        albums: utilities.getDirectories(albumsPath)
    });
});

router.get('/about', function (req, res) {
    res.render('index', {
        page: "about",
        albums: utilities.getDirectories(albumsPath)
    });
});

router.get('/contact', function (req, res) {
    res.render('index', {
        page: "contact",
        albums: utilities.getDirectories(albumsPath)
    });
});

router.get('/generateAlbum', function (req, res) {
    res.render('index', {
        page: "generateAlbum",
        albums: utilities.getDirectories(albumsPath),
        solutions: utilities.getFiles(solsPath)
    });
});

router.get('/album/:name', function (req, res) {

    /**
     * Get content file
     */
    var dataFile = utilities.readFileContent(albumsPath + req.params.name + "/info.txt")
    console.log(dataFile.substr(dataFile.lastIndexOf("/") + 1));

    var albums = utilities.getDirectories(albumsPath);

    if(albums.indexOf(req.params.name) !== -1){

        res.render('index', {
            page: "album",
            albumName: req.params.name,
            idPage: 0,
            albums: utilities.getDirectories(albumsPath),
            solutions: utilities.getFiles(solsPath)
        });
    }else{
        res.redirect('error');
    }
});

router.get('/album/:name/:id', function (req, res) {

    var albums = utilities.getDirectories(albumsPath);

    var album = req.params.name;
    var id = req.params.id;
    if(albums.indexOf(req.params.name) !== -1){

        utilities.filePathExists(albumsPath + album + '/page_' + id + '.ejs').then(function(exists) {

            if(exists){
                res.render('index', {
                    page: "album",
                    albumName: album,
                    idPage: id,
                    albums: albums,
                    solutions: utilities.getFiles(solsPath)
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

    var albums = utilities.getDirectories(albumsPath);

    if (albums.indexOf(album) !== -1) {


        const ls = spawn('python', [buildAlbumFile, solsPath + solutionFile, album]);

        /*
        ls.stdout.on('data', (data) => {
            console.log(`stdout: ${data}`);
        });

        ls.stderr.on('data', (data) => {
            console.log(`stderr: ${data}`);
        });*/

        ls.on('close', (code) => {
            console.log(`child process exited with code ${code}`);
            res.redirect('/album/' + album);
        });

    } else {
        res.redirect('/error');
    }
});

router.get("/error", function(req, res){
    res.render('index', {
        page: 'error',
        message: "Page not found",
        albums: utilities.getDirectories(albumsPath)
    });
});

module.exports = router;