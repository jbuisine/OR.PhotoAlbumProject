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
const templatesTypePath = './../resources/data/templates-type/';
const buildTemplateFile = './../utilities/buildAlbum.py';

router.get('/templates/:name', function (req, res) {

    utilities.readFileContent(templatesPath + req.params.name + "/info.txt").then(function(dataFile){

        var currentSol = dataFile.substr(dataFile.lastIndexOf("/") + 1);

        var templates = utilities.getDirectories(templatesPath);

        var templatesType = utilities.getFiles(templatesTypePath);

        if(templates.indexOf(req.params.name) !== -1){

            res.render('index', {
                page: "template",
                templateName: req.params.name,
                idPage: 0,
                templates: utilities.getDirectories(templatesPath),
                templatesType: templatesType,
                solutions: utilities.getFiles(solsPath + "/" + templatesType[0].replace('.json', '')),
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
                utilities.readFileContent(templatesPath + req.params.name + "/info.txt").then(function(dataFile) {

                    var currentSol = dataFile.substr(dataFile.lastIndexOf("/") + 1);
                    var templatesType = utilities.getFiles(templatesTypePath);
                    res.render('index', {
                        page: "template",
                        templateName: template,
                        idPage: id,
                        templates: templates,
                        templatesType: templatesType,
                        solutions: utilities.getFiles(solsPath + "/" + templatesType[0].replace('.json', '')),
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
    var templateType= req.body.templateType;

    var templates = utilities.getDirectories(templatesPath);

    if (templates.indexOf(template) !== -1) {

        var solutionPath = solsPath + templateType.replace(".json", "") + "/" + solutionFile;
        var python = spawn('python', [buildTemplateFile, solutionPath, templateType, template]);

        python.stdout.on('data', function (data) {
            io.sockets.emit('uploadProgress', data.toString());
            console.log('stdout: ' + data.toString());
        });

        python.stderr.on('data', function (data) {
            console.log('stderr: ' + data.toString());
        });

        python.on('close', function(code) {
            console.log('closing code: ' + code);
            res.redirect('/templates/' + template);
        });

    } else {
        res.redirect('/error');
    }
});

router.post('/load-solutions', function (req, res) {

    var templateType = req.body.templateType;

    var templatesType = utilities.getFiles(solsPath + "/" + templateType.replace('.json', ''));

    res.send(templatesType);
});

module.exports = router;
