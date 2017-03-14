/**
 * Created by jbuisine on 06/02/17.
 */
var express = require('express');
var router = express.Router();
var utilities = require('./../utilities');

const spawn = require('child_process').spawn;
const templatesPath = './views/templates/';

router.get('/', function (req, res) {
    res.render('index', {
        page: "home",
        templates: utilities.getDirectories(templatesPath)
    });
});

router.get('/about', function (req, res) {
    res.render('index', {
        page: "about",
        templates: utilities.getDirectories(templatesPath)
    });
});

router.get('/contact', function (req, res) {
    res.render('index', {
        page: "contact",
        templates: utilities.getDirectories(templatesPath)
    });
});

router.get("/error", function(req, res){
    res.render('index', {
        page: 'error',
        message: "Page not found",
        templates: utilities.getDirectories(templatesPath)
    });
});

router.post("/restart_server", function (req, res) {
    var restart_cmd = spawn('sudo', ['reboot']);

    restart_cmd.on('close', function() {
        res.contentType('text/html');
        res.send("Server will restart in approximately 1 minute. It will compile and download new dependencies. ");
    });
});

router.post("/compile_scala", function (req, res) {

    var compile_cmd = spawn('bash', ['../run.sh','compile']);

    compile_cmd.stdout.on('data', function (data) {
       console.log(data.toString());
    });

    compile_cmd.stderr.on('data', function (data) {
        console.log(data.toString());
    });

    compile_cmd.on('close', function() {
        res.contentType('text/html');
        res.send("Finished to compile scala code.");
    });
});

router.post("/update_git", function (req, res) {
    var git_cmd = spawn('git', ['pull', 'origin', 'master']);

    git_cmd.on('close', function() {
        res.contentType('text/html');
        res.send("Project now up-to-date from master !");
    });
});



module.exports = router;