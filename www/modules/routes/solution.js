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
const albumsTypePath = './../resources/data/';
const classPathUtils = './../utilities/compile:./../utilities/lib/json-simple-1.1.1.jar';

router.get('/solution/:template', function (req, res) {

    var template = req.params.template;
    var templates = utilities.getDirectories(templatesPath);

    utilities.filePathExists(templatesPath + template).then(function(exists) {

        if(exists){
            res.render('index', {
                page: "solution",
                templateName: template,
                templates: templates,
                albumsType: utilities.getFiles(albumsTypePath + template),
                templateSize:  utilities.getFiles(templatesPath + template + "/img").length
            });
        }else{
            res.redirect('/error');
        }
    });


});

router.post('/create-solution', function (req, res) {

    console.log(req.body);

    var solutionScript = spawn('scala', ['-cp', classPathUtils, 'MainWebApp', JSON.stringify(req.body)]);
    var percent = 0;

    solutionScript.stdout.on('data', function (data) {

        //Send only if data contains % and only if percent changed
        if(data.toString().indexOf('%') !== -1) {
            var formattedPercent = parseInt(data.toString().split('>')[1].replace('%', ''));
            console.log(formattedPercent.toString());

            if(percent != formattedPercent){
                percent = formattedPercent;
                io.sockets.emit('generationProgress', {solFile: req.body.solutionFile, percent: formattedPercent.toString()});
            }
        }
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