/**
 * Created by jbuisine on 09/02/17.
 */

var app = require('./../../app');
var io = app.io;

var express = require('express');
var router = express.Router();
var utilities = require('./../utilities');
const spawn = require('child_process').spawn;

const templatesPath = './views/templates/';
const albumsTypePath = './../resources/data/albums-type/';
const classPathUtils = './../utilities/compile:./../utilities/lib/json-simple-1.1.1.jar';

router.get('/solution', function (req, res) {
    res.render('index', {
        page: "solution",
        templates: utilities.getDirectories(templatesPath),
        albumsType: utilities.getFiles(albumsTypePath)
    });
});


router.post('/create-solution', function (req, res) {

    req.body.templateSize = utilities.getFiles(templatesPath + req.body.templateName + "/img").length;

    console.log(req.body);

    var solutionScript = spawn('scala', ['-cp', classPathUtils, 'MainWebApp', JSON.stringify(req.body)]);

    solutionScript.stdout.on('data', function (data) {
        io.sockets.emit('generationProgress', { solFile: req.body.solutionFile, percent: data.toString() });
        console.log('stdout: ' + data.toString());
    });

    solutionScript.stderr.on('data', function (data) {
        console.log('stderr: ' + data.toString());
    });

    solutionScript.on('close', function() {
        io.sockets.emit('generationFinished', { solFile: req.body.solutionFile });
        res.contentType('text/html');
        res.send('Success');
    });
});

module.exports = router;