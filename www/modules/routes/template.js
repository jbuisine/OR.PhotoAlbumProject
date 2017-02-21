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

const solsPath = './../resources/solutions/';
const albumsTypePath = './../resources/data/albums-type/';
const buildTemplateFile = './../utilities/buildAlbum.py';

router.get('/templates/:name', function (req, res) {

    utilities.readFileContent(templatesPath + req.params.name + "/info.txt").then(function(dataFile){

        var currentSol = dataFile.substr(dataFile.lastIndexOf("/") + 1);

        var templates = utilities.getDirectories(templatesPath);

        var albumsType = utilities.getFiles(albumsTypePath);

        var template = req.params.name;

        if(templates.indexOf(req.params.name) !== -1){

            res.render('index', {
                page: "template",
                templateName: template,
                idPage: 0,
                templates: utilities.getDirectories(templatesPath),
                albumsType: albumsType,
                solutions: utilities.getFiles(solsPath + template + "/" + albumsType[0].replace('.json', '')),
                currentSolution: currentSol
            });
        }else{
            res.redirect('error');
        }
    });
});

router.get('/templates/:name/:id', function (req, res) {

    var templates = utilities.getDirectories(templatesPath);

    var template = req.params.name;
    var id = req.params.id;
    if(templates.indexOf(req.params.name) !== -1){

        utilities.filePathExists(templatesPath + template + '/page_' + id + '.ejs').then(function(exists) {

            if(exists){
                utilities.readFileContent(templatesPath + template + "/info.txt").then(function(dataFile) {

                    var currentSol = dataFile.substr(dataFile.lastIndexOf("/") + 1);
                    var albumsType = utilities.getFiles(albumsTypePath);
                    res.render('index', {
                        page: "template",
                        templateName: template,
                        idPage: id,
                        templates: templates,
                        albumsType: albumsType,
                        solutions: utilities.getFiles(solsPath + template + "/" + albumsType[0].replace('.json', '')),
                        currentSolution: currentSol
                    });
                });
            } else{
                res.redirect("/templates/"+template);
            }
        }).catch(function(e) { throw e; });

    }else{
        res.redirect('/error');
    }
});

router.post('/generate-album', function (req, res) {

    var template = req.body.templateName;
    var solutionFile = req.body.solutionFile;
    var albumType = req.body.albumType;

    var templates = utilities.getDirectories(templatesPath);

    if (templates.indexOf(template) !== -1) {

        var solutionPath = solsPath + template + "/" + albumType.replace('.json', '') + "/" + solutionFile;
        var python = spawn('python', [buildTemplateFile, solutionPath, albumType, template]);

        python.stdout.on('data', function (data) {
            io.sockets.emit('uploadProgress', data.toString());
            console.log('stdout: ' + data.toString());
        });

        python.stderr.on('data', function (data) {
            console.log('stderr: ' + data.toString());
        });

        python.on('close', function() {
            res.redirect('/templates/' + template);
        });

    } else {
        res.redirect('/error');
    }
});

router.post('/load-solutions', function (req, res) {

    var albumType = req.body.albumType;
    var template = req.body.templateName;

    var solutions = utilities.getFiles(solsPath + template + "/" + albumType.replace('.json', ''));

    var lineReader = require('readline').createInterface({
      input: require('fs').createReadStream((solsPath + template + "/" + albumType.replace('.json', '') + "/" + solutions[1]))
    });

    lineReader.on('line', function (line) {
      console.log('Line from file:', line);
    });

    res.send(solutions);
});

module.exports = router;
