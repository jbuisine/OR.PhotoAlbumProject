/**
 * Created by jbuisine on 09/02/17.
 */

var app = require('./../../app');
var io = app.io;

var express = require('express');
var router = express.Router();
var utilities = require('./../utilities');
const spawn = require('child_process').spawn;
var path = require('path')

const readline = require('readline');
const fs = require('fs-extra');
const rmdir = require('rimraf');
const multer  = require('multer');

const templatesPath = './views/templates/';

const solsPath = './../resources/solutions/';
const albumsTypePath = './../resources/data/';
const buildTemplateFile = './../utilities/buildAlbum.py';;
const buildTagPath = './../utilities/tag-clarifai.py';;
const buildInfoPath = './../utilities/extractInfo.py';

router.get('/templates/:name', function (req, res) {

    var template = req.params.name;

    var templates = utilities.getDirectories(templatesPath);

    if (templates.indexOf(template) !== -1) {

        utilities.filePathExists(templatesPath + template + '/info.txt').then(function (exists) {

            if(exists){
                utilities.readFileContent(templatesPath + template + "/info.txt").then(function (dataFile) {

                    console.log(dataFile);

                    var currentSol = "";

                    if(dataFile.indexOf("/") !== -1)
                        currentSol = dataFile.substr(dataFile.lastIndexOf("/") + 1);
                    else
                        currentSol = dataFile;

                    var albumsType = utilities.getFiles(albumsTypePath + template);

                    utilities.filePathExists(templatesPath + template + '/page_0.ejs').then(function (exists) {

                        if (exists) {
                            res.redirect("/templates/" + template + "/0");
                        }
                        else {
                            if(albumsType){
                                res.render('index', {
                                    page: "template",
                                    templateName: template,
                                    templates: utilities.getDirectories(templatesPath),
                                    albumsType: albumsType,
                                    solutions: utilities.getFiles(solsPath + template + "/" + albumsType[0].replace('.json', '')),
                                    currentSolution: currentSol
                                });
                            }else{
                                res.render('index', {
                                    page: "template",
                                    templateName: template,
                                    templates: utilities.getDirectories(templatesPath),
                                    currentSolution: currentSol
                                });
                            }
                        }
                    });

                });
            }else{
                console.log("go to generate");
                generateInfoFiles(template, res);
            }
        });
    } else {
        res.redirect('/error');
    }

});

router.get('/templates/:name/:id', function (req, res) {

    var templates = utilities.getDirectories(templatesPath);
    var template = req.params.name;
    var id = req.params.id;

    if(templates.indexOf(template) !== -1){

        utilities.filePathExists(templatesPath + template + '/page_' + id + '.ejs').then(function(exists) {

            if(exists){
                utilities.readFileContent(templatesPath + template + "/info.txt").then(function(dataFile) {

                    var currentSol = dataFile.substr(dataFile.lastIndexOf("/") + 1);
                    var albumsType = utilities.getFiles(albumsTypePath + template);
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
        }).catch(function(e) { console.log("Here");
        throw e; });

    }else{
        res.redirect('/error');
    }
});

router.post('/generate-album', function (req, res) {

    var template = req.body.templateName;
    var solutionFile = req.body.solutionFile;
    var albumType = req.body.albumType;
    var lineFile = req.body.selectSolutionsGenerate;

    var templates = utilities.getDirectories(templatesPath);

    if (templates.indexOf(template) !== -1) {

        var solutionPath = solsPath + template + "/" + albumType.replace('.json', '') + "/" + solutionFile;
        var python = spawn('python', [buildTemplateFile, solutionPath, albumType, template, lineFile]);

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

    var completePath = solsPath + template + "/" + albumType.replace('.json', '');
    var solutions = utilities.getFiles(completePath);

    res.send(solutions);
});

router.post('/load-solution-content', function (req, res) {
    var albumType = req.body.albumType;
    var template = req.body.templateName;
    var solutionFile = req.body.solutionFile;

    var solutionPath = solsPath + template + "/" + albumType.replace('.json', '') + "/" + solutionFile;

    var lineReader = readline.createInterface({
        input: fs.createReadStream(solutionPath)
    });

    var contentFile = [];

    lineReader.on('line', function (line) {

        contentFile.push(line.split(','));

    }).on('close', function(){
        res.send(contentFile);
    });
});

// Routes associated to the managing of templates
router.get('/manage-templates', function(req, res){
    res.render('index', {
        page: "manage-templates",
        templates: utilities.getDirectories(templatesPath)
    });
});

router.post('/create-template', function (req, res) {
    var dir = templatesPath + req.body.templateName + "/img";
    console.log("DIR ", dir);
    if (!fs.existsSync(dir)){
        fs.mkdirsSync(dir);
    }
    res.status('200');
    res.send('success');
});


router.post('/template-save-image', function (req, res) {

    var filename = "rIMG_1940.jpg";

    var storage = multer.diskStorage({
        destination: function (req, file, cb) {

            var templateName = req.body.templateName;
            var pathFolder = templatesPath + templateName + "/img";
            var files = utilities.getFiles(pathFolder);

            if(files){
                if(files.length > 0){
                    var number = files[files.length-1].replace( /^\D+/g, '').replace('.jpg','');
                    filename = 'rIMG_' + (parseInt(number)+1) + '.jpg';
                }
            }else{
                fs.mkdirsSync(pathFolder);
            }

            cb(null, pathFolder);
        },
        filename: function (req, file, cb) {
            cb(null, filename)
        }
    });

    var upload = multer({ storage: storage }).single('photo');

    //Upload file and update info about photo
    upload(req, res, function (err) {
        if(err)
            res.status(406);
        else
            res.status(200);

        res.send();

    })
});

router.post('/generate-template-file', function (req, res) {

    var template = req.body.templateName;

    var buildTag = spawn('python', [buildTagPath, templatesPath + template]);

    buildTag.stdout.on('data', function (data) {
        console.log('stdout: ' + data.toString());
    });

    buildTag.stderr.on('data', function (data) {
        console.log('stderr: ' + data.toString());
    });

    buildTag.on('close', function() {

        var buildInfoFile = spawn('python', [buildInfoPath, templatesPath + template]);

        buildInfoFile.stdout.on('data', function (data) {
            console.log('stdout: ' + data.toString());
        });

        buildInfoFile.stderr.on('data', function (data) {
            console.log('stderr: ' + data.toString());
        });

        buildInfoFile.on('close', function() {

            //Emit socket
            io.sockets.emit('templateGeneration', template);
        });
    });
});

router.post('/template-images-info', function (req, res) {
    var templateName    = req.body.templateName;
    var pathTemplateImg = templatesPath + templateName + "/img";

    //Return images of template if it has at least one
    utilities.filePathExists(pathTemplateImg).then(function (exists) {
       if(exists){
           res.header("application/json");
           res.status(200);
           res.send(utilities.getFiles(pathTemplateImg));

       } else {
           res.status(406);
           res.send("error");
       }
    });

});

router.post('/template-remove-image', function (req, res) {
    var templateName    = req.body.templateName;
    var photoName       = req.body.photoName;

    fs.unlink(templatesPath + templateName + "/img/" + photoName);
    res.status(200);
    res.send("success");
});

router.post('/template-remove', function (req, res) {
    var templateName    = req.body.templateName;

    rmdir(templatesPath + templateName, function (error) {
        if(error){
            res.status(406);
            res.send("error");
        }else{
            res.status(200);
            res.send("success");
        }

    });
});

function generateInfoFiles(template, res) {

    var fileInfoPath = templatesPath + template + '/info.txt';
    var buffer = new Buffer("No information about solution used for generation\n");

    utilities.filePathExists(fileInfoPath).then(function (exists){
        if(!exists){
            fs.open(fileInfoPath, 'w', function(err, fd) {
                if (err) {
                    throw 'error opening file: ' + err;
                }

                fs.write(fd, buffer, 0, buffer.length, null, function(err) {
                    if (err) throw 'error writing file: ' + err;

                    res.redirect('/templates/'+template);
                });
            });
        }
    });
}


module.exports = router;
